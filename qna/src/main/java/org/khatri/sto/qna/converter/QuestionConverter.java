package org.khatri.sto.qna.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.khatri.sto.qna.dto.MediaDto;
import org.khatri.sto.qna.dto.QuestionDto;
import org.khatri.sto.qna.entity.Question;
import org.khatri.sto.qna.entity.UpVote;

import java.util.List;

/**
 * @author Ankit Khatri
 */
public class QuestionConverter {

    public static Question convertDtoToEntity(final Long userId, final QuestionDto questionDto){
        return Question.builder()
                .title(questionDto.getTitle()).content(questionDto.getContent())
                .userId(userId).build();
    }

    public static QuestionDto convertEntityToDto(final Long currentUserId, final Question question){
        List<UpVote> upVotes = question.getUpVotes();
        Long upVotesCount = CollectionUtils.isNotEmpty(upVotes) ? upVotes.parallelStream().filter(q -> UpVote.UpVoteType.USEFUL.equals(q.getUpVoteType())).count() : 0;
        Long downVotes = CollectionUtils.isEmpty(upVotes) ? 0 : Math.abs(upVotes.size() - upVotesCount);
        return QuestionDto.builder().questionId(question.getId())
                .author(currentUserId.equals(question.getUserId()))
                .title(question.getTitle()).content(question.getContent())
                .medias(CollectionUtils.isNotEmpty(question.getMedias()) ? question.getMedias().stream().map(media -> MediaDto.builder().mediaType(media.getMediaType()).url(media.getUrl()).build()).toList() : null)
                .totalUpvotes(upVotesCount).totalDownvotes(downVotes)
                .userId(question.getUserId()).build();
    }
}
