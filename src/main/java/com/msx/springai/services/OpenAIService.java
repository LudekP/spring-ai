package com.msx.springai.services;

import com.msx.springai.model.*;

public interface OpenAIService {

    String getAnswer(String question);

    Answer getAnswer(Question question);

    GetCapitalResponse getCapital(GetCapitalRequest request);

    GetCapitalResponseWithInfo getCapitalWithInfo(GetCapitalRequest request);
}
