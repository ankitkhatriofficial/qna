package org.khatri.sto.qna.service;

import org.khatri.sto.qna.dto.request.PostAnswerRequest;
import org.khatri.sto.qna.dto.response.GenericPaginatedResponse;
import org.khatri.sto.qna.dto.response.PostAnswerResponse;
import org.khatri.sto.qna.entity.UpVote;

/**
 * @author Ankit Khatri
 */
public interface AnswerService {

    PostAnswerResponse writeAnswerToQuestion(final Long userId, final Long quesId, final PostAnswerRequest postAnswerRequest);

    GenericPaginatedResponse<PostAnswerResponse> getAnswerOfQuestion(int page, int size, String sort, String direction, Long quesId);

    PostAnswerResponse modifyAnswer(final Long userId, final Long answerId, final PostAnswerRequest request);

    PostAnswerResponse writeRepliesToAnswer(final Long userId, final Long answerId, final PostAnswerRequest request);

    GenericPaginatedResponse<PostAnswerResponse> getRepliesOfAnswer(final int page, final int size, final String sort, final String direction, final Long userId,final  Long answerId);

    void suggestVoteOnAnswer(final Long userId, final Long answerId, final UpVote.UpVoteType voteType);
}