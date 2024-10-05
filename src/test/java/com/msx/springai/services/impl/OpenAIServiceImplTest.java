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

        String answer = openAIService.getAnswer("Tell me a dad joke.");
        System.out.println("Answer: " + answer);
    }
}