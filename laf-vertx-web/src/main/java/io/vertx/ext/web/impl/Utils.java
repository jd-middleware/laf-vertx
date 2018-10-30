package io.vertx.ext.web.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.CharsetUtil;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utils extends io.vertx.core.impl.Utils {

    private static final Pattern SEMICOLON_SPLITTER = Pattern.compile(" *; *");
    private static final Pattern EQUAL_SPLITTER = Pattern.compile(" *= *");

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

    private static void decode(StringBuilder path, int start) {
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
     * Normalizes a path as per <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4>rfc3986</a>.
     * <p>
     * There are 2 extra transformations that are not part of the spec but kept for backwards compatibility:
     * <p>
     * double slash // will be converted to single slash and the path will always start with slash.
     *
     * @param pathname raw path
     * @return normalized path
     */
    public static String normalize(String pathname) {
        // add trailing slash if not set
        if (pathname == null || pathname.length() == 0) {
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
                decode(ibuf, i);
            }

            i++;
        }

        // remove dots as described in
        // http://tools.ietf.org/html/rfc3986#section-5.2.4
        return removeDots(ibuf);
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
     * Normalizes a path as per <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4>rfc3986</a>.
     * <p>
     * There are 2 extra transformations that are not part of the spec but kept for backwards compatibility:
     * <p>
     * double slash // will be converted to single slash and the path will always start with slash.
     * <p>
     * 对性能进行了优化，原有的方法名称改为normalize
     *
     * @param pathname raw path
     * @return normalized path
     */
    public static String normalizePath(final String pathname) {
        // add trailing slash if not set
        if (pathname == null || pathname.isEmpty()) {
            return "/";
        }
        //不需要变化
        boolean flag = false;
        //根节点
        Slice root = null;
        //字符串不以'/'开头
        if (pathname.charAt(0) != '/') {
            root = new Slice(-1, -1, '/');
            root.partial = true;
            flag = true;
        }
        int length = pathname.length();
        char ch;
        Slice slice = new Slice(0, -1, root);
        root = root == null ? slice : root;
        //遍历字符，按照'/'切片
        for (int i = 0; i < length; ) {
            ch = pathname.charAt(i);
            //当前字符
            switch (ch) {
                case '/':
                    if (i > 0) {
                        //不是第一个字符，则作为结束符号
                        slice.end = i;
                        slice.partial = false;
                        flag = flag || slice.dots != -1;
                        //创建下一个切片
                        slice = new Slice(i, -1, slice);
                    }
                    slice.accept(ch);
                    break;
                case '%':
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
                        slice.accept(ch);
                        if (slice.end < length) {
                            //创建下一个切片
                            slice.partial = true;
                            slice = new Slice(slice.end, -1, slice);
                        }
                        flag = true;
                        i += 2;
                    } else {
                        slice.accept(ch);
                    }
                    break;
                default:
                    slice.accept(ch);
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

    /**
     * Removed dots as per <a href="http://tools.ietf.org/html/rfc3986#section-5.2.4>rfc3986</a>.
     * <p>
     * There are 2 extra transformations that are not part of the spec but kept for backwards compatibility:
     * <p>
     * double slash // will be converted to single slash and the path will always start with slash.
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
     * Decodes a bit of an URL encoded by a browser.
     * <p>
     * The string is expected to be encoded as per RFC 3986, Section 2. This is the encoding used by JavaScript functions
     * encodeURI and encodeURIComponent, but not escape. For example in this encoding, é (in Unicode U+00E9 or in
     * UTF-8 0xC3 0xA9) is encoded as %C3%A9 or %c3%a9.
     *
     * @param s    string to decode
     * @param plus weather or not to transform plus signs into spaces
     * @return decoded string
     */
    public static String urlDecode(final String s, boolean plus) {
        if (s == null) {
            return null;
        }

        final int size = s.length();
        boolean modified = false;
        for (int i = 0; i < size; i++) {
            final char c = s.charAt(i);
            if (c == '%' || (plus && c == '+')) {
                modified = true;
                break;
            }
        }
        if (!modified) {
            return s;
        }
        final byte[] buf = new byte[size];
        int pos = 0;  // position in `buf'.
        for (int i = 0; i < size; i++) {
            char c = s.charAt(i);
            if (c == '%') {
                if (i == size - 1) {
                    throw new IllegalArgumentException("unterminated escape"
                            + " sequence at end of string: " + s);
                }
                c = s.charAt(++i);
                if (c == '%') {
                    buf[pos++] = '%';  // "%%" -> "%"
                    break;
                }
                if (i == size - 1) {
                    throw new IllegalArgumentException("partial escape"
                            + " sequence at end of string: " + s);
                }
                c = decodeHexNibble(c);
                final char c2 = decodeHexNibble(s.charAt(++i));
                if (c == Character.MAX_VALUE || c2 == Character.MAX_VALUE) {
                    throw new IllegalArgumentException(
                            "invalid escape sequence `%" + s.charAt(i - 1)
                                    + s.charAt(i) + "' at index " + (i - 2)
                                    + " of: " + s);
                }
                c = (char) (c * 16 + c2);
                // shouldn't check for plus since it would be a double decoding
                buf[pos++] = (byte) c;
            } else {
                buf[pos++] = (byte) (plus && c == '+' ? ' ' : c);
            }
        }
        return new String(buf, 0, pos, CharsetUtil.UTF_8);
    }

    /**
     * Helper to decode half of a hexadecimal number from a string.
     *
     * @param c The ASCII character of the hexadecimal number to decode.
     *          Must be in the range {@code [0-9a-fA-F]}.
     * @return The hexadecimal value represented in the ASCII character
     * given, or {@link Character#MAX_VALUE} if the character is invalid.
     */
    private static char decodeHexNibble(final char c) {
        if ('0' <= c && c <= '9') {
            return (char) (c - '0');
        } else if ('a' <= c && c <= 'f') {
            return (char) (c - 'a' + 10);
        } else if ('A' <= c && c <= 'F') {
            return (char) (c - 'A' + 10);
        } else {
            return Character.MAX_VALUE;
        }
    }

    public static ClassLoader getClassLoader() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        return tccl == null ? Utils.class.getClassLoader() : tccl;
    }

    public static Buffer readResourceToBuffer(String resource) {
        ClassLoader cl = getClassLoader();
        try {
            Buffer buffer = Buffer.buffer();
            try (InputStream in = cl.getResourceAsStream(resource)) {
                if (in == null) {
                    return null;
                }
                int read;
                byte[] data = new byte[4096];
                while ((read = in.read(data, 0, data.length)) != -1) {
                    if (read == data.length) {
                        buffer.appendBytes(data);
                    } else {
                        byte[] slice = new byte[read];
                        System.arraycopy(data, 0, slice, 0, slice.length);
                        buffer.appendBytes(slice);
                    }
                }
            }
            return buffer;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /*
    Reads from file or classpath using UTF-8
     */
    public static String readFileToString(Vertx vertx, String resource) {
        return readFileToString(vertx, resource, StandardCharsets.UTF_8);
    }

    /*
    Reads from file or classpath using the provided charset
     */
    public static String readFileToString(Vertx vertx, String resource, Charset charset) {
        try {
            Buffer buff = vertx.fileSystem().readFileBlocking(resource);
            return buff.toString(charset);
        } catch (Exception e) {
            throw new VertxException(e);
        }
    }

    public static DateFormat createRFC1123DateTimeFormatter() {
        DateFormat dtf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        dtf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dtf;
    }

    public static String pathOffset(String path, RoutingContext context) {
        int prefixLen = 0;
        String mountPoint = context.mountPoint();
        if (mountPoint != null) {
            prefixLen = mountPoint.length();
        }
        String routePath = context.currentRoute().getPath();
        if (routePath != null) {
            prefixLen += routePath.length();
            // special case we need to verify if a trailing slash  is present and exclude
            if (routePath.charAt(routePath.length() - 1) == '/') {
                prefixLen--;
            }
        }
        return prefixLen != 0 ? path.substring(prefixLen) : path;
    }

    private static final Comparator<String> ACCEPT_X_COMPARATOR = new Comparator<String>() {
        float getQuality(String s) {
            if (s == null) {
                return 0;
            }

            String[] params = SEMICOLON_SPLITTER.split(s);
            for (int i = 1; i < params.length; i++) {
                String[] q = EQUAL_SPLITTER.split(params[1]);
                if ("q".equals(q[0])) {
                    return Float.parseFloat(q[1]);
                }
            }
            return 1;
        }

        @Override
        public int compare(String o1, String o2) {
            float f1 = getQuality(o1);
            float f2 = getQuality(o2);
            return Float.compare(f2, f1);
        }
    };

    public static long secondsFactor(long millis) {
        return millis - (millis % 1000);
    }

    public static JsonNode toJsonNode(String object) {
        try {
            return new ObjectMapper().readTree(object);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonNode toJsonNode(JsonObject object) {
        try {
            return new ObjectMapper().readTree(object.encode());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject toJsonObject(JsonNode node) {
        return new JsonObject(node.toString());
    }

    public static JsonArray toJsonArray(JsonNode node) {
        return new JsonArray(node.toString());
    }

    public static Object toVertxJson(JsonNode node) {
        if (node.isArray()) {
            return toJsonArray(node);
        } else if (node.isObject()) {
            return toJsonObject(node);
        } else {
            return node.toString();
        }
    }

    public static boolean isJsonContentType(String contentType) {
        return contentType.contains("application/json") || contentType.contains("+json");
    }

    public static boolean isXMLContentType(String contentType) {
        return contentType.contains("application/xml") || contentType.contains("text/xml") || contentType.contains("+xml");
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
            this(start, end, (char) 0, prev);
        }

        public Slice(int start, int end, char value) {
            this(start, end, value, null);
        }

        public Slice(int start, int end, char value, Slice prev) {
            this.start = start;
            this.end = end;
            this.value = value;
            this.prev = prev;
            if (prev != null) {
                prev.setNext(this);
                if (prev.partial) {
                    //没有结束，小数点延续
                    this.dots = prev.dots;
                }
            }
            if (value > 0) {
                //当前字符，继续判断小数点
                accept(value);
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

        public void accept(final char ch) {
            if (dots == -1) {
                return;
            }
            switch (ch) {
                case '/':
                    dots = dots == 0 ? 1 : -1;
                    break;
                case '.':
                    dots = (dots == 1 || dots == 2) ? ++dots : -1;
                    break;
                default:
                    dots = -1;
            }
        }
    }

}