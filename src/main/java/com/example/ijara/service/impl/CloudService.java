package com.example.ijara.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CloudService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String UPLOAD_URL = "https://api.imgbb.com/1/upload";

    @Value("${cloud.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String uploadFile(MultipartFile file) throws IOException {
        validateImageFile(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("image", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    UPLOAD_URL, HttpMethod.POST, requestEntity, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            JsonNode urlNode = data.path("url");

            if (urlNode.isMissingNode() || urlNode.asText().isBlank()) {
                throw new RuntimeException("Rasm yuklanmadi: URL topilmadi");
            }

            return urlNode.asText();

        } catch (RestClientException e) {
            throw new RuntimeException("ImgBB server bilan aloqa xatosi: " + e.getMessage(), e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fayl bo'sh yoki yuklanmagan");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Fayl hajmi 5MB dan oshmasligi kerak");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Faqat rasm formatidagi fayllar qabul qilinadi");
        }
    }
}