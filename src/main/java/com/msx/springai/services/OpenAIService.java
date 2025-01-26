package com.msx.springai.services;

import com.msx.springai.model.*;
import reactor.core.publisher.Flux;

public interface OpenAIService {

    String getAnswer(String question);

    Flux<String> getAnswers(Question question);

    Answer getAnswer(Question question);

    GetCapitalResponse getCapital(GetCapitalRequest request);

    GetCapitalResponseWithInfo getCapitalWithInfo(GetCapitalRequest request);

    Answer getMovieAnswerRAG(Question question);
}
