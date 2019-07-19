/*
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.core.http.impl;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.StreamPriority;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.vertx.core.http.Http2Settings.*;

/**
 * Various http utils.
 * <p> update normalizePath,decodeUnreserved,validateHeader, validateHeaderValue</p>
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public final class HttpUtils {

    protected static final boolean VALIDATE_HEADER = Boolean.valueOf(System.getProperty("http.header.validate", "true"));

    static final StreamPriority DEFAULT_STREAM_PRIORITY = new StreamPriority() {
        @Override
        public StreamPriority setWeight(short weight) {
            throw new UnsupportedOperationException("Unmodifiable stream priority");
        }

        @Override
        public StreamPriority setDependency(int dependency) {
            throw new UnsupportedOperationException("Unmodifiable stream priority");
        }

        @Override
        public StreamPriority setExclusive(boolean exclusive) {
            throw new UnsupportedOperationException("Unmodifiable stream priority");
        }
    };


    private HttpUtils() {
    }

    private static int indexOfSlash(CharSequence str, int start) {
        for (int i = start; i < str.length(); i++) {
            if (str.charAt(i) == '/') {
                return i;
            }
        }

        return -1;
    }

    private static boolean matches(CharSequence path, int start, String what) {
        return matches(path, start, what, false);
    }

    private static boolean matches(CharSequence path, int start, String what, boolean exact) {
        if (exact) {
            if (path.length() - start != what.length()) {
                return false;
            }
        }

        if (path.length() - start >= what.length()) {
            for (int i = 0; i < what.length(); i++) {
                if (path.charAt(start + i) != what.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Normalizes a path as per <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4">rfc3986</a>.
     *
     * There are 2 extra transformations that are not part of the spec but kept for backwards compatibility:
     *
     * double slash // will be converted to single slash and the path will always start with slash.
     *
     * Null paths are not normalized as nothing can be said about them.
     *
     * @param pathname raw path
     * @return normalized path
     */
    public static String normalize(String pathname) {
        if (pathname == null) {
            return null;
        }

        // add trailing slash if not set
        if (pathname.length() == 0) {
            return "/";
        }

        StringBuilder ibuf = new StringBuilder(pathname.length() + 1);

        // Not standard!!!
        if (pathname.charAt(0) != '/') {
            ibuf.append('/');
        }

        ibuf.append(pathname);
        int i = 0;

        while (i < ibuf.length()) {
            // decode unreserved chars described in
            // http://tools.ietf.org/html/rfc3986#section-2.4
            if (ibuf.charAt(i) == '%') {
                decodeUnreserved(ibuf, i);
            }

            i++;
        }

        // remove dots as described in
        // http://tools.ietf.org/html/rfc3986#section-5.2.4
        return removeDots(ibuf);
    }

    private static void decodeUnreserved(StringBuilder path, int start) {
        if (start + 3 <= path.length()) {
            // these are latin chars so there is no danger of falling into some special unicode char that requires more
            // than 1 byte
            final String escapeSequence = path.substring(start + 1, start + 3);
            int unescaped;
            try {
                unescaped = Integer.parseInt(escapeSequence, 16);
                if (unescaped < 0) {
                    throw new IllegalArgumentException("Invalid escape sequence: %" + escapeSequence);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid escape sequence: %" + escapeSequence);
            }
            // validate if the octet is within the allowed ranges
            if (
                // ALPHA
                    (unescaped >= 0x41 && unescaped <= 0x5A) ||
                            (unescaped >= 0x61 && unescaped <= 0x7A) ||
                            // DIGIT
                            (unescaped >= 0x30 && unescaped <= 0x39) ||
                            // HYPHEN
                            (unescaped == 0x2D) ||
                            // PERIOD
                            (unescaped == 0x2E) ||
                            // UNDERSCORE
                            (unescaped == 0x5F) ||
                            // TILDE
                            (unescaped == 0x7E)) {

                path.setCharAt(start, (char) unescaped);
                path.delete(start + 1, start + 3);
            }
        } else {
            throw new IllegalArgumentException("Invalid position for escape character: " + start);
        }
    }

    /**
     * Normalizes a path as per <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4">rfc3986</a>.
     * <p>
     * There are 2 extra transformations that are not part of the spec but kept for backwards compatibility:
     * <p>
     * double slash // will be converted to single slash and the path will always start with slash.
     * <p>
     * 对性能进行了优化，采用链表方式切片，另外方法内敛，减少方法调用，原有的方法名称改为normalize
     *
     * @param pathname raw path
     * @return normalized path
     */
    public static String normalizePath(final String pathname) {
        // add trailing slash if not set
        if (pathname == null) {
            return null;
        } else if (pathname.isEmpty()) {
            return "/";
        }
        //不需要变化
        boolean flag = false;
        //根节点
        Slice root = null;
        //字符串不以'/'开头
        if (pathname.charAt(0) != '/') {
            root = new Slice(-1, -1, '/', true, 1, null);
            flag = true;
        }
        int length = pathname.length();
        char ch;
        Slice slice = new Slice(0, -1, root);
        root = root == null ? slice : root;
        boolean created;
        //遍历字符，按照'/'切片
        for (int i = 0; i < length; ) {
            ch = pathname.charAt(i);
            created = false;
            if (ch == '/') {
                if (i > 0) {
                    //不是第一个字符，则作为结束符号
                    slice.end = i;
                    slice.partial = false;
                    flag = flag || slice.dots != -1;
                    //创建下一个切片
                    slice = new Slice(i, -1, slice);
                }
            } else if (ch == '%') {
                //'/'对应的"%2E"不会进行转义
                ch = decodeUnreserved(pathname, i);
                if (ch > 0) {
                    if (i > 0) {
                        //不是第一个字符，则当前切片结束
                        slice.end = i;
                        slice.partial = true;
                        //创建转义字符切片
                        slice = new Slice(i, i + 3, slice);
                    } else {
                        //第一个字符
                        slice.end = i + 3;
                    }
                    slice.value = ch;
                    if (slice.end < length) {
                        slice.partial = true;
                        //需要创建下一个切片
                        created = true;
                    }
                    flag = true;
                    i += 2;
                }
            }
            if (slice.dots != -1) {
                if ((ch == '/' && slice.dots == 0) || ch == '.' && (slice.dots == 1 || slice.dots == 2)) {
                    slice.dots++;
                } else {
                    slice.dots = -1;
                }
            }
            if (created) {
                //创建下一个切片
                slice = new Slice(slice.end, -1, slice);
            }
            i++;
        }
        if (slice != null && slice.end == -1) {
            slice.partial = false;
            slice.end = length;
            flag = flag || slice.dots != -1;
        }
        if (!flag) {
            return pathname;
        }
        //不需要变化
        flag = false;
        slice = root;
        Slice prev;
        while (slice != null) {
            if (!flag && slice.value > 0) {
                flag = true;
            }
            if (!slice.partial) {
                switch (slice.dots) {
                    case 1:
                        //处理'/'
                        if (slice.prev != null && slice.prev.dots == 1) {
                            //处理'//'，把当前节点删除，上一个'/'保留，不涉及root的变化
                            slice.remove();
                            flag = true;
                        }
                        break;
                    case 2:
                        //处理'/.'，删除当前节点
                        prev = slice.removePath();
                        if (prev != null) {
                            //有父节点
                            if (slice.next == null) {
                                //当前节点是最后一个节点，则保留最后的'/'
                                prev.next = new Slice(-1, -1, '/');
                            }
                        } else {
                            root = slice.next;
                        }
                        flag = true;
                        break;
                    case 3:
                        //处理'/..'，删除当前节点及前一个节点
                        prev = slice.removePath();
                        prev = prev == null ? null : prev.removePath();
                        if (prev != null) {
                            //有父节点
                            if (slice.next == null) {
                                //当前节点是最后一个节点，则保留最后的'/'
                                prev.next = new Slice(-1, -1, '/');
                            }
                        } else {
                            root = slice.next;
                        }
                        flag = true;
                        break;
                }
            }
            slice = slice.next;
        }
        //判断是否发生了变化
        if (!flag) {
            return pathname;
        } else if (root == null) {
            return "/";
        }
        //构造缓冲区，拼接字符串
        StringBuilder builder = new StringBuilder(length);
        while (root != null) {
            if (root.value > 0) {
                builder.append(root.value);
            } else {
                builder.append(pathname, root.start, root.end);
            }
            root = root.next;
        }
        return builder.toString();
    }

    protected static char decodeUnreserved(final CharSequence sequence, final int start) {
        if (start + 3 <= sequence.length()) {
            // these are latin chars so there is no danger of falling into some special unicode char that requires more
            // than 1 byte
            final CharSequence escapeSequence = sequence.subSequence(start + 1, start + 3);
            int unescaped;
            try {
                unescaped = Integer.parseInt(escapeSequence.toString(), 16);
                if (unescaped < 0) {
                    throw new IllegalArgumentException("Invalid escape sequence: %" + escapeSequence);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid escape sequence: %" + escapeSequence);
            }
            // validate if the octet is within the allowed ranges
            if (
                // ALPHA
                    (unescaped >= 0x41 && unescaped <= 0x5A) ||
                            (unescaped >= 0x61 && unescaped <= 0x7A) ||
                            // DIGIT
                            (unescaped >= 0x30 && unescaped <= 0x39) ||
                            // HYPHEN
                            (unescaped == 0x2D) ||
                            // PERIOD
                            (unescaped == 0x2E) ||
                            // UNDERSCORE
                            (unescaped == 0x5F) ||
                            // TILDE
                            (unescaped == 0x7E)) {
                return (char) unescaped;
            }
            return 0;
        } else {
            throw new IllegalArgumentException("Invalid position for escape character: " + start);
        }
    }

    /**
     * Removed dots as per <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4">rfc3986</a>.
     * <p>
     * There is 1 extra transformation that are not part of the spec but kept for backwards compatibility:
     * <p>
     * double slash // will be converted to single slash.
     *
     * @param path raw path
     * @return normalized path
     */
    public static String removeDots(CharSequence path) {

        if (path == null) {
            return null;
        }

        final StringBuilder obuf = new StringBuilder(path.length());

        int i = 0;
        while (i < path.length()) {
            // remove dots as described in
            // http://tools.ietf.org/html/rfc3986#section-5.2.4
            if (matches(path, i, "./")) {
                i += 2;
            } else if (matches(path, i, "../")) {
                i += 3;
            } else if (matches(path, i, "/./")) {
                // preserve last slash
                i += 2;
            } else if (matches(path, i, "/.", true)) {
                path = "/";
                i = 0;
            } else if (matches(path, i, "/../")) {
                // preserve last slash
                i += 3;
                int pos = obuf.lastIndexOf("/");
                if (pos != -1) {
                    obuf.delete(pos, obuf.length());
                }
            } else if (matches(path, i, "/..", true)) {
                path = "/";
                i = 0;
                int pos = obuf.lastIndexOf("/");
                if (pos != -1) {
                    obuf.delete(pos, obuf.length());
                }
            } else if (matches(path, i, ".", true) || matches(path, i, "..", true)) {
                break;
            } else {
                if (path.charAt(i) == '/') {
                    i++;
                    // Not standard!!!
                    // but common // -> /
                    if (obuf.length() == 0 || obuf.charAt(obuf.length() - 1) != '/') {
                        obuf.append('/');
                    }
                }
                int pos = indexOfSlash(path, i);
                if (pos != -1) {
                    obuf.append(path, i, pos);
                    i = pos;
                } else {
                    obuf.append(path, i, path.length());
                    break;
                }
            }
        }

        return obuf.toString();
    }

    /**
     * Resolve an URI reference as per <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4">rfc3986</a>
     */
    public static URI resolveURIReference(String base, String ref) throws URISyntaxException {
        return resolveURIReference(URI.create(base), ref);
    }

    /**
     * Resolve an URI reference as per <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4">rfc3986</a>
     */
    public static URI resolveURIReference(URI base, String ref) throws URISyntaxException {
        URI _ref = URI.create(ref);
        String scheme;
        String authority;
        String path;
        String query;
        if (_ref.getScheme() != null) {
            scheme = _ref.getScheme();
            authority = _ref.getAuthority();
            path = removeDots(_ref.getPath());
            query = _ref.getRawQuery();
        } else {
            if (_ref.getAuthority() != null) {
                authority = _ref.getAuthority();
                path = _ref.getPath();
                query = _ref.getRawQuery();
            } else {
                if (_ref.getPath().length() == 0) {
                    path = base.getPath();
                    if (_ref.getRawQuery() != null) {
                        query = _ref.getRawQuery();
                    } else {
                        query = base.getRawQuery();
                    }
                } else {
                    if (_ref.getPath().startsWith("/")) {
                        path = removeDots(_ref.getPath());
                    } else {
                        // Merge paths
                        String mergedPath;
                        String basePath = base.getPath();
                        if (base.getAuthority() != null && basePath.length() == 0) {
                            mergedPath = "/" + _ref.getPath();
                        } else {
                            int index = basePath.lastIndexOf('/');
                            if (index > -1) {
                                mergedPath = basePath.substring(0, index + 1) + _ref.getPath();
                            } else {
                                mergedPath = _ref.getPath();
                            }
                        }
                        path = removeDots(mergedPath);
                    }
                    query = _ref.getRawQuery();
                }
                authority = base.getAuthority();
            }
            scheme = base.getScheme();
        }
        return new URI(scheme, authority, path, query, _ref.getFragment());
    }

    /**
     * Extract the path out of the uri.
     */
    static String parsePath(String uri) {
        int i;
        if (uri.charAt(0) == '/') {
            i = 0;
        } else {
            i = uri.indexOf("://");
            if (i == -1) {
                i = 0;
            } else {
                i = uri.indexOf('/', i + 3);
                if (i == -1) {
                    // contains no /
                    return "/";
                }
            }
        }

        int queryStart = uri.indexOf('?', i);
        if (queryStart == -1) {
            queryStart = uri.length();
        }
        return uri.substring(i, queryStart);
    }

    /**
     * Extract the query out of a uri or returns {@code null} if no query was found.
     */
    static String parseQuery(String uri) {
        int i = uri.indexOf('?');
        if (i == -1) {
            return null;
        } else {
            return uri.substring(i + 1, uri.length());
        }
    }

    static String absoluteURI(String serverOrigin, HttpServerRequest req) throws URISyntaxException {
        String absoluteURI;
        URI uri = new URI(req.uri());
        String scheme = uri.getScheme();
        if (scheme != null && (scheme.equals("http") || scheme.equals("https"))) {
            absoluteURI = uri.toString();
        } else {
            String host = req.host();
            if (host != null) {
                absoluteURI = req.scheme() + "://" + host + uri;
            } else {
                // Fall back to the server origin
                absoluteURI = serverOrigin + uri;
            }
        }
        return absoluteURI;
    }

    static MultiMap params(String uri) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> prms = queryStringDecoder.parameters();
        MultiMap params = new CaseInsensitiveHeaders();
        if (!prms.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : prms.entrySet()) {
                params.add(entry.getKey(), entry.getValue());
            }
        }
        return params;
    }

    public static void fromVertxInitialSettings(boolean server, io.vertx.core.http.Http2Settings vertxSettings, Http2Settings nettySettings) {
        if (vertxSettings != null) {
            if (!server && vertxSettings.isPushEnabled() != DEFAULT_ENABLE_PUSH) {
                nettySettings.pushEnabled(vertxSettings.isPushEnabled());
            }
            if (vertxSettings.getHeaderTableSize() != DEFAULT_HEADER_TABLE_SIZE) {
                nettySettings.put('\u0001', (Long) vertxSettings.getHeaderTableSize());
            }
            if (vertxSettings.getInitialWindowSize() != DEFAULT_INITIAL_WINDOW_SIZE) {
                nettySettings.initialWindowSize(vertxSettings.getInitialWindowSize());
            }
            if (vertxSettings.getMaxConcurrentStreams() != DEFAULT_MAX_CONCURRENT_STREAMS) {
                nettySettings.maxConcurrentStreams(vertxSettings.getMaxConcurrentStreams());
            }
            if (vertxSettings.getMaxFrameSize() != DEFAULT_MAX_FRAME_SIZE) {
                nettySettings.maxFrameSize(vertxSettings.getMaxFrameSize());
            }
            if (vertxSettings.getMaxHeaderListSize() != DEFAULT_MAX_HEADER_LIST_SIZE) {
                nettySettings.maxHeaderListSize(vertxSettings.getMaxHeaderListSize());
            }
            Map<Integer, Long> extraSettings = vertxSettings.getExtraSettings();
            if (extraSettings != null) {
                extraSettings.forEach((code, setting) -> {
                    nettySettings.put((char) (int) code, setting);
                });
            }
        }
    }

    public static Http2Settings fromVertxSettings(io.vertx.core.http.Http2Settings settings) {
        Http2Settings converted = new Http2Settings();
        converted.pushEnabled(settings.isPushEnabled());
        converted.maxFrameSize(settings.getMaxFrameSize());
        converted.initialWindowSize(settings.getInitialWindowSize());
        converted.headerTableSize(settings.getHeaderTableSize());
        converted.maxConcurrentStreams(settings.getMaxConcurrentStreams());
        converted.maxHeaderListSize(settings.getMaxHeaderListSize());
        if (settings.getExtraSettings() != null) {
            settings.getExtraSettings().forEach((key, value) -> {
                converted.put((char) (int) key, value);
            });
        }
        return converted;
    }

    public static io.vertx.core.http.Http2Settings toVertxSettings(Http2Settings settings) {
        io.vertx.core.http.Http2Settings converted = new io.vertx.core.http.Http2Settings();
        Boolean pushEnabled = settings.pushEnabled();
        if (pushEnabled != null) {
            converted.setPushEnabled(pushEnabled);
        }
        Long maxConcurrentStreams = settings.maxConcurrentStreams();
        if (maxConcurrentStreams != null) {
            converted.setMaxConcurrentStreams(maxConcurrentStreams);
        }
        Long maxHeaderListSize = settings.maxHeaderListSize();
        if (maxHeaderListSize != null) {
            converted.setMaxHeaderListSize(maxHeaderListSize);
        }
        Integer maxFrameSize = settings.maxFrameSize();
        if (maxFrameSize != null) {
            converted.setMaxFrameSize(maxFrameSize);
        }
        Integer initialWindowSize = settings.initialWindowSize();
        if (initialWindowSize != null) {
            converted.setInitialWindowSize(initialWindowSize);
        }
        Long headerTableSize = settings.headerTableSize();
        if (headerTableSize != null) {
            converted.setHeaderTableSize(headerTableSize);
        }
        settings.forEach((key, value) -> {
            if (key > 6) {
                converted.set(key, value);
            }
        });
        return converted;
    }

    static Http2Settings decodeSettings(String base64Settings) {
        try {
            Http2Settings settings = new Http2Settings();
            Buffer buffer = Buffer.buffer(Base64.getUrlDecoder().decode(base64Settings));
            int pos = 0;
            int len = buffer.length();
            while (pos < len) {
                int i = buffer.getUnsignedShort(pos);
                pos += 2;
                long j = buffer.getUnsignedInt(pos);
                pos += 4;
                settings.put((char) i, (Long) j);
            }
            return settings;
        } catch (Exception ignore) {
        }
        return null;
    }

    public static String encodeSettings(io.vertx.core.http.Http2Settings settings) {
        Buffer buffer = Buffer.buffer();
        fromVertxSettings(settings).forEach((c, l) -> {
            buffer.appendUnsignedShort(c);
            buffer.appendUnsignedInt(l);
        });
        return Base64.getUrlEncoder().encodeToString(buffer.getBytes());
    }

    public static ByteBuf generateWSCloseFrameByteBuf(short statusCode, String reason) {
        if (reason != null) {
            return Unpooled.copiedBuffer(
                    Unpooled.copyShort(statusCode), // First two bytes are reserved for status code
                    Unpooled.copiedBuffer(reason, Charset.forName("UTF-8"))
            );
        } else {
            return Unpooled.copyShort(statusCode);
        }
    }

    static void sendError(Channel ch, HttpResponseStatus status, CharSequence err) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, status);
        if (status.code() == METHOD_NOT_ALLOWED.code()) {
            // SockJS requires this
            resp.headers().set(io.vertx.core.http.HttpHeaders.ALLOW, io.vertx.core.http.HttpHeaders.GET);
        }
        if (err != null) {
            resp.content().writeBytes(err.toString().getBytes(CharsetUtil.UTF_8));
            HttpUtil.setContentLength(resp, err.length());
        } else {
            HttpUtil.setContentLength(resp, 0);
        }
        ch.writeAndFlush(resp);
    }

    static String getWebSocketLocation(HttpRequest req, boolean ssl) throws Exception {
        String prefix;
        if (ssl) {
            prefix = "ws://";
        } else {
            prefix = "wss://";
        }
        URI uri = new URI(req.uri());
        String path = uri.getRawPath();
        String loc = prefix + req.headers().get(HttpHeaderNames.HOST) + path;
        String query = uri.getRawQuery();
        if (query != null) {
            loc += "?" + query;
        }
        return loc;
    }

    private static class CustomCompressor extends HttpContentCompressor {
        @Override
        public ZlibWrapper determineWrapper(String acceptEncoding) {
            return super.determineWrapper(acceptEncoding);
        }
    }

    private static final CustomCompressor compressor = new CustomCompressor();

    static String determineContentEncoding(Http2Headers headers) {
        String acceptEncoding = headers.get(HttpHeaderNames.ACCEPT_ENCODING) != null ? headers.get(HttpHeaderNames.ACCEPT_ENCODING).toString() : null;
        if (acceptEncoding != null) {
            ZlibWrapper wrapper = compressor.determineWrapper(acceptEncoding);
            if (wrapper != null) {
                switch (wrapper) {
                    case GZIP:
                        return "gzip";
                    case ZLIB:
                        return "deflate";
                }
            }
        }
        return null;
    }

    static HttpMethod toNettyHttpMethod(io.vertx.core.http.HttpMethod method, String rawMethod) {
        switch (method) {
            case CONNECT: {
                return HttpMethod.CONNECT;
            }
            case GET: {
                return HttpMethod.GET;
            }
            case PUT: {
                return HttpMethod.PUT;
            }
            case POST: {
                return HttpMethod.POST;
            }
            case DELETE: {
                return HttpMethod.DELETE;
            }
            case HEAD: {
                return HttpMethod.HEAD;
            }
            case OPTIONS: {
                return HttpMethod.OPTIONS;
            }
            case TRACE: {
                return HttpMethod.TRACE;
            }
            case PATCH: {
                return HttpMethod.PATCH;
            }
            default: {
                return HttpMethod.valueOf(rawMethod);
            }
        }
    }

    static HttpVersion toNettyHttpVersion(io.vertx.core.http.HttpVersion version) {
        switch (version) {
            case HTTP_1_0: {
                return HttpVersion.HTTP_1_0;
            }
            case HTTP_1_1: {
                return HttpVersion.HTTP_1_1;
            }
            default:
                throw new IllegalArgumentException("Unsupported HTTP version: " + version);
        }
    }

    static io.vertx.core.http.HttpMethod toVertxMethod(String method) {
        try {
            return io.vertx.core.http.HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            return io.vertx.core.http.HttpMethod.OTHER;
        }
    }

    private static final AsciiString TIMEOUT_EQ = AsciiString.of("timeout=");

    public static int parseKeepAliveHeaderTimeout(CharSequence value) {
        int len = value.length();
        int pos = 0;
        while (pos < len) {
            int idx = AsciiString.indexOf(value, ',', pos);
            int next;
            if (idx == -1) {
                idx = next = len;
            } else {
                next = idx + 1;
            }
            while (pos < idx && value.charAt(pos) == ' ') {
                pos++;
            }
            int to = idx;
            while (to > pos && value.charAt(to - 1) == ' ') {
                to--;
            }
            if (AsciiString.regionMatches(value, true, pos, TIMEOUT_EQ, 0, TIMEOUT_EQ.length())) {
                pos += TIMEOUT_EQ.length();
                if (pos < to) {
                    int ret = 0;
                    while (pos < to) {
                        int ch = value.charAt(pos++);
                        if (ch >= '0' && ch < '9') {
                            ret = ret * 10 + (ch - '0');
                        } else {
                            ret = -1;
                            break;
                        }
                    }
                    if (ret > -1) {
                        return ret;
                    }
                }
            }
            pos = next;
        }
        return -1;
    }

    private static final Consumer<CharSequence> HEADER_VALUE_VALIDATOR = HttpUtils::validateHeaderValue;

    public static void validateHeader(CharSequence name, CharSequence value) {
        if (VALIDATE_HEADER) {
            validateHeaderName(name);
            validateHeaderValue(value);
        }
    }

    public static void validateHeader(CharSequence name, Iterable<? extends CharSequence> values) {
        if (VALIDATE_HEADER) {
            validateHeaderName(name);
            values.forEach(HEADER_VALUE_VALIDATOR);
        }
    }

    public static void validateHeaderValue(CharSequence seq) {
        if (VALIDATE_HEADER) {
            int state = 0;
            // Start looping through each of the character
            for (int index = 0; index < seq.length(); index++) {
                state = validateValueChar(seq, state, seq.charAt(index));
            }

            if (state != 0) {
                throw new IllegalArgumentException("a header value must not end with '\\r' or '\\n':" + seq);
            }
        }
    }

    private static final int HIGHEST_INVALID_VALUE_CHAR_MASK = ~15;

    private static int validateValueChar(CharSequence seq, int state, char character) {
        /*
         * State:
         * 0: Previous character was neither CR nor LF
         * 1: The previous character was CR
         * 2: The previous character was LF
         */
        if ((character & HIGHEST_INVALID_VALUE_CHAR_MASK) == 0) {
            // Check the absolutely prohibited characters.
            switch (character) {
                case 0x0: // NULL
                    throw new IllegalArgumentException("a header value contains a prohibited character '\0': " + seq);
                case 0x0b: // Vertical tab
                    throw new IllegalArgumentException("a header value contains a prohibited character '\\v': " + seq);
                case '\f':
                    throw new IllegalArgumentException("a header value contains a prohibited character '\\f': " + seq);
            }
        }

        // Check the CRLF (HT | SP) pattern
        switch (state) {
            case 0:
                switch (character) {
                    case '\r':
                        return 1;
                    case '\n':
                        return 2;
                }
                break;
            case 1:
                switch (character) {
                    case '\n':
                        return 2;
                    default:
                        throw new IllegalArgumentException("only '\\n' is allowed after '\\r': " + seq);
                }
            case 2:
                switch (character) {
                    case '\t':
                    case ' ':
                        return 0;
                    default:
                        throw new IllegalArgumentException("only ' ' and '\\t' are allowed after '\\n': " + seq);
                }
        }
        return state;
    }

    public static void validateHeaderName(CharSequence value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case 0x00:
                case '\t':
                case '\n':
                case 0x0b:
                case '\f':
                case '\r':
                case ' ':
                case ',':
                case ':':
                case ';':
                case '=':
                    throw new IllegalArgumentException(
                            "a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " +
                                    value);
                default:
                    // Check to see if the character is not an ASCII character, or invalid
                    if (c > 127) {
                        throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " +
                                value);
                    }
            }
        }
    }

    /**
     * 切片链表
     */
    protected static class Slice {
        //起始位置
        protected int start = -1;
        //结束位置+1
        protected int end = -1;
        //部分字段
        protected boolean partial;
        //替换字符
        protected char value;
        //上一个节点
        protected Slice prev;
        //下一个节点
        protected Slice next;
        //起始小数点连续数量
        protected int dots;

        public Slice(int start, int end, Slice prev) {
            this(start, end, (char) 0, false, 0, prev);
        }

        public Slice(int start, int end, char value) {
            this(start, end, value, false, 0, null);
        }

        public Slice(int start, int end, char value, boolean partial) {
            this(start, end, value, partial, 0, null);
        }

        public Slice(int start, int end, char value, Slice prev) {
            this(start, end, value, false, 0, prev);
        }

        public Slice(int start, int end, char value, boolean partial, int dots, Slice prev) {
            this.start = start;
            this.end = end;
            this.value = value;
            this.partial = partial;
            this.dots = dots;
            this.prev = prev;
            if (prev != null) {
                prev.setNext(this);
                if (prev.partial) {
                    //没有结束，小数点延续
                    this.dots = prev.dots;
                }
            }
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public boolean isPartial() {
            return partial;
        }

        public void setPartial(boolean partial) {
            this.partial = partial;
        }

        public int length() {
            return end - start;
        }

        public char getValue() {
            return value;
        }

        public void setValue(char value) {
            this.value = value;
        }

        public int getDots() {
            return dots;
        }

        public Slice getPrev() {
            return prev;
        }

        public void setPrev(Slice prev) {
            this.prev = prev;
        }

        public Slice getNext() {
            return next;
        }

        public void setNext(Slice next) {
            this.next = next;
        }

        /**
         * 删除当前节点，返回上一个节点
         *
         * @return
         */
        public Slice remove() {
            if (prev != null) {
                prev.setNext(next);
                if (next != null) {
                    next.setPrev(prev);
                }
            } else if (next != null) {
                next.setPrev(null);
            }
            return prev;
        }

        /**
         * 删除当前路径
         *
         * @return 上一个路径
         */
        public Slice removePath() {
            Slice result = this;
            while ((result = result.remove()) != null && result.partial) {
                ;
            }
            return result;
        }
    }
}
