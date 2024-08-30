package org.khatri.sto.qna.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.khatri.sto.qna.constant.ExceptionCode;
import org.khatri.sto.qna.converter.AnswerConverter;
import org.khatri.sto.qna.dto.request.PostAnswerRequest;
import org.khatri.sto.qna.dto.response.GenericPaginatedResponse;
import org.khatri.sto.qna.dto.response.PostAnswerResponse;
import org.khatri.sto.qna.entity.Answer;
import org.khatri.sto.qna.entity.Question;
import org.khatri.sto.qna.entity.UpVote;
import org.khatri.sto.qna.exceptions.InputInvalidException;
import org.khatri.sto.qna.repository.AnswerRepository;
import org.khatri.sto.qna.repository.QuestionRepository;
import org.khatri.sto.qna.repository.UpVoteRepository;
import org.khatri.sto.qna.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Ankit Khatri
 */

@Service
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private UpVoteRepository upVoteRepository;

    @Override
    public PostAnswerResponse writeAnswerToQuestion(final Long userId, final Long quesId, final PostAnswerRequest postAnswerRequest){
        log.info("Request received for writing answer to userId:{}, quesId:{}, data:{}", userId, quesId, postAnswerRequest);
        Question question = this.questionRepository.findById(quesId).orElseThrow(() -> new InputInvalidException(ExceptionCode.INVALID_REQUEST, "Question not found."));
        Answer answer = AnswerConverter.convertDtoToEntity(userId, postAnswerRequest, question);
        answer = this.answerRepository.saveAndFlush(answer);
        return AnswerConverter.preparePostAnswerResponse(quesId, answer);
    }

    @Override
    public GenericPaginatedResponse<PostAnswerResponse> getAnswerOfQuestion(final int page, final int size, final String sort, final String direction, final Long quesId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<Answer> answers = this.answerRepository.findByQuestionId(quesId, pageable);
        GenericPaginatedResponse<PostAnswerResponse> response = new GenericPaginatedResponse();
        if(answers != null && CollectionUtils.isNotEmpty(answers.getContent())){
            response.setTotalCount(answers.getTotalElements());
            response.setTotalPages(answers.getTotalPages());
            response.setData(answers.getContent().stream().map(ans -> AnswerConverter.preparePostAnswerResponse(quesId, ans)).collect(Collectors.toList()));
        }
        return response;
    }

    @Override
    public PostAnswerResponse modifyAnswer(final Long userId, final Long answerId, final PostAnswerRequest request){
        log.info("Request received for modifying answer by userId:{}, answerId:{}, data:{}", userId, answerId, request);
        Answer answer = this.answerRepository.findById(answerId).orElseThrow(() -> new InputInvalidException(ExceptionCode.INVALID_REQUEST, "Answer not found"));
        if(!answer.getUserId().equals(userId)){
            throw new InputInvalidException(ExceptionCode.NOT_AUTHORIZED, "You're not authorized to modify this answer.");
        }
        answer.setContent(request.getAnswer());
        answer = this.answerRepository.saveAndFlush(answer);
        log.info("Successfully modified the answer:{} by userId:{}", answerId, userId);
        return AnswerConverter.preparePostAnswerResponse(userId, answer);
    }

    @Override
    public PostAnswerResponse writeRepliesToAnswer(final Long userId, final Long answerId, final PostAnswerRequest request){
        log.info("Request received for writing replies for answerId:{}, userId:{}, data:{}", answerId, userId, request);
        Answer answer = this.answerRepository.findById(answerId).orElseThrow(() -> new InputInvalidException(ExceptionCode.INVALID_REQUEST, "Answer not found"));
        Answer answerReply = AnswerConverter.convertDtoToEntity(userId, request, answer.getQuestion());
        answerReply.setParentAnswer(answer);
        answerReply = this.answerRepository.saveAndFlush(answerReply);
        log.info("Successfully replied to answer for answerId:{}", answerId);
        return AnswerConverter.preparePostAnswerResponse(answer.getQuestion().getId(), answerReply);
    }

    @Override
    public GenericPaginatedResponse<PostAnswerResponse> getRepliesOfAnswer(final int page, final int size, final String sort, final String direction, final Long userId,final  Long answerId){
        Answer answer = this.answerRepository.findById(answerId).orElseThrow(() -> new InputInvalidException(ExceptionCode.INVALID_REQUEST, "Answer not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<Answer> replies = this.answerRepository.findByParentAnswerId(answerId, pageable);
        GenericPaginatedResponse<PostAnswerResponse> response = new GenericPaginatedResponse();
        if(replies != null && CollectionUtils.isNotEmpty(replies.getContent())){
            final Long quesId = answer.getQuestion().getId();
            response.setTotalPages(replies.getTotalPages());
            response.setTotalCount(replies.getTotalElements());
            response.setData(replies.getContent().stream().map(ans -> AnswerConverter.preparePostAnswerResponse(quesId, ans)).collect(Collectors.toList()));
        }
        return response;
    }

    @Override
    public void suggestVoteOnAnswer(final Long userId, final Long answerId, final UpVote.UpVoteType voteType){
        log.info("Request received for vote for the answerId:{} by userId:{}, voteType:{}", answerId,userId, voteType);
        Answer answer = this.answerRepository.findById(answerId).orElseThrow(() -> new InputInvalidException(ExceptionCode.INVALID_REQUEST, "Answer not found."));
        Optional<UpVote> upVoteOptional = this.upVoteRepository.findByUserIdAndAnswer(userId, answer);
        if (upVoteOptional.isPresent()) {
            UpVote existingVote = upVoteOptional.get();
            existingVote.setUpVoteType(voteType);
            this.upVoteRepository.save(existingVote);
        } else {
            UpVote newVote = UpVote.builder().userId(userId).answer(answer).upVoteType(voteType).build();
            this.upVoteRepository.save(newVote);
        }
    }

}
