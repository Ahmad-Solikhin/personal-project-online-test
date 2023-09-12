package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.entity.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.query.UserQuery;
import com.gayuh.personalproject.repository.*;
import com.gayuh.personalproject.service.questiontitle.QuestionTitleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TestHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestHistoryRepository testHistoryRepository;
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionTitleService questionTitleService;
    @Autowired
    private QuestionTitleRepository questionTitleRepository;
    private QuestionTitle questionTitleStatic;
    private List<String> tempTestId;
    @Value("${SEEDER_TOKEN}")
    private String seederToken;


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

        Question questionStatic = Question.builder()
                .score(20)
                .time(20)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .questionText("Test question")
                .questionTitle(questionTitleStatic)
                .build();
        questionRepository.save(questionStatic);

        List<Choice> choicesStatic = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            var choice = Choice.builder()
                    .choiceText("Choice ke-" + i)
                    .question(questionStatic)
                    .correct(i == 1)
                    .build();
            choicesStatic.add(choice);
        }
        choiceRepository.saveAll(choicesStatic);
    }

    @Test
    void getAllTestHistoryByQuestionTitleIdSuccess() throws Exception {
        finishTestSuccess();

        mockMvc.perform(
                get("/api/v1/question-titles/" + questionTitleStatic.getId() + "/test-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<PaginationResponse<UserTestHistoryResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_ALL_DATA.value() + response.getData().result().size(), response.getMessage());
            assertNotNull(response.getData());
            assertEquals("admin@gmail.com", response.getData().result().get(0).email());
        });
    }

    @Test
    void getAllHistoryByUserId() throws Exception {
        finishTestSuccess();

        mockMvc.perform(
                get("/api/v1/test-histories")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<PaginationResponse<TestHistoryResponse>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_ALL_DATA.value() + response.getData().result().size(), response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Programming", response.getData().result().get(0).topic());
        });
    }

    @Test
    void getTestHistoryDetailSuccess() throws Exception {
        finishTestSuccess();

        var testHistoryPage = testHistoryRepository.findAllTestHistoryByQuestionTitleId(
                questionTitleStatic.getId(), "%%", PageRequest.of(0, 1));

        var testHistoryId = testHistoryPage.get().findFirst().get().testHistoryId();

        mockMvc.perform(
                get("/api/v1/test-histories/" + testHistoryId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TestHistoryDetailResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_DATA.value(), response.getMessage());
            assertNotNull(response.getData());
            assertEquals("admin@gmail.com", response.getData().email());
        });
    }

    @Test
    void getTestHistoryDetailNotFound() throws Exception {
        finishTestSuccess();

        mockMvc.perform(
                get("/api/v1/test-histories/undefined")
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

    private void startTestSuccess() throws Exception {
        mockMvc.perform(
                post("/api/v1/question-titles/" + questionTitleStatic.getId() + "/tests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andDo(result -> {
            WebResponse<List<String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            tempTestId = response.getData();
        });
    }

    void finishTestSuccess() throws Exception {
        startTestSuccess();

        mockMvc.perform(
                put("/api/v1/tests/" + tempTestId.get(0) + "/finish")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        );
    }

    @AfterEach
    void tearDown() {
        questionTitleService.deleteQuestionTitle(questionTitleStatic.getId(), new UserObject(
                "74d56e1d-577c-44d2-80cd-347909005da0",
                null,
                null
        ));
    }
}
