package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.repository.*;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class QuestionTitleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionTitleRepository questionTitleRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserVerifyRepository userVerifyRepository;
    @Autowired
    private ForgetPasswordRepository forgetPasswordRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestHistoryRepository testHistoryRepository;
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private MediaRepository mediaRepository;
    private String staticLinkQuestionTitleById;

    @Value("${SEEDER_TOKEN}")
    private String seederToken;

    @BeforeEach
    void setUp() {
        userVerifyRepository.deleteAll();
        forgetPasswordRepository.deleteAll();
        testRepository.deleteAll();
        testHistoryRepository.deleteAll();
        choiceRepository.deleteAll();
        mediaRepository.deleteAll();
        questionRepository.deleteAll();
        questionTitleRepository.deleteAll();
        userRepository.deleteAllExceptSeeder();
    }

    @Test
    void createQuestionTitleSuccess() throws Exception {
        QuestionTitleRequest request = new QuestionTitleRequest(
                "Test your programming skills",
                1L,
                1L,
                1L
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/question-titles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<Map<String, String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(response.getMessage(), ResponseMessage.CREATE_DATA.value());
            assertNotNull(response.getData().get("link"));

            staticLinkQuestionTitleById = response.getData().get("link");
        });
    }

    @Test
    void createQuestionTitleBadRequest() throws Exception {
        QuestionTitleRequest request = new QuestionTitleRequest(
                "",
                1L,
                1L,
                1L
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/question-titles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, QuestionTitleRequest> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("must not be blank", response.getMessage().title());
            assertNull(response.getData());
        });
    }

    @Test
    void createQuestionTitleUnauthorize() throws Exception {
        QuestionTitleRequest request = new QuestionTitleRequest(
                "Test",
                1L,
                1L,
                1L
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/question-titles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.UNAUTHORIZED.value(), response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void getAllQuestionTitlePublic() throws Exception {
        createQuestionTitleSuccess();

        mockMvc.perform(
                get("/api/v1/question-titles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<PaginationResponse<QuestionTitleResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertEquals(1, response.getData().currentPage());
            assertEquals(1, response.getData().element());
            assertEquals("Test your programming skills", response.getData().result().get(0).title());
            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_ALL_DATA.value() + "1", response.getMessage());
        });
    }

    @Test
    void getAllQuestionTitlePublicEmpty() throws Exception {
        createQuestionTitleSuccess();

        mockMvc.perform(
                get("/api/v1/question-titles")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "2")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<PaginationResponse<QuestionTitleResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getData());
            assertEquals(2, response.getData().currentPage());
            assertEquals(1, response.getData().element());
            assertTrue(response.getData().result().isEmpty());
            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_ALL_DATA.value() + "0", response.getMessage());
        });
    }

    @Test
    void getQuestionTitleByIdFound() throws Exception {
        createQuestionTitleSuccess();

        mockMvc.perform(
                get(staticLinkQuestionTitleById)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<QuestionTitleDetailResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_DATA.value(), response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Easy", response.getData().difficulty());
            assertEquals("Public", response.getData().access());
        });
    }

    @Test
    void getQuestionTitleByIdNotFound() throws Exception {
        mockMvc.perform(
                get("/api/v1/question-titles/undefined")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.DATA_NOT_FOUND.value(), response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void updateQuestionTitleSuccess() throws Exception {
        createQuestionTitleSuccess();

        QuestionTitleRequest request = new QuestionTitleRequest(
                "Update ges", 1L, 1L, 1L
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put(staticLinkQuestionTitleById)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<Map<String, String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.UPDATE_DATA.value(), response.getMessage());
            assertNotNull(response.getData().get("link"));
        });
    }

    @Test
    void updateQuestionTitleBadRequest() throws Exception {
        createQuestionTitleSuccess();

        QuestionTitleRequest request = new QuestionTitleRequest(
                "", 1L, 1L, 1L
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put(staticLinkQuestionTitleById)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, QuestionTitleRequest> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("must not be blank", response.getMessage().title());
            assertNull(response.getData());
        });
    }

    @Test
    void updateQuestionTitleUnauthorize() throws Exception {
        createQuestionTitleSuccess();

        QuestionTitleRequest request = new QuestionTitleRequest(
                "Test", 1L, 1L, 1L
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put(staticLinkQuestionTitleById)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.UNAUTHORIZED.value(), response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void updateQuestionTitleNotFound() throws Exception {
        QuestionTitleRequest request = new QuestionTitleRequest(
                "Test", 1L, 1L, 1L
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/question-titles/undefined")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.DATA_NOT_FOUND.value(), response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void getQuestionTitleCreatedByUser() throws Exception {
        createQuestionTitleSuccess();

        mockMvc.perform(
                get("/api/v1/question-titles/histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<PaginationResponse<QuestionTitleResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_ALL_DATA.value() + "1", response.getMessage());
            assertNotNull(response.getData());
        });
    }

    @Test
    void deleteQuestionTitleSuccess() throws Exception {
        createQuestionTitleSuccess();

        mockMvc.perform(
                delete(staticLinkQuestionTitleById)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.DELETE_DATA.value(), response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void deleteQuestionTitleNotFound() throws Exception {
        createQuestionTitleSuccess();

        mockMvc.perform(
                delete("/api/v1/question-titles/undefined")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.DATA_NOT_FOUND.value(), response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void sharePrivateQuestion() throws Exception {
        createQuestionTitleSuccess();

        mockMvc.perform(
                patch(staticLinkQuestionTitleById + "/share")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("generate", "true")
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<Map<String, String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("Success share question title", response.getMessage());
            assertNotNull(response.getData());
        });
    }
}
