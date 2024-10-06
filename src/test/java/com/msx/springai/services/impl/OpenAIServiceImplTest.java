package com.msx.springai.services.impl;

import com.msx.springai.services.OpenAIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenAIServiceImplTest {

    @Autowired
    OpenAIService openAIService;

    @Test
    void getAnswer() {

        String answer = openAIService.getAnswer("Write a python script to output numbers from 1 to 100.");
        System.out.println("Answer: " + answer);
    }
}