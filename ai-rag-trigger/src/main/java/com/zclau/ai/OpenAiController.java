package com.zclau.ai;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author 刘梓聪
 * @email liuzicong@aulton.com
 * @date 2025/3/12 21:53
 * @Copyright Copyright(c) aulton Inc.AllRightsReserved.
 **/
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/openai/")
public class OpenAiController {

    @Autowired
    private OpenAiChatModel chatModel;
    @Autowired
    private PgVectorStore pgVectorStore;

    @GetMapping("/ai/generate")
    public ChatResponse generates(@RequestParam String model, @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatModel.call(new Prompt(
                message,
                OpenAiChatOptions.builder().model(model).build()
        ));
    }


    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(@RequestParam String model, @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatModel.stream(new Prompt(
                message,
                OpenAiChatOptions.builder().model(model).build()
        ));
    }

    @RequestMapping(value = "/ai/generateStreamRag", method = RequestMethod.GET)
    public Flux<ChatResponse> generateStreamRag(@RequestParam String model, @RequestParam String ragTag, @RequestParam String message) {

        String SYSTEM_PROMPT = """
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;

        // 指定文档搜索
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(5)
                .filterExpression("knowledge == '" + ragTag + "'")
                .build();

        List<Document> documents = pgVectorStore.similaritySearch(request);
        assert documents != null;
        String documentCollectors = documents.stream().map(Document::getText).collect(Collectors.joining());
        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));

        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(message));
        messages.add(ragMessage);

        return chatModel.stream(new Prompt(
                messages,
                OpenAiChatOptions.builder().model(model).build()
        ));
    }

    @RequestMapping(value = "/ai/generateRag", method = RequestMethod.GET)
    public ChatResponse generateRag(@RequestParam String model, @RequestParam String ragTag, @RequestParam String message) {

        String SYSTEM_PROMPT = """
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;

        // 指定文档搜索
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(5)
                .filterExpression("knowledge == '" + ragTag + "'")
                .build();

        List<Document> documents = pgVectorStore.similaritySearch(request);
        assert documents != null;
        String documentCollectors = documents.stream().map(Document::getText).collect(Collectors.joining());
        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));

        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(message));
        messages.add(ragMessage);

        return chatModel.call(new Prompt(
                messages,
                OpenAiChatOptions.builder().model(model).build()
        ));
    }
}
