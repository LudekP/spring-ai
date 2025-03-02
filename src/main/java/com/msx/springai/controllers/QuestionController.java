package com.msx.springai.controllers;

import com.msx.springai.model.*;
import com.msx.springai.services.OpenAIService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class QuestionController {

    private final OpenAIService openAIService;

    public QuestionController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping(value = "/stream-response-for-question", produces = "text/event-stream")
    public Flux<String> streamChatResponse(@RequestBody Question question) {
        return openAIService.getAnswers(question);
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

    @PostMapping("/ask/movies")
    public Answer askQuestionAboutMovies(@RequestBody Question question) {
        return openAIService.getMovieAnswerRAG(question);
    }

    @PostMapping("/ask/boat-tow")
    public Answer askQuestionAboutBoatTow(@RequestBody Question question) {
        return openAIService.getBoatTowAnswerRAG(question);
    }

    @PostMapping("/ask/weather")
    public Answer askWeatherQuestion(@RequestBody Question question) {
        return openAIService.getWeatherAnswer(question);
    }

}
