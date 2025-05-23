package com.msx.springai.services.impl;

import com.msx.springai.functions.WeatherServiceFunction;
import com.msx.springai.model.*;
import com.msx.springai.services.OpenAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAIServiceImpl implements OpenAIService {

    @Value("classpath:templates/get-capital-prompt.st")
    private Resource getCapitalPrompt;

    @Value("classpath:templates/get-capital-prompt-with-info.st")
    private Resource getCapitalPromptWithInfo;

    @Value("classpath:templates/rag-prompt-template.st")
    private Resource getRagPrompt;

    @Value("classpath:templates/system-message.st")
    private Resource getBoatTowSystemMessage;

    @Value("${sfg.aiapp.apiNinjasKey}")
    private String apiNinjasKey;

    private final ChatModel chatModel;
    private final VectorStore vectorStore;
    private final OpenAiChatModel openAiChatModel;

    public OpenAIServiceImpl(ChatModel chatModel, VectorStore vectorStore, OpenAiChatModel openAiChatModel) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.openAiChatModel = openAiChatModel;
    }

    @Override
    public String getAnswer(String question) {
        PromptTemplate promptTemplate = new PromptTemplate(question);
        Prompt prompt = promptTemplate.create();

        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    @Override
    public Flux<String> getAnswers(Question question) {
        // Step 1: Create a prompt based on the question
        PromptTemplate promptTemplate = new PromptTemplate(question.question());
        Prompt prompt = promptTemplate.create();

        // Step 2: Stream responses using chat model
        Flux<ChatResponse> responseStream = chatModel.stream(prompt);

        // Step 3: Transform each ChatResponse to String content
        return responseStream
                .map(chatResponse -> chatResponse.getResult() != null
                        && chatResponse.getResult().getOutput() != null
                        && chatResponse.getResult().getOutput().getText() != null ? chatResponse.getResult().getOutput().getText() : "")
                .onErrorResume(e -> Flux.just("Error: " + e.getMessage()));
    }

    @Override
    public Answer getAnswer(Question question) {
        log.info("You've asked question: {}", question);
        PromptTemplate promptTemplate = new PromptTemplate(question.question());
        Prompt prompt = promptTemplate.create();

        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public GetCapitalResponse getCapital(GetCapitalRequest request) {
        log.info("You've asked for a capital city of {}", request);
        BeanOutputConverter<GetCapitalResponse> outputConverter = new BeanOutputConverter<>(GetCapitalResponse.class);
        String format = outputConverter.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPrompt);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", request.stateOrCountry(),
                "format", format));

        ChatResponse response = chatModel.call(prompt);
        return outputConverter.convert(response.getResult().getOutput().getText());
    }

    @Override
    public GetCapitalResponseWithInfo getCapitalWithInfo(GetCapitalRequest request) {
        log.info("You've asked for a capital city of {} with additional information", request);
        BeanOutputConverter<GetCapitalResponseWithInfo> outputConverter = new BeanOutputConverter<>(GetCapitalResponseWithInfo.class);
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPromptWithInfo);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", request.stateOrCountry(), "format", outputConverter.getFormat()));

        ChatResponse response = chatModel.call(prompt);
        return outputConverter.convert(response.getResult().getOutput().getText());
    }

    @Override
    public Answer getMovieAnswerRAG(Question question) {
        log.info("You've asked question: {}", question);
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(question.question()).topK(4).build());
        List<String> context = documents.stream().map(Document::getText).toList();
        PromptTemplate promptTemplate = new PromptTemplate(getRagPrompt);
        Prompt prompt = promptTemplate.create(Map.of("input", question.question(), "documents", String.join("\n", context)));

        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public Answer getBoatTowAnswerRAG(Question question) {
        PromptTemplate systemMessageTemplate = new PromptTemplate(getBoatTowSystemMessage);
        Message systemMessage = systemMessageTemplate.createMessage();

        log.info("You've asked question: {}", question);
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(question.question()).topK(2).build());
        List<String> context = documents.stream().map(Document::getText).toList();
        PromptTemplate promptTemplate = new PromptTemplate(getRagPrompt);
        Message userMessage = promptTemplate.createMessage(Map.of("input", question.question(), "documents", String.join("\n", context)));

        ChatResponse response = chatModel.call(new Prompt(List.of(systemMessage, userMessage)));
        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public Answer getWeatherAnswer(Question question) {
        FunctionCallback functionCallback = FunctionCallback.builder()
                .function("CurrentWeather", new WeatherServiceFunction(apiNinjasKey))
                .description("Get the current weather for a location")
                .inputType(WeatherRequest.class) // Explicitly specify the input type
                .build();

        var promptOptions = OpenAiChatOptions.builder()
                .functionCallbacks(List.of(functionCallback))
                .build();

        Message userMessage = new PromptTemplate(question.question()).createMessage();

        var response = openAiChatModel.call(new Prompt(List.of(userMessage), promptOptions));

        return new Answer(response.getResult().getOutput().getText());
    }

}
