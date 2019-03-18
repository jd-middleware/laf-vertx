package com.jd.laf.web.vertx.query;

public class QPageQuery<M> {

    protected int page;
    protected int size;
    protected M query;

    public QPageQuery() {
    }

    public QPageQuery(int page, int size, M query) {
        this.page = page;
        this.size = size;
        this.query = query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public M getQuery() {
        return query;
    }

    public void setQuery(M query) {
        this.query = query;
    }
}
