package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.dto.WebResponse;
import com.gayuh.personalproject.entity.Difficulty;
import com.gayuh.personalproject.repository.DifficultyRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class DifficultyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DifficultyRepository difficultyRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final Difficulty staticDifficulty = new Difficulty();

    @BeforeEach
    void setUp() {
        difficultyRepository.deleteAllExceptSeeders();
    }

    @Test
    void getAll() throws Exception {

        List<Difficulty> difficulties = new ArrayList<>();

        Difficulty difficulty;

        for (int i = 0; i < 10; i++) {
            difficulty = new Difficulty();
            difficulty.setName("Difficulty ke-" + (i + 1));
            difficulties.add(difficulty);
        }

        difficultyRepository.saveAll(difficulties);

        mockMvc.perform(
                get("/api/v1/difficulties")
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
        staticDifficulty.setName("Test");
        difficultyRepository.save(staticDifficulty);

        mockMvc.perform(
                get("/api/v1/difficulties/" + staticDifficulty.getId())
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
                get("/api/v1/difficulties/-1")
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
    void createDifficultySuccess() throws Exception {
        MasterRequest request = new MasterRequest("Test 2");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/difficulties")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
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
    void createDifficultyBadRequest() throws Exception {
        MasterRequest request = new MasterRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/difficulties")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
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
    void updateDifficultySuccess() throws Exception {
        Difficulty role = new Difficulty();
        role.setName("Test Aja");
        difficultyRepository.save(role);

        MasterRequest request = new MasterRequest("Test Update");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/difficulties/" + role.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
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
    void updateDifficultyBadRequest() throws Exception {
        Difficulty role = new Difficulty();
        role.setName("Test Aja");
        difficultyRepository.save(role);

        MasterRequest request = new MasterRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/difficulties/" + role.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
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
    void deleteDifficultySuccess() throws Exception {
        Difficulty role = new Difficulty();
        role.setName("Test Aja");
        difficultyRepository.save(role);

        mockMvc.perform(
                delete("/api/v1/difficulties/" + role.getId())
                        .accept(MediaType.APPLICATION_JSON)
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
    void deleteDifficultyNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/v1/difficulties/-1")
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