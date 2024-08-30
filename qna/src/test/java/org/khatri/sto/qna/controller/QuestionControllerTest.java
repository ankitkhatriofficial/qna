package org.khatri.sto.qna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.khatri.sto.qna.constant.Constants;
import org.khatri.sto.qna.dto.QuestionDto;
import org.khatri.sto.qna.dto.response.GenericPaginatedResponse;
import org.khatri.sto.qna.service.QuestionService;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ankit Khatri
 */

@ExtendWith(MockitoExtension.class)
@WebMvcTest(QuestionController.class)
public class QuestionControllerTest {

    @MockBean private QuestionService questionService;
    @InjectMocks private QuestionController questionController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
    }

    @Test
    public void testCreateNewQuestion() throws Exception {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle("Sample Question");
        questionDto.setContent("This is a sample question content.");
        QuestionDto createdQuestionDto = new QuestionDto();
        createdQuestionDto.setTitle("Sample Question");
        createdQuestionDto.setContent("This is a sample question content.");
        when(questionService.createNewQuestion(eq(1L), any(QuestionDto.class))).thenReturn(createdQuestionDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/ques/create")
                        .header(Constants.USER_ID_ATTR, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(questionDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(asJsonString(createdQuestionDto)));
    }

    @Test
    public void testGetAllQuestionsOfUser() throws Exception {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle("Sample Question");
        questionDto.setContent("This is a sample question content.");
        GenericPaginatedResponse<QuestionDto> questions = new GenericPaginatedResponse<>();
        questions.setData(List.of(questionDto));
        when(questionService.getAllUserQuestions(0, 5, "createdAt", "desc", 1L)).thenReturn(questions);
        mockMvc.perform(MockMvcRequestBuilders.get("/ques/get-all")
                        .header(Constants.USER_ID_ATTR, 1L)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "createdAt")
                        .param("direction", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(questions)));
    }

    @Test
    public void testGetQuestionById() throws Exception {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle("Sample Question");
        questionDto.setContent("This is a sample question content.");
        when(questionService.findQuestionById(1L, 1L)).thenReturn(questionDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/ques/get/1")
                        .header(Constants.USER_ID_ATTR, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(questionDto)));
    }

    @Test
    public void testUpdateExistingQuestion() throws Exception {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle("Updated Question");
        questionDto.setContent("This is an updated question content.");
        QuestionDto updatedQuestionDto = new QuestionDto();
        updatedQuestionDto.setTitle("Updated Question");
        updatedQuestionDto.setContent("This is an updated question content.");
        when(questionService.modifyExistingQuestion(eq(1L), eq(1L), any(QuestionDto.class))).thenReturn(updatedQuestionDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/ques/modify/1")
                        .header(Constants.USER_ID_ATTR, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(questionDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(updatedQuestionDto)));
    }

    @Test
    public void testDeleteQuestionId() throws Exception {
        doNothing().when(questionService).deleteQuestion(1L, 1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/ques/delete/1")
                        .header(Constants.USER_ID_ATTR, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
