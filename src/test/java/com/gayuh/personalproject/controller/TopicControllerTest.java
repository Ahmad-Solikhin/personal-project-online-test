package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.TopicRequest;
import com.gayuh.personalproject.dto.TopicResponse;
import com.gayuh.personalproject.dto.WebResponse;
import com.gayuh.personalproject.entity.Topic;
import com.gayuh.personalproject.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final Topic staticTopic = new Topic();

    @BeforeEach
    void setUp() {
        topicRepository.deleteAll();
    }

    @Test
    void getAll() throws Exception {

        List<Topic> topics = new ArrayList<>();

        Topic topic;

        for (int i = 0; i < 10; i++) {
            topic = new Topic();
            topic.setName("Topic ke-" + (i + 1));
            topics.add(topic);
        }

        topicRepository.saveAll(topics);

        mockMvc.perform(
                get("/api/v1/topics")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<TopicResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Data found with size 10", response.getMessage());
            assertNotNull(response.getData());
            assertEquals(10, response.getData().size());
        });
    }

    @Test
    void getByIdSuccess() throws Exception {
        staticTopic.setName("Test");
        topicRepository.save(staticTopic);

        mockMvc.perform(
                get("/api/v1/topics/" + staticTopic.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TopicResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Data found", response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Test", response.getData().name());
        });
    }

    @Test
    void getByIdNotFound() throws Exception {

        mockMvc.perform(
                get("/api/v1/topics/-1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Data not found", response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void createTopicSuccess() throws Exception {
        TopicRequest request = new TopicRequest("Test 2");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/topics")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<TopicResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Success add new topic", response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Test 2", response.getData().name());
        });
    }

    @Test
    void createTopicBadRequest() throws Exception {
        TopicRequest request = new TopicRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/topics")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, TopicResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("must not be blank", response.getMessage().name());
            assertNull(response.getData());
        });
    }

    @Test
    void updateTopicSuccess() throws Exception {
        Topic topic = new Topic();
        topic.setName("Test Aja");
        topicRepository.save(topic);

        TopicRequest request = new TopicRequest("Test Update");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/topics/" + topic.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TopicResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Success update topic", response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Test Update", response.getData().name());
        });
    }

    @Test
    void updateTopicBadRequest() throws Exception {
        Topic topic = new Topic();
        topic.setName("Test Aja");
        topicRepository.save(topic);

        TopicRequest request = new TopicRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/topics/" + topic.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, TopicResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("must not be blank", response.getMessage().name());
            assertNull(response.getData());
        });
    }

    @Test
    void deleteTopicSuccess() throws Exception {
        Topic topic = new Topic();
        topic.setName("Test Aja");
        topicRepository.save(topic);

        mockMvc.perform(
                delete("/api/v1/topics/" + topic.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNoContent()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Success delete topic", response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void deleteTopicNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/v1/topics/-1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Data not found", response.getMessage());
            assertNull(response.getData());
        });
    }


}
