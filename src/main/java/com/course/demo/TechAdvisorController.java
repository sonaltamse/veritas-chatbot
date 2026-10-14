package com.course.demo;

import org.apache.catalina.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/advisor")
public class TechAdvisorController {
    private final ChatClient chatClient;
    private final ChatClient chatClientWithDefaultPersona;

    @Value("classpath:/prompts/recipe-prompts.st")
    private Resource programPromt;

    public TechAdvisorController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
        this.chatClientWithDefaultPersona = builder.clone().defaultSystem("You are strict technical interviewer conducting a Java backend interview."+
                "Ask probing follow-up questions in your response.").build();
    }

    @GetMapping
    public String advise(){
        return chatClient.prompt().user("Explain Spring Boot in Simple terms.").call().content();
    }

    @GetMapping("/converation-history")
    public String conversationHistory(@RequestParam String followUpQuestion){
        Message systemMessage = new SystemMessage("You are a helpful Java Tutor.");
        Message userMessage = new UserMessage("Explain Spring Boot in Simple terms.");
        Message priorReply = new AssistantMessage("Spring Boot is a framework for building Java applications. It simplifies the process of creating stand-alone, production-ready applications by providing default configurations and a set of tools that make it easier to get started. In simple terms, it helps developers quickly create Java applications without having to deal with complex setup and configuration.");
        Message newUserMessage = new UserMessage(followUpQuestion);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage, priorReply, newUserMessage));
        return chatClient.prompt(prompt).call().content();
    }

    @GetMapping("/system-prompt")
    public String systemPrompt(){
        return chatClient.prompt().system("You are a helpful Java Tutor.").user("Explain Spring Boot in simple and easy to understand format.").call().content();
    }

    @GetMapping("/default-system-prompt")
    public String defaultSystemPrompt(@RequestParam String question){
        return chatClient.prompt().user(question).call().content();
    }

    @GetMapping("/prompt-variables")
    public String promptVariables(@RequestParam String question, @RequestParam String personaName){
        PromptTemplate promptTemplate = new PromptTemplate("You are a {personaName}. {question}");
        Prompt prompt = promptTemplate.create(Map.of("question", question, "personaName", personaName));
        return "<pre>"+chatClient.prompt(prompt).call().content()+"<pre>";
    }

    @GetMapping("/inline-prompt-templates")
    public String inlinePromptTemplates(@RequestParam String question, @RequestParam String personaName){
        return "<pre>"+chatClient.prompt().user(u->u.text("Write a short story about "+personaName+". "+question).param("personaName", personaName).param("question", question)).call().content()+"<pre>";
    }

    @GetMapping("/external-prompt-file")
    public String externalPromptFile(@RequestParam String concept){
        PromptTemplate promptTemplate = new PromptTemplate(programPromt);
        Prompt prompt = promptTemplate.create(Map.of("concept", concept));
        return "<pre>"+chatClient.prompt(prompt).call().content()+"<pre>";
    }
}
