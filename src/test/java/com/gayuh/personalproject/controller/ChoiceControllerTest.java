package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.ChoiceRequest;
import com.gayuh.personalproject.dto.ChoiceResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.dto.WebResponse;
import com.gayuh.personalproject.entity.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.query.UserQuery;
import com.gayuh.personalproject.repository.ChoiceRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ChoiceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuestionTitleService questionTitleService;
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionTitleRepository questionTitleRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Value("${SEEDER_TOKEN}")
    private String seederToken;
    private QuestionTitle questionTitleStatic;
    private Question questionStatic;

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

        questionStatic = Question.builder()
                .score(20)
                .time(20)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .questionText("Test question")
                .questionTitle(questionTitleStatic)
                .build();
        questionRepository.save(questionStatic);
    }

    @Test
    void createChoiceSuccess() throws Exception {
        ChoiceRequest request = new ChoiceRequest(
                "Test choice",
                false
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + questionStatic.getId() + "/choices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.CREATE_DATA.value(), response.getMessage());
            assertNull(response.getData());

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void createChoiceBadRequest() throws Exception {
        ChoiceRequest request = new ChoiceRequest(
                "",
                null
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + questionStatic.getId() + "/choices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, Map<String, String>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("must not be blank", response.getMessage().get("choiceText"));
            assertEquals("must not be null", response.getMessage().get("correct"));
            assertNull(response.getData());

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void updateChoiceSuccess() throws Exception {
        var choice = choiceRepository.save(Choice.builder()
                .choiceText("Test 1")
                .correct(false)
                .question(questionStatic)
                .build());

        ChoiceRequest request = new ChoiceRequest(
                "Test update",
                true
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + questionStatic.getId() + "/choices/" + choice.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.UPDATE_DATA.value(), response.getMessage());
            assertNull(response.getData());

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void updateChoiceBadRequest() throws Exception {
        var choice = choiceRepository.save(Choice.builder()
                .choiceText("Test 1")
                .correct(false)
                .question(questionStatic)
                .build());

        ChoiceRequest request = new ChoiceRequest(
                "",
                true
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + questionStatic.getId() + "/choices/" + choice.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, Map<String, String>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("must not be blank", response.getMessage().get("choiceText"));
            assertNull(response.getData());

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void updateChoiceNotFound() throws Exception {
        ChoiceRequest request = new ChoiceRequest(
                "Haiyaa",
                true
        );

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + questionStatic.getId() + "/choices/-1")
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

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void getListChoice() throws Exception {

        List<Choice> choices = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            var choice = Choice.builder()
                    .correct(false)
                    .question(questionStatic)
                    .choiceText("test choice ke-" + i)
                    .build();
            choices.add(choice);
        }

        choiceRepository.saveAll(choices);

        mockMvc.perform(
                get("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + questionStatic.getId() + "/choices")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ChoiceResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_ALL_DATA.value() + "4", response.getMessage());
            assertNotNull(response.getData());

            deleteQuestionTitle(questionTitleStatic.getId());
        });
    }

    @Test
    void deleteChoiceSuccess() throws Exception {

        Choice choice = choiceRepository.save(Choice.builder()
                .correct(false)
                .question(questionStatic)
                .choiceText("test choice")
                .build());

        mockMvc.perform(
                delete("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + questionStatic.getId() + "/choices/" + choice.getId())
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
    void deleteChoiceNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/v1/question-titles/" + questionTitleStatic.getId() + "/questions/" + questionStatic.getId() + "/choices/-1")
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
