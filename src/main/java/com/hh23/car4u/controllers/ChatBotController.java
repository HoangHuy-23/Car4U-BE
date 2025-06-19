package com.hh23.car4u.controllers;

import com.hh23.car4u.dtos.AIResponse;
import com.hh23.car4u.services.GeminiAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatBotController {
    private final GeminiAIService geminiAIService;

    @PostMapping("/ask")
    public ResponseEntity<AIResponse> ask(@RequestBody Map<String, String> request) {
        log.info("Received user message: {}", request);
        AIResponse response = geminiAIService.searchCarsByIntent(request.get("message"));
        log.info("AI response: {}", response);
        return ResponseEntity.ok(response);
    }

}
