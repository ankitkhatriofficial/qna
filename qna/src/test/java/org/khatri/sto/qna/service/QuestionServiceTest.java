package org.khatri.sto.qna.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.khatri.sto.qna.RefType;
import org.khatri.sto.qna.dto.MediaDto;
import org.khatri.sto.qna.dto.QuestionDto;
import org.khatri.sto.qna.dto.external.TagMappingDto;
import org.khatri.sto.qna.entity.Media;
import org.khatri.sto.qna.entity.Question;
import org.khatri.sto.qna.external.TagService;
import org.khatri.sto.qna.repository.MediaRepository;
import org.khatri.sto.qna.repository.QuestionRepository;
import org.khatri.sto.qna.repository.UpVoteRepository;
import org.khatri.sto.qna.service.impl.QuestionServiceImpl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ankit Khatri
 */
@SpringBootTest
public class QuestionServiceTest {

    @Mock private QuestionRepository questionRepository;
    @Mock private TagService tagService;
    @Mock private UpVoteRepository upVoteRepository;
    @Mock private MediaRepository mediaRepository;
    @InjectMocks private QuestionServiceImpl questionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @Transactional
    public void testCreateNewQuestionWithMediasAndTags() {
        Long userId = 1L;
        QuestionDto questionDto = new QuestionDto();
        questionDto.setMedias(List.of(new MediaDto(Media.MediaType.VIDEO, "https://example.com/video")));
        questionDto.setTags(List.of("java", "spring"));
        Question question = new Question();
        question.setId(1L);
        when(questionRepository.saveAndFlush(any(Question.class))).thenReturn(question);
        when(mediaRepository.saveAllAndFlush(anyList())).thenReturn(Collections.emptyList());
        doNothing().when(tagService).mapTagWithReference(anyLong(), any(RefType.class), anyList());
        QuestionDto result = questionService.createNewQuestion(userId, questionDto);
        assertNotNull(result);
        verify(questionRepository).saveAndFlush(any(Question.class));
        verify(mediaRepository).saveAllAndFlush(anyList());
        verify(tagService).mapTagWithReference(eq(question.getId()), eq(RefType.QUESTION), anyList());
    }

    @Test
    @Transactional
    public void testCreateNewQuestionWithMediasOnly() {
        Long userId = 1L;
        QuestionDto questionDto = new QuestionDto();
        questionDto.setMedias(List.of(new MediaDto(Media.MediaType.PHOTO, "https://example.com/photo")));
        questionDto.setTags(Collections.emptyList());
        Question question = new Question();
        question.setId(1L);
        when(questionRepository.saveAndFlush(any(Question.class))).thenReturn(question);
        when(mediaRepository.saveAllAndFlush(anyList())).thenReturn(Collections.emptyList());
        doNothing().when(tagService).mapTagWithReference(anyLong(), any(RefType.class), anyList());
        QuestionDto result = questionService.createNewQuestion(userId, questionDto);
        assertNotNull(result);
        verify(questionRepository).saveAndFlush(any(Question.class));
        verify(mediaRepository).saveAllAndFlush(anyList());
        verify(tagService, never()).mapTagWithReference(anyLong(), any(RefType.class), anyList());
    }

    @Test
    @Transactional
    public void testCreateNewQuestionWithTagsOnly() {
        Long userId = 1L;
        QuestionDto questionDto = new QuestionDto();
        questionDto.setMedias(Collections.emptyList());
        questionDto.setTags(List.of("java", "hibernate"));
        Question question = new Question();
        question.setId(1L);
        when(questionRepository.saveAndFlush(any(Question.class))).thenReturn(question);
        doNothing().when(tagService).mapTagWithReference(anyLong(), any(RefType.class), anyList());
        QuestionDto result = questionService.createNewQuestion(userId, questionDto);
        assertNotNull(result);
        verify(questionRepository).saveAndFlush(any(Question.class));
        verify(mediaRepository, never()).saveAllAndFlush(anyList());
        verify(tagService).mapTagWithReference(eq(question.getId()), eq(RefType.QUESTION), anyList());
    }

    @Test
    @Transactional
    public void testCreateNewQuestionWithNoMediasAndTags() {
        Long userId = 1L;
        QuestionDto questionDto = new QuestionDto();
        questionDto.setMedias(Collections.emptyList());
        questionDto.setTags(Collections.emptyList());
        Question question = new Question();
        question.setId(1L);
        when(questionRepository.saveAndFlush(any(Question.class))).thenReturn(question);
        doNothing().when(tagService).mapTagWithReference(anyLong(), any(RefType.class), anyList());
        QuestionDto result = questionService.createNewQuestion(userId, questionDto);
        assertNotNull(result);
        verify(questionRepository).saveAndFlush(any(Question.class));
        verify(mediaRepository, never()).saveAllAndFlush(anyList());
        verify(tagService, never()).mapTagWithReference(anyLong(), any(RefType.class), anyList());
    }

    @Test
    public void testSearchQuestionByTagsWithNoResults() {
        TagMappingDto tagMappingDto = TagMappingDto.builder().tags(List.of("java", "exception")).build();
        List<TagMappingDto> tagMappings = List.of(tagMappingDto);
        Question question = Question.builder()
                .id(1L).userId(1L).title("Sample question").content("This is a sample question content").build();
        List<Question> questions = List.of(question);
        when(tagService.findRefIdByTags(any(), eq(RefType.QUESTION))).thenReturn(tagMappings);
        when(questionRepository.findAllById(eq(List.of(1L)))).thenReturn(questions);
        List<QuestionDto> result = questionService.searchQuestionByTags(new Object());
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(tagService).findRefIdByTags(any(), eq(RefType.QUESTION));
    }



}
