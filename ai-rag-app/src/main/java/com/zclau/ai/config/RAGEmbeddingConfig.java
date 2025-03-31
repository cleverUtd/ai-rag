package com.zclau.ai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author 刘梓聪
 * @email liuzicong@aulton.com
 * @date 2025/3/26 15:28
 * @Copyright Copyright(c) aulton Inc.AllRightsReserved.
 **/
@Configuration
public class RAGEmbeddingConfig {

    /**
     * 用于切割文本的操作
     *
     * @author 刘梓聪
     * @email liuzicong@aulton.com
     * @date 2025/3/26 16:09
     * @Copyright Copyright(c) aulton Inc.AllRightsReserved.
     **/
    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }


    /**
     * 把知识库缓存到内存
     *
     * @author 刘梓聪
     * @email liuzicong@aulton.com
     * @date 2025/3/26 16:09
     * @Copyright Copyright(c) aulton Inc.AllRightsReserved.
     **/
    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
