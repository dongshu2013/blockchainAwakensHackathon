package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorBody {
    private String message;

    public ErrorBody() {

    }

    public ErrorBody(String message) {
        this.message = message;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
