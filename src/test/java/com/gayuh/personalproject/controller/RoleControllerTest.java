package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.dto.WebResponse;
import com.gayuh.personalproject.entity.Role;
import com.gayuh.personalproject.repository.*;
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
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ForgetPasswordRepository forgetPasswordRepository;
    @Autowired
    private UserVerifyRepository userVerifyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuestionTitleRepository questionTitleRepository;

    private final Role staticRole = new Role();

    @BeforeEach
    void setUp() {
        forgetPasswordRepository.deleteAll();
        userVerifyRepository.deleteAll();
        questionTitleRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAllExceptSeeders();
    }

    @Test
    void getAll() throws Exception {

        List<Role> roles = new ArrayList<>();

        Role role;

        for (int i = 0; i < 10; i++) {
            role = new Role();
            role.setName("Role ke-" + (i + 1));
            roles.add(role);
        }

        roleRepository.saveAll(roles);

        mockMvc.perform(
                get("/api/v1/roles")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<MasterResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Data found with size 13", response.getMessage());
            assertNotNull(response.getData());
            assertEquals(13, response.getData().size());
        });
    }

    @Test
    void getByIdSuccess() throws Exception {
        staticRole.setName("Test");
        roleRepository.save(staticRole);

        mockMvc.perform(
                get("/api/v1/roles/" + staticRole.getId())
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
                get("/api/v1/roles/-1")
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
    void createRoleSuccess() throws Exception {
        MasterRequest request = new MasterRequest("Test 2");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/roles")
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
    void createRoleBadRequest() throws Exception {
        MasterRequest request = new MasterRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/roles")
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
    void updateRoleSuccess() throws Exception {
        Role role = new Role();
        role.setName("Test Aja");
        roleRepository.save(role);

        MasterRequest request = new MasterRequest("Test Update");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/roles/" + role.getId())
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
    void updateRoleBadRequest() throws Exception {
        Role role = new Role();
        role.setName("Test Aja");
        roleRepository.save(role);

        MasterRequest request = new MasterRequest("");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/roles/" + role.getId())
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
    void deleteRoleSuccess() throws Exception {
        Role role = new Role();
        role.setName("Test Aja");
        roleRepository.save(role);

        mockMvc.perform(
                delete("/api/v1/roles/" + role.getId())
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
    void deleteRoleNotFound() throws Exception {
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