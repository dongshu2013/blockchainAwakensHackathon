package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UrlOutput {
    private UrlType urlType;
    private String url;

    public UrlOutput() {
    }

    public UrlOutput(UrlType urlType, String url) {
        this.urlType = urlType;
        this.url = url;
    }

    @JsonProperty
    public UrlType getUrlType() {
        return urlType;
    }

    public void setUrlType(UrlType urlType) {
        this.urlType = urlType;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
