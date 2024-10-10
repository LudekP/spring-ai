package com.msx.springai.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msx.springai.model.Answer;
import com.msx.springai.model.GetCapitalRequest;
import com.msx.springai.model.Question;
import com.msx.springai.services.OpenAIService;
import groovy.util.logging.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@lombok.extern.slf4j.Slf4j
@Slf4j
@Service
public class OpenAIServiceImpl implements OpenAIService {

    @Value("classpath:templates/get-capital-prompt.st")
    private Resource getCapitalPrompt;

    @Value("classpath:templates/get-capital-prompt-with-info.st")
    private Resource getCapitalPromptWithInfo;

    @Autowired
    private ObjectMapper objectMapper;

    private final ChatModel chatModel;

    public OpenAIServiceImpl(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String getAnswer(String question) {
        PromptTemplate promptTemplate = new PromptTemplate(question);
        Prompt prompt = promptTemplate.create();

        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    @Override
    public Answer getAnswer(Question question) {
        log.info("You've asked question: {}", question);
        PromptTemplate promptTemplate = new PromptTemplate(question.question());
        Prompt prompt = promptTemplate.create();

        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getContent());
    }

    @Override
    public Answer getCapital(GetCapitalRequest request) {
        log.info("You've asked for a capital city of {}", request);
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPrompt);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", request.stateOrCountry()));

        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getContent());
    }

    @Override
    public Answer getCapitalWithInfo(GetCapitalRequest request) {
        log.info("You've asked for a capital city of {} with additional information", request);
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPromptWithInfo);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", request.stateOrCountry()));

        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getContent());
    }

}
