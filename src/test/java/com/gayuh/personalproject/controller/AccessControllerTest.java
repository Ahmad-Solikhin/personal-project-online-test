package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.dto.WebResponse;
import com.gayuh.personalproject.entity.Access;
import com.gayuh.personalproject.repository.AccessRepository;
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
class AccessControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final Access staticAccess = new Access();

    @BeforeEach
    void setUp() {
        accessRepository.deleteAll();
    }

    @Test
    void getAll() throws Exception {

        List<Access> accesses = new ArrayList<>();

        Access access;

        for (int i = 0; i < 10; i++) {
            access = new Access();
            access.setName("Access ke-" + (i + 1));
            accesses.add(access);
        }

        accessRepository.saveAll(accesses);

        mockMvc.perform(
                get("/api/v1/accesses")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<MasterResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Data found with size 10", response.getMessage());
            assertNotNull(response.getData());
            assertEquals(10, response.getData().size());
        });
    }

    @Test
    void getByIdSuccess() throws Exception {
        staticAccess.setName("Test");
        accessRepository.save(staticAccess);

        mockMvc.perform(
                get("/api/v1/accesses/" + staticAccess.getId())
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
                get("/api/v1/accesses/-1")
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
    void createAccessSuccess() throws Exception {
        MasterRequest request = new MasterRequest("Test 2");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/accesses")
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
    void createAccessBadRequest() throws Exception {
        MasterRequest request = new MasterRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/accesses")
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
    void updateAccessSuccess() throws Exception {
        Access access = new Access();
        access.setName("Test Aja");
        accessRepository.save(access);

        MasterRequest request = new MasterRequest("Test Update");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/accesses/" + access.getId())
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
    void updateAccessBadRequest() throws Exception {
        Access access = new Access();
        access.setName("Test Aja");
        accessRepository.save(access);

        MasterRequest request = new MasterRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/accesses/" + access.getId())
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
    void deleteAccessSuccess() throws Exception {
        Access access = new Access();
        access.setName("Test Aja");
        accessRepository.save(access);

        mockMvc.perform(
                delete("/api/v1/accesses/" + access.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNoContent()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Success delete data", response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void deleteAccessNotFound() throws Exception {
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