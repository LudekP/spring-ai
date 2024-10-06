package com.msx.springai.services;

import com.msx.springai.model.Answer;
import com.msx.springai.model.Question;

public interface OpenAIService {

    String getAnswer(String question);

    Answer getAnswer(Question question);
}
