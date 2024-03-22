package com.redis.demo.spring.ai;

import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.transformers.TransformersEmbeddingClient;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.RedisVectorStore.RedisVectorStoreConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RagConfiguration {

    @Bean
    @Primary
    TransformersEmbeddingClient transformersEmbeddingClient() {
        return new TransformersEmbeddingClient(MetadataMode.EMBED);
    }

    @Bean
    RedisVectorStore vectorStore(TransformersEmbeddingClient embeddingClient, RedisVectorStoreProperties properties) {
        var config = RedisVectorStoreConfig.builder().withURI(properties.getUri()).withIndexName(properties.getIndex())
                .withPrefix(properties.getPrefix()).build();
        RedisVectorStore vectorStore = new RedisVectorStore(config, embeddingClient);

        vectorStore.afterPropertiesSet();
        return vectorStore;
    }

    @Bean
    RagService ragService(ChatClient chatClient, VectorStore vectorStore) {
        return new RagService(chatClient, vectorStore);
    }

}
