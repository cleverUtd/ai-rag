package com.zclau.ai.test;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author 刘梓聪
 * @email liuzicong@aulton.com
 * @date 2025/3/26 16:12
 * @Copyright Copyright(c) aulton Inc.AllRightsReserved.
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RAGTest {

    @Autowired
    private TokenTextSplitter tokenTextSplitter;
    @Autowired
    private PgVectorStore pgVectorStore;
    @Autowired
    private OpenAiChatModel chatModel;

    @Test
    public void upload() {
        TikaDocumentReader reader = new TikaDocumentReader("./data/test.md");
        List<Document> documentList = reader.read();
        List<Document> documentSplitterList = tokenTextSplitter.apply(documentList);

        documentList.forEach(doc -> doc.getMetadata().put("knowledge", "ai-rag-demo"));
        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "ai-rag-demo"));

        pgVectorStore.accept(documentSplitterList);

        log.info("=================== 上传完成 =====================");
    }

    @Test
    public void chat() {
        String message = "王西瓜，哪年出生";

        String SYSTEM_PROMPT = """
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;

        // 指定文档搜索
        SearchRequest request = SearchRequest.builder()
                .topK(5)
                .filterExpression("knowledge == 'ai-rag-demo'")
                .query(message)
                .build();
        List<Document> documents = pgVectorStore.similaritySearch(request);
        assert documents != null;
        String documentCollectors = documents.stream().map(Document::getText).collect(Collectors.joining());

        Message rawMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(message));
        messages.add(rawMessage);

        ChatResponse chatResponse = chatModel.call(new Prompt(messages));
        log.info("测试结果：{}", JSON.toJSONString(chatResponse));
    }
}
