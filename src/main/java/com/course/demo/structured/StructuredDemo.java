package com.course.demo.structured;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/structured")
public class StructuredDemo {
    private final ChatClient chatClient;

    @Value("classpath:/prompts/recipe-prompts.st")
    private String fewShotPromt;

    public StructuredDemo(ChatClient.Builder builder) {
        this.chatClient = builder.build();;
    }

    @GetMapping("/course-summary")
    public CourseSummary courseSummary(@RequestParam String topic) {
        String prompt = String.format("""
            Create a course summary for a course on the topic of "%s".
            The summary should include the following information:
            - Course title
            - Course level (beginner, intermediate, advanced)
            - List of learning objectives
            - Estimated hours to complete the course
        
            Please provide the response in JSON format with the following structure:
            {
              "title": "Course Title",
              "level": "Course Level",
              "learningObjectives": ["Objective 1", "Objective 2", ...],
              "estimatedHours": Estimated Hours
            }
            """, topic);

        return chatClient.prompt().user(prompt).call().entity(CourseSummary.class);
    }
}
