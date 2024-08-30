package org.khatri.sto.qna.repository;

import org.khatri.sto.qna.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Ankit Khatri
 */
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT ans FROM Answer ans WHERE ans.question.id = :qId AND ans.parentAnswer.id is NULL")
    Page<Answer> findByQuestionId(@Param("qId") Long questionId, Pageable pageable);

    @Query("SELECT ans FROM Answer ans WHERE ans.parentAnswer.id = :ansId")
    Page<Answer> findByParentAnswerId(@Param("ansId") Long answerId, Pageable pageable);
}
