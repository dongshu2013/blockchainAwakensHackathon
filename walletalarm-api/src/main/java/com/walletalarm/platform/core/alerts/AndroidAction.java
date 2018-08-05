package com.walletalarm.platform.core.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AndroidAction {
    //    {
//        icon: 'emailGuests',
//                title: 'EMAIL GUESTS',
//            callback: 'emailGuests',
//            foreground: false,
//            inline: true,
//            replyLabel: 'Enter your reply here'
//    }
    private String title;
    private String icon;
    private String callback;
    private boolean foreground;
    private boolean inline;
    private String replyLabel;

    public AndroidAction(String title, String icon, String callback, boolean foreground, boolean inline, String replyLabel) {
        this.title = title;
        this.icon = icon;
        this.callback = callback;
        this.foreground = foreground;
        this.inline = inline;
        this.replyLabel = replyLabel;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public String getReplyLabel() {
        return replyLabel;
    }

    public void setReplyLabel(String replyLabel) {
        this.replyLabel = replyLabel;
    }

    @JsonProperty
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @JsonProperty
    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    @JsonProperty
    public boolean getForeground() {
        return foreground;
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

    @JsonProperty
    public boolean getInline() {
        return inline;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }
}