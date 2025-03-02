package com.msx.springai.bootstrap;


import com.msx.springai.config.VectorStoreProperties;
import io.milvus.client.MilvusClient;
import io.milvus.grpc.CollectionSchema;
import io.milvus.grpc.DataType;
import io.milvus.grpc.FieldSchema;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.highlevel.collection.ListCollectionsParam;
import io.milvus.param.highlevel.collection.response.ListCollectionsResponse;
import io.milvus.param.index.CreateIndexParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LoadVectorStore implements CommandLineRunner {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private VectorStoreProperties vectorStoreProperties;

    @Autowired
    private MilvusClient milvusClient;


    @Override
    public void run(String... args) throws Exception {

        log.debug("Loading vector store...");

        // Step 2: Check if the collection already exists
        ListCollectionsParam param = ListCollectionsParam.newBuilder().build();
        R<ListCollectionsResponse> collections = milvusClient.listCollections(param);

        if (vectorStore.similaritySearch("Sportsman").isEmpty()) {
            vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
                log.info("Vector store is empty, loading data...");
                TikaDocumentReader documentReader = new TikaDocumentReader(document);
                List<Document> documents = documentReader.get();
                TextSplitter textSplitter = new TokenTextSplitter();
                List<Document> splitDocuments = textSplitter.apply(documents);
                vectorStore.add(splitDocuments);
            });
        }
    }
}
