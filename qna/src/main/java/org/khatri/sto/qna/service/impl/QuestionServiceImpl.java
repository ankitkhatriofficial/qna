package org.khatri.sto.qna.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.khatri.sto.qna.RefType;
import org.khatri.sto.qna.constant.ExceptionCode;
import org.khatri.sto.qna.converter.QuestionConverter;
import org.khatri.sto.qna.dto.QuestionDto;
import org.khatri.sto.qna.dto.external.TagMappingDto;
import org.khatri.sto.qna.dto.response.GenericPaginatedResponse;
import org.khatri.sto.qna.entity.Media;
import org.khatri.sto.qna.entity.Question;
import org.khatri.sto.qna.entity.UpVote;
import org.khatri.sto.qna.exceptions.InputInvalidException;
import org.khatri.sto.qna.external.TagService;
import org.khatri.sto.qna.repository.MediaRepository;
import org.khatri.sto.qna.repository.QuestionRepository;
import org.khatri.sto.qna.repository.UpVoteRepository;
import org.khatri.sto.qna.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Ankit Khatri
 */

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    @Autowired private QuestionRepository questionRepository;
    @Autowired private TagService tagService;
    @Autowired private UpVoteRepository upVoteRepository;
    @Autowired private MediaRepository mediaRepository;

    @Override
    @Transactional
    public QuestionDto createNewQuestion(final Long userId, final QuestionDto questionDto) {
        log.info("Request received for creating new question from userId:{}, data:{}", userId, questionDto);
        Question question = QuestionConverter.convertDtoToEntity(userId, questionDto);
        Question savedQuestion = this.questionRepository.saveAndFlush(question);
        log.info("Successfully created new question of userId:{}, quesId:{}", userId, savedQuestion.getId());
        if (CollectionUtils.isNotEmpty(questionDto.getMedias())) {
            List<Media> mediaList = questionDto.getMedias().stream().map(mediaDto ->
                Media.builder().userId(userId).mediaType(mediaDto.getMediaType()).url(mediaDto.getUrl()).question(savedQuestion).build()).collect(Collectors.toList());
            this.mediaRepository.saveAllAndFlush(mediaList);
        }
        if(CollectionUtils.isNotEmpty(questionDto.getTags())){
            CompletableFuture.runAsync(() -> this.tagService.mapTagWithReference(savedQuestion.getId(), RefType.QUESTION, questionDto.getTags()));
        }
        return QuestionConverter.convertEntityToDto(userId, savedQuestion);
    }

    @Override
    public List<QuestionDto> findAllQuestions(final Long userId) {
        return this.questionRepository.findAll().stream().map(q -> QuestionConverter.convertEntityToDto(q.getUserId(), q)).collect(Collectors.toList());
    }

    @Override
    public QuestionDto findQuestionById(final Long userId, final Long quesId) {
        return this.questionRepository.findById(quesId).map(q -> QuestionConverter.convertEntityToDto(q.getUserId(), q)).orElse(null);
    }

    @Override
    public GenericPaginatedResponse<QuestionDto> getAllUserQuestions(final int page, final int size, final String sort, final String direction, final Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<Question> questions = this.questionRepository.findByUserId(userId, pageable);
        GenericPaginatedResponse<QuestionDto> response = new GenericPaginatedResponse();
        if(questions != null && CollectionUtils.isNotEmpty(questions.getContent())){
            response.setTotalCount(questions.getTotalElements());
            response.setTotalPages(questions.getTotalPages());
            List<QuestionDto> questionDtoList = questions.getContent().stream().map(q -> QuestionConverter.convertEntityToDto(userId, q)).collect(Collectors.toList());
            response.setData(questionDtoList);
        }
        return response;
    }

    @Override
    @Transactional
    public QuestionDto modifyExistingQuestion(final Long userId, final Long quesId, final QuestionDto questionDto){
        log.info("Request received to modify existing ques:{}, userId:{}, data:{}", quesId, userId, questionDto);
        Question question = this.questionRepository.findById(quesId).orElseThrow(() -> new InputInvalidException(ExceptionCode.INVALID_REQUEST, "Question not found."));
        if(!question.getUserId().equals(userId)){
            throw new InputInvalidException(ExceptionCode.NOT_AUTHORIZED, "You're not authorized to modify this question.");
        }
        question.setTitle(questionDto.getTitle());
        question.setContent(questionDto.getContent());
        Question savedQuestion = this.questionRepository.saveAndFlush(question);
        if(questionDto.getMedias() != null){
            List<Media> mediaList = questionDto.getMedias().stream().map(mediaDto ->
                    Media.builder().userId(userId).mediaType(mediaDto.getMediaType()).url(mediaDto.getUrl()).question(savedQuestion).build()).collect(Collectors.toList());
            this.mediaRepository.saveAllAndFlush(mediaList);
        }
        if(questionDto.getTags() != null){
            CompletableFuture.runAsync(() -> this.tagService.mapTagWithReference(quesId, RefType.QUESTION, questionDto.getTags()));
        }
        log.info("Successfully modified the ques:{}", quesId);
        return QuestionConverter.convertEntityToDto(userId, savedQuestion);
    }

    @Override
    public void deleteQuestion(final Long userId, final Long quesId){
        log.info("Request received to delete questionId:{}, userId:{}", quesId, userId);
        Question question = this.questionRepository.findById(quesId).orElseThrow(() -> new InputInvalidException(ExceptionCode.INVALID_REQUEST, "Question not found."));
        if(!question.getUserId().equals(userId)){
            throw new InputInvalidException(ExceptionCode.NOT_AUTHORIZED, "You're not authorized to delete this question.");
        }
        if(CollectionUtils.isNotEmpty(question.getAnswers())){
            throw new InputInvalidException(ExceptionCode.NOT_APPLICABLE, "This question can't be deleted since it has attached answers.");
        }
        this.questionRepository.deleteById(quesId);
        log.info("Successfully deleted the question:{}", quesId);
    }

    @Override
    public void suggestVoteOnQuestion(final Long userId, final Long quesId, final UpVote.UpVoteType voteType) {
        log.info("Request received for vote for the quesId:{} by userId:{}, voteType:{}", quesId,userId, voteType);
        Question question = this.questionRepository.findById(quesId).orElseThrow(() -> new InputInvalidException(ExceptionCode.INVALID_REQUEST, "Question not found."));
        Optional<UpVote> upVoteOptional = this.upVoteRepository.findByUserIdAndQuestion(userId, question);
        if (upVoteOptional.isPresent() ) {
            UpVote existingVote = upVoteOptional.get();
            existingVote.setUpVoteType(voteType);
            this.upVoteRepository.save(existingVote);
        } else {
            UpVote newVote = UpVote.builder().userId(userId).question(question).upVoteType(voteType).build();
            this.upVoteRepository.save(newVote);
        }
    }

    @Override
    public GenericPaginatedResponse<QuestionDto> searchQuestion(final int page, final int size, final String sort, final String direction, final String query){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<Question> questions = this.questionRepository.searchByTitleOrContent(query, pageable);
        GenericPaginatedResponse<QuestionDto> response = new GenericPaginatedResponse();
        if(questions != null && CollectionUtils.isNotEmpty(questions.getContent())){
            response.setTotalCount(questions.getTotalElements());
            response.setTotalPages(questions.getTotalPages());
            List<QuestionDto> questionDtoList = questions.getContent().stream().map(q -> QuestionConverter.convertEntityToDto(q.getUserId(), q)).collect(Collectors.toList());
            response.setData(questionDtoList);
        }
        return response;
    }

    @Override
    public List<QuestionDto> searchQuestionByTags(final Object request){
        List<TagMappingDto> list = this.tagService.findRefIdByTags(request, RefType.QUESTION);
        if(CollectionUtils.isNotEmpty(list)){
            List<Long> queIds = list.stream().map(TagMappingDto::getRefId).toList();
            List<Question> questions = this.questionRepository.findAllById(queIds);
            if(CollectionUtils.isNotEmpty(questions)){
                return questions.stream().map(q -> QuestionConverter.convertEntityToDto(q.getUserId(), q)).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}
