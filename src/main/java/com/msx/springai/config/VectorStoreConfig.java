package com.msx.springai.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

//@Configuration
public class VectorStoreConfig {

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel, VectorStoreProperties vectorStoreProperties) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File vectorStoreFile = new File(vectorStoreProperties.getVectorStorePath());
        if (vectorStoreFile.exists()) {
            vectorStore.load(vectorStoreFile);
        } else {
            vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
                TikaDocumentReader documentReader = new TikaDocumentReader(document);
                List<Document> docs = documentReader.read();
                TextSplitter textSplitter = new TokenTextSplitter();
                List<Document> splitDocs = textSplitter.apply(docs);
                vectorStore.add(splitDocs);
            });

            vectorStore.save(vectorStoreFile);
        }
        return vectorStore;
    }
}
