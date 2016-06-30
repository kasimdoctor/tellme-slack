package com.appdirect.hackathon.tellme.app;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.bots4j.wit.WitClient;
import org.bots4j.wit.beans.ConverseResponse;
import org.bots4j.wit.beans.WitContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.appdirect.hackathon.tellme.response.SlackResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

@Component
public class MesssageApiImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MesssageApiImpl.class);
    
    private static final String WIT_BEARER_TOKEN = "ECHNQGMCVOWEALFIFVNKIFRDF6Y5KQDW";
    private static final String API_VERSION = "20160629";
    private static final String SLACK_TOKEN = "XFDenCvU5ZNy9YdgpRaBqZoV";
    private static final String RESPONSE_TYPE = "in_channel";

    private WitClient client = new WitClient(WIT_BEARER_TOKEN);
    private com.fasterxml.jackson.databind.ObjectMapper jacksonMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    
    @PostConstruct
    public void postConstruct() {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            public <SlackResponse> SlackResponse readValue(String value, Class<SlackResponse> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Call the WIT AI Api to get an outcome for the user's question.
     * @param question
     * @param token
     * @return
     */
    public ResponseEntity<String> callWitAiApi(String question, String token) {
        StringBuffer witAPIResponse = new StringBuffer();
        
        try {
            if (!StringUtils.isBlank(question) && verifyRequest(token)) {
                String randomSessionID = UUID.randomUUID().toString();

                WitContext ctx = new WitContext();
                ConverseResponse response = client.converse(randomSessionID, question, ctx);
                while (!response.getType().equals("stop")) {
                    if (response.getMsg() != null) {
                        witAPIResponse.append(response.getMsg() + "\n");
                    }
                    response = client.converse(randomSessionID, null, ctx);
                }
            } else {
                witAPIResponse.append("I am not sure what you mean.");
            }
        } catch(Exception e) {
            LOGGER.error("Something went wrong");
        }
        
        return new ResponseEntity<>(createSlackResponse(witAPIResponse), HttpStatus.OK);
    }
    
    private boolean verifyRequest(String token) {
        return SLACK_TOKEN.equals(token);
    }
    
    private String createSlackResponse(StringBuffer rawResponse) {
        String sendResponse = null;
        try {
            SlackResponse JSONResponse = new SlackResponse();
            JSONResponse.setText(rawResponse.toString());
            JSONResponse.setResponse_type(RESPONSE_TYPE);
            sendResponse = jacksonMapper.writeValueAsString(JSONResponse);

        } catch(Exception e) {
            // do something
        }
        
        return sendResponse;
    }
    
}
