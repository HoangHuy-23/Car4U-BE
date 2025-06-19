package com.hh23.car4u.services;

import com.hh23.car4u.dtos.AIResponse;

public interface GeminiAIService {
    AIResponse searchCarsByIntent(String userMessage);
}
