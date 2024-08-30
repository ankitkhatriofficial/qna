package org.khatri.sto.qna.repository;

import org.khatri.sto.qna.entity.Answer;
import org.khatri.sto.qna.entity.Question;
import org.khatri.sto.qna.entity.UpVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Ankit Khatri
 */
public interface UpVoteRepository extends JpaRepository<UpVote, Long> {

    Optional<UpVote> findByUserIdAndQuestion(Long userId, Question question);

    Optional<UpVote> findByUserIdAndAnswer(Long userId, Answer answer);
}
