package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.dto.WebResponse;
import com.gayuh.personalproject.entity.Topic;
import com.gayuh.personalproject.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${SEEDER_TOKEN}")
    private String seederToken;

    private final Topic staticTopic = new Topic();

    @BeforeEach
    void setUp() {
        topicRepository.deleteAllExceptSeeders();
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
            WebResponse<List<MasterResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Data found with size 12", response.getMessage());
            assertNotNull(response.getData());
            assertEquals(12, response.getData().size());
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
            WebResponse<MasterResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
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
        MasterRequest request = new MasterRequest("Test 2");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/topics")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<MasterResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Success create new data", response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Test 2", response.getData().name());
        });
    }

    @Test
    void createTopicBadRequest() throws Exception {
        MasterRequest request = new MasterRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/topics")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, MasterResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
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

        MasterRequest request = new MasterRequest("Test Update");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/topics/" + topic.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<MasterResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Success update data", response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Test Update", response.getData().name());
        });
    }

    @Test
    void updateTopicBadRequest() throws Exception {
        Topic topic = new Topic();
        topic.setName("Test Aja");
        topicRepository.save(topic);

        MasterRequest request = new MasterRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/topics/" + topic.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, MasterResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
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
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Success delete data", response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void deleteTopicNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/v1/topics/-1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
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
