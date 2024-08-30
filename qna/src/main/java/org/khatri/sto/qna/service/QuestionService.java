package org.khatri.sto.qna.service;

import org.khatri.sto.qna.dto.QuestionDto;
import org.khatri.sto.qna.dto.response.GenericPaginatedResponse;
import org.khatri.sto.qna.entity.UpVote;

import java.util.List;

/**
 * @author Ankit Khatri
 */
public interface QuestionService {

    QuestionDto createNewQuestion(final Long userId, final QuestionDto questionDto);

    List<QuestionDto> findAllQuestions(final Long userId);

    QuestionDto findQuestionById(final Long userId, final Long quesId);

    GenericPaginatedResponse<QuestionDto> getAllUserQuestions(final int page, final int size, final String sort, final String direction, final Long userId);

    QuestionDto modifyExistingQuestion(final Long userId, final Long quesId, final QuestionDto questionDto);

    void deleteQuestion(final Long userId, final Long quesId);

    void suggestVoteOnQuestion(final Long userId, final Long quesId, final UpVote.UpVoteType voteType);

    GenericPaginatedResponse<QuestionDto> searchQuestion(final int page, final int size, final String sort, final String direction, final String query);

    List<QuestionDto> searchQuestionByTags(final Object request);
}
