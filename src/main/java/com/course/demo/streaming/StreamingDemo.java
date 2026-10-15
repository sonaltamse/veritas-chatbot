package com.course.demo.streaming;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/streaming")
public class StreamingDemo {
    private final ChatClient chatClient;

    @Value("classpath:/prompts/recipe-prompts.st")
    private String fewShotPromt;

    public StreamingDemo(ChatClient.Builder builder) {
        this.chatClient = builder.build();;
    }

    @GetMapping(value = "/sse-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> courseSummary(@RequestParam String topic) {
        String prompt = String.format("""
            Write 5 fun facts about %s, one per line.
            """, topic);

        return chatClient.prompt().user(prompt).stream().content();
    }
}
