package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.TestResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.dto.WebResponse;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TestControllerTest {

    private final List<Choice> choicesStatic = new ArrayList<>();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestHistoryRepository testHistoryRepository;
    @Autowired
    private QuestionTitleRepository questionTitleRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private QuestionTitleService questionTitleService;
    @Autowired
    private UserRepository userRepository;
    private QuestionTitle questionTitleStatic;
    private List<String> tempTestId = new ArrayList<>();
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

    @AfterEach
    void tearDown() {
        questionTitleService.deleteQuestionTitle(questionTitleStatic.getId(), new UserObject(
                "74d56e1d-577c-44d2-80cd-347909005da0",
                null,
                null
        ));
    }

    @Test
    void startTestSuccess() throws Exception {
        mockMvc.perform(
                post("/api/v1/question-titles/" + questionTitleStatic.getId() + "/tests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("Start test success", response.getMessage());
            assertNotNull(response.getData());
            assertEquals(1, response.getData().size());

            tempTestId = response.getData();
        });
    }

    @Test
    void startTestNotFound() throws Exception {
        mockMvc.perform(
                post("/api/v1/question-titles/undefined/tests")
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
    void doTestSuccess() throws Exception {
        startTestSuccess();

        mockMvc.perform(
                get("/api/v1/tests/" + tempTestId.get(0))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TestResponse, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.GET_DATA.value(), response.getMessage());
            assertNotNull(response.getData());
            assertEquals("Test question", response.getData().questionText());
        });
    }

    @Test
    void doTestNotFound() throws Exception {
        mockMvc.perform(
                get("/api/v1/tests/undefined")
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
    void doTestExpired() throws Exception {
        startTestSuccess();

        var testHistoryPage = testHistoryRepository.findAllTestHistoryByQuestionTitleId(
                questionTitleStatic.getId(), "%%", PageRequest.of(0, 1));

        var testHistoryId = testHistoryPage.get().findFirst().get().testHistoryId();

        var testHistory = testHistoryRepository.findById(testHistoryId).orElseThrow();

        testHistory.setFinishedAt(LocalDateTime.now().minusMinutes(1L));
        testHistoryRepository.save(testHistory);

        mockMvc.perform(
                get("/api/v1/tests/" + tempTestId.get(0))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("Test already finished", response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void answerTestSuccess() throws Exception {
        startTestSuccess();

        mockMvc.perform(
                put("/api/v1/tests/" + tempTestId.get(0) + "/choices/" + choicesStatic.get(1).getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.UPDATE_DATA.value(), response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void answerTestNotFound() throws Exception {
        startTestSuccess();

        mockMvc.perform(
                put("/api/v1/tests/undefined/choices/" + choicesStatic.get(1).getId())
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
    void answerTestExpired() throws Exception {
        startTestSuccess();

        var testHistoryPage = testHistoryRepository.findAllTestHistoryByQuestionTitleId(
                questionTitleStatic.getId(), "%%", PageRequest.of(0, 1));

        var testHistoryId = testHistoryPage.get().findFirst().get().testHistoryId();

        var testHistory = testHistoryRepository.findById(testHistoryId).orElseThrow();

        testHistory.setFinishedAt(LocalDateTime.now().minusMinutes(1L));
        testHistoryRepository.save(testHistory);

        mockMvc.perform(
                put("/api/v1/tests/" + tempTestId.get(0) + "/choices/" + choicesStatic.get(1).getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("Test already finished", response.getMessage());
            assertNull(response.getData());
        });
    }

    @Test
    void finishTestSuccess() throws Exception {
        startTestSuccess();

        mockMvc.perform(
                put("/api/v1/tests/" + tempTestId.get(0) + "/finish")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<Map<String, String>, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("Success finish test", response.getMessage());
            assertNotNull(response.getData().get("link"));
        });
    }

    @Test
    void finishTestNotFound() throws Exception {
        startTestSuccess();

        mockMvc.perform(
                put("/api/v1/tests/undefined/finish")
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
    void finishTestFinished() throws Exception {
        startTestSuccess();

        var testHistoryPage = testHistoryRepository.findAllTestHistoryByQuestionTitleId(
                questionTitleStatic.getId(), "%%", PageRequest.of(0, 1));

        var testHistoryId = testHistoryPage.get().findFirst().get().testHistoryId();

        var testHistory = testHistoryRepository.findById(testHistoryId).orElseThrow();

        testHistory.setFinishedAt(LocalDateTime.now().minusMinutes(1L));
        testHistoryRepository.save(testHistory);

        mockMvc.perform(
                put("/api/v1/tests/" + tempTestId.get(0) + "/finish")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", seederToken)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("Test already finished", response.getMessage());
            assertNull(response.getData());
        });
    }

}
