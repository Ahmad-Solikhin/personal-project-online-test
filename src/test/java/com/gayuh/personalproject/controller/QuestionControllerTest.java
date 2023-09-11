package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.QuestionRequest;
import com.gayuh.personalproject.dto.QuestionResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.dto.WebResponse;
import com.gayuh.personalproject.entity.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.query.UserQuery;
import com.gayuh.personalproject.repository.QuestionRepository;
import com.gayuh.personalproject.repository.QuestionTitleRepository;
import com.gayuh.personalproject.repository.UserRepository;
import com.gayuh.personalproject.service.questiontitle.QuestionTitleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuestionTitleRepository questionTitleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionTitleService questionTitleService;
    @Autowired
    private QuestionRepository questionRepository;
    @Value("${SEEDER_TOKEN}")
    private String seederToken;
    private QuestionTitle questionTitleStatic;

    @BeforeEach
    void setUp() {
        UserQuery user = userRepository.findUserQueryByEmail("admin@gmail.com").orElseThrow();
        questionTitleStatic = QuestionTitle.builder()
                .updatedAt(LocalDateTime.now())
                .title("Test")
                .createdAt(LocalDateTime.now())
                .topic(Topic.builder().id(1L).build())
                .difficulty(Difficulty.builder().id(1L).build())
                .access(Access.builder().id(1L).build())
                .user(User.builder().id(user.id()).build())
                .build();
        questionTitleRepository.save(questionTitleStatic);
    }

    @Test
    void createQuestionSuccess() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "Test Question",
                20,
                20
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                multipart("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions")
                        .file(new MockMultipartFile("file", "test.png", "image/png",
                                getClass().getResourceAsStream("/src/test/resource/files/test.png")))
                        .file(new MockMultipartFile("data", "", "application/json", requestString.getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<Map<String, String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(response.getMessage(), ResponseMessage.CREATE_DATA.value());
            assertNotNull(response.getData().get("link"));

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void createQuestionSuccessWithoutFile() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "Test Question",
                20,
                20
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                multipart("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions")
                        .file(new MockMultipartFile("data", "", "application/json", requestString.getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<Map<String, String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(response.getMessage(), ResponseMessage.CREATE_DATA.value());
            assertNotNull(response.getData().get("link"));

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void createQuestionBadRequest() throws Exception {
        QuestionRequest request = new QuestionRequest(
                "",
                20,
                20
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                multipart("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions")
                        .file(new MockMultipartFile("data", "", "application/json", requestString.getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, Map<String, String>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("must not be blank", response.getMessage().get("questionText"));

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void updateQuestionSuccess() throws Exception {
        var question = questionRepository.save(Question.builder()
                .questionTitle(questionTitleStatic)
                .questionText("Test")
                .score(20)
                .time(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        QuestionRequest request = new QuestionRequest(
                "Test",
                20,
                20
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                multipart("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + question.getId())
                        .file(new MockMultipartFile("file", "test.png", "image/png",
                                getClass().getResourceAsStream("/src/test/resources/files/test.png")))
                        .file(new MockMultipartFile("data", "", "application/json", requestString.getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", seederToken)
                        .with(pros -> {
                            pros.setMethod("PUT");
                            return pros;
                        })
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<Map<String, String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.UPDATE_DATA.value(), response.getMessage());
            assertNotNull(response.getData().get("link"));

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void updateQuestionSuccessWithoutData() throws Exception {
        var question = questionRepository.save(Question.builder()
                .questionTitle(questionTitleStatic)
                .questionText("Test")
                .score(20)
                .time(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        QuestionRequest request = new QuestionRequest(
                "Test",
                20,
                20
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                multipart("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + question.getId())
                        .file(new MockMultipartFile("data", "", "application/json", requestString.getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", seederToken)
                        .with(pros -> {
                            pros.setMethod("PUT");
                            return pros;
                        })
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<Map<String, String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.UPDATE_DATA.value(), response.getMessage());
            assertNotNull(response.getData().get("link"));

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void updateQuestionBadRequest() throws Exception {
        var question = questionRepository.save(Question.builder()
                .questionTitle(questionTitleStatic)
                .questionText("Test")
                .score(20)
                .time(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        QuestionRequest request = new QuestionRequest(
                "",
                20,
                20
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                multipart("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + question.getId())
                        .file(new MockMultipartFile("data", "", "application/json", requestString.getBytes()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", seederToken)
                        .with(pros -> {
                            pros.setMethod("PUT");
                            return pros;
                        })
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, Map<String, String>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("must not be blank", response.getMessage().get("questionText"));

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void getAllQuestion() throws Exception {
        for (int i = 0; i < 4; i++) {
            var question = Question.builder()
                    .questionTitle(questionTitleStatic)
                    .questionText("Test ke-" + i)
                    .score(10)
                    .time(10)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            questionRepository.save(question);
        }

        mockMvc.perform(
                get("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<QuestionResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_ALL_DATA.value() + "4", response.getMessage());
            assertNotNull(response.getData());
            assertEquals(4, response.getData().size());
            assertNull(response.getData().get(1).mediaLink());

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void deleteQuestionSuccess() throws Exception {
        var question = questionRepository.save(Question.builder()
                .questionTitle(questionTitleStatic)
                .questionText("Test")
                .score(10)
                .time(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(
                delete("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + question.getId())
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

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void deleteQuestionNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/-1")
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

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void getDetailQuestionSuccess() throws Exception {
        var question = questionRepository.save(Question.builder()
                .questionTitle(questionTitleStatic)
                .questionText("Test")
                .score(10)
                .time(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(
                get("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + question.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<QuestionResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_DATA.value(), response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Test", response.getData().questionText());

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void getDetailQuestionNotFound() throws Exception {
        mockMvc.perform(
                get("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/-1")
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

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    private void deleteQuestionTitle(String id) {
        questionTitleService.deleteQuestionTitle(id, new UserObject(
                "74d56e1d-577c-44d2-80cd-347909005da0",
                null,
                null
        ));
    }
}
