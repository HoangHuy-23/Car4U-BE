package com.hh23.car4u.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh23.car4u.dtos.AIIntent;
import com.hh23.car4u.dtos.AIResponse;
import com.hh23.car4u.dtos.PageResponse;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.services.CarService;
import com.hh23.car4u.services.GeminiAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiAIServiceImpl implements GeminiAIService {
    @Value("${app.gemini.api-key}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final CarService carService;
    @Override
    public AIResponse searchCarsByIntent(String userMessage) {
        AIIntent intent = null;
        try {
            intent = extractIntentFromResponse(userMessage);
            if (intent == null) {
                return AIResponse.builder()
                        .message("Không thể xác định ý định từ câu hỏi của bạn. Vui lòng thử lại.")
                        .build();
            }
        } catch (JsonProcessingException e) {
            return AIResponse.builder()
                    .message("Lỗi khi xử lý phản hồi từ AI: " + e.getMessage())
                    .build();
        }
        PageResponse<CarResponse> cars = new PageResponse<>();
        if ("car_search".equals(intent.getIntent())) {
            System.out.println("Intent: " + intent.getCarFilter().toString());
            cars =  carService.filterCar(1, intent.getCarFilter());
            if (cars.getTotalElements() == 0) {
                return AIResponse.builder()
                        .message("Không tìm thấy xe nào phù hợp với yêu cầu của bạn. Vui lòng thử lại với các tiêu chí khác.")
                        .results(cars)
                        .intent(intent.getIntent())
                        .build();
            }
        } else if ("assist".equals(intent.getIntent())) {
            // Nếu chỉ hỏi thông tin, trả về một danh sách rỗng
            System.out.println("Intent: " + intent.getIntent());
            cars.setData(new ArrayList<>());
            cars.setTotalElements(0);
        } else {
            return AIResponse.builder()
                    .message("Ý định không hợp lệ. Vui lòng thử lại.")
                    .build();
        }
        return AIResponse.builder()
                .results(cars)
                .message(intent.getMessage())
                .intent(intent.getIntent())
                .build();
    }

    private AIIntent extractIntentFromResponse(String userMessage) throws JsonProcessingException {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
        Map<String, Object> requestBody = Map.of("contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", prompt(userMessage))))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

// Parse kết quả từ Gemini
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
        String json = parts.get(0).get("text");
        String cleanedJson = json.replaceAll("(?s)```json\\s*", "")  // xóa phần mở đầu ```json
                .replaceAll("(?s)```", "")          // xóa phần kết thúc ```
                .trim();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(cleanedJson, AIIntent.class);
    }

    private String prompt(String userMessage) {
        return String.format("""
    Bạn là một trợ lý AI giúp người dùng tìm kiếm xe tự lái để thuê.

    Dưới đây là câu hỏi từ người dùng: "%s"

    Nhiệm vụ của bạn:
    - Phân tích nội dung và trích xuất thông tin để hỗ trợ người dùng tìm kiếm xe phù hợp.
    - Nếu người dùng thể hiện mong muốn thuê, tìm xe, hoặc có nhu cầu chung chung (như "Tư vấn xe", "Có xe nào không?") thì `intent` sẽ là `"car_search"`.
    - Nếu người dùng **đề cập đến địa điểm (location)** thì cũng được xem là có ý định tìm xe, và `intent` phải là `"car_search"` ngay cả khi không có thông tin khác.
    - Nếu người dùng chỉ hỏi thông tin không liên quan đến thuê xe, dùng `intent = "assist"`.
    - Nếu location được đề cập, bạn cần format là location, ví dụ: "Hà Nội" -> "hanoi"; "tp HCM" -> "hochiminh" ; vv...
    Trả về một đối tượng JSON hợp lệ với **3 field chính**:
    {
        "intent": "car_search" hoặc "assist",
        "message": "Câu trả lời phản hồi lại người dùng. Cần rõ ràng, thân thiện và định hướng nếu cần.",
        "carFilter": {
            "brand": "hãng xe nếu có (ví dụ: 'Toyota', '')",
            "model": "dòng xe nếu có (ví dụ: 'Vios', '')",
            "color": "màu xe nếu có (ví dụ: 'đen', '')",
            "fuelType": "LOAI_NHIEN_LIEU (ví dụ: 'GASOLINE', 'ELECTRIC'), null nếu không có",
            "fuelConsumption": số_thập_phân, // Lít/100km. Nếu không đề cập thì là null
            "transmissionType": "LOAI_HOP_SO (ví dụ: 'AUTOMATIC', 'MANUAL'), null nếu không có",
            "type": "LOAI_XE (ví dụ: 'SUV', 'SEDAN'), null nếu không có",
            "minPrice": số_nguyên, // VND
            "maxPrice": số_nguyên,
            "minSeats": số_nguyên,
            "maxSeats": số_nguyên,
            "rating": số_thập_phân, null nếu không có,
            "features": "danh sách tiện ích nếu có, cách nhau bởi dấu phẩy, ví dụ: 'GPS,CAMERA,BLUETOOTH', rỗng nếu không có",
            "location": "địa điểm thuê nếu có (ví dụ: 'Hà Nội', '')",
            "sortBy": "tiêu chí sắp xếp, ví dụ: 'priceAsc', 'ratingDesc', ''",
            "deliveryAvailable": true hoặc false, null nếu không xác định
        }
    }

    **Yêu cầu bắt buộc:**
    - JSON phải hợp lệ hoàn toàn, không kèm giải thích, không có văn bản ngoài JSON.
    - Với các trường không được người dùng đề cập: null
    - Trường `intent` phải là `"car_search"` nếu người dùng có ý định thuê xe hoặc có đề cập đến `location`, ngược lại là `"assist"`.
    - Chỉ cần có location, các trường khác có thể để null.
    - Field `message` cần rõ ràng, thân thiện, và có thể gợi ý thêm nếu câu hỏi chung chung.
    """, userMessage);
    }




}
