package com.appdirect.hackathon.tellme.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appdirect.hackathon.tellme.app.MesssageApiImpl;

@RestController
@RequestMapping("/message")
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MesssageApiImpl messsageApi;
    
    @RequestMapping(method = RequestMethod.GET, value = "/request")
    public ResponseEntity<String> getEventResponse(HttpServletRequest request, @RequestParam(value="token") String token, @RequestParam(value="team_id") String teamId,
                                                   @RequestParam(value="team_domain") String teamDomain, @RequestParam(value="channel_id") String channelId,
                                                   @RequestParam(value="user_id") String userId, @RequestParam(value="user_name") String userName,
                                                   @RequestParam(value="command") String command, @RequestParam(value="text") String question,
                                                   @RequestParam(value="response_url") String responseUrl) {
        LOGGER.info("Received request with token = {}, TeamID = {}, UserID = {}, UserName = {}, Command = {}, Question = {}", token, teamId, userId, userName, command, question);
        return messsageApi.callWitAiApi(question, token);
    }
}
