package com.msx.springai.controllers;

import com.msx.springai.model.*;
import com.msx.springai.services.OpenAIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionController {

    private final OpenAIService openAIService;

    public QuestionController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/capitalWithInfo")
    public GetCapitalResponseWithInfo getCapitalWithInfo(@RequestBody GetCapitalRequest request) {
        return openAIService.getCapitalWithInfo(request);
    }

    @PostMapping("/capital")
    public GetCapitalResponse getCapital(@RequestBody GetCapitalRequest request) {
        return openAIService.getCapital(request);
    }

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question) {
        return openAIService.getAnswer(question);
    }

}
