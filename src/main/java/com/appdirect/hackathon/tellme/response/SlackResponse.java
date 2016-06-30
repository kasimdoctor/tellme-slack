package com.appdirect.hackathon.tellme.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackResponse {
    
    private String text;
    private String response_type;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getResponse_type() {
        return response_type;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
