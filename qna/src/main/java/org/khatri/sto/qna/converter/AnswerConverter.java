package org.khatri.sto.qna.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.khatri.sto.qna.dto.request.PostAnswerRequest;
import org.khatri.sto.qna.dto.response.PostAnswerResponse;
import org.khatri.sto.qna.entity.Answer;
import org.khatri.sto.qna.entity.Question;

/**
 * @author Ankit Khatri
 */
public class AnswerConverter {

    public static PostAnswerResponse preparePostAnswerResponse(final Long quesId, final Answer answer) {
        return PostAnswerResponse.builder().userId(answer.getUserId()).quesId(quesId).ansId(answer.getId()).answer(answer.getContent())
                .parentAnswerId(answer.getParentAnswer() != null ? answer.getParentAnswer().getId() : null)
                .hasReplies(CollectionUtils.isNotEmpty(answer.getChildAnswers())).build();
    }

    public static Answer convertDtoToEntity(final Long userId, final PostAnswerRequest request, final Question question) {
        return Answer.builder().userId(userId).content(request.getAnswer()).question(question).build();
    }
}
