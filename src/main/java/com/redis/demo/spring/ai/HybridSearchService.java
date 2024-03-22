
package com.redis.demo.spring.ai;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.stereotype.Service;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;

import java.nio.ByteBuffer;
import java.util.List;

@Service
public class HybridSearchService {

    private final RedisVectorStore vectorStore;
    private final EmbeddingClient embeddingClient;

    public HybridSearchService(RedisVectorStore vectorStore, EmbeddingClient embeddingClient){
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }

    public void searchRedis(String indexName, String queryString, String hybridFields, String vectorField, int k) {

        // Fields that will be returned from query results
        String[] returnFields = new String[] { "id", "name", "nameDisplay", "description",
                "abv","ibu", "vector_score", "content" };

        List<Double> embedding = embeddingClient.embed(queryString);

        // Build query
        Query q = new Query(hybridFields + "=>[KNN $k @" + vectorField + "$vec AS vector_score]")
                .returnFields(returnFields)
                .setSortBy("vector_score", true)
                .addParam("k", k)
                .addParam("vec", doubleToByte(embedding))
                .limit(0, k)
                .dialect(2);

        // Get and iterate over query results
        SearchResult res = vectorStore.getJedis().ftSearch(indexName, q);
        List<Document> products = res.getDocuments();
        int i = 1;
        for (Document prod : products) {
            float score = Float.parseFloat((String) prod.get("vector_score"));
            System.out.println(i + ". " + prod.get("nameDisplay") + " (Score: " + (1 - score) + ")");
            i++;
        }
    }
    public byte[] doubleToByte(List<Double> doubles) {
        ByteBuffer buffer = ByteBuffer.allocate(doubles.size() * Double.BYTES);
        for (Double value : doubles) {
            buffer.putDouble(value);
        }
        return buffer.array();
    }
}
