package org.khatri.sto.qna.repository;

import org.khatri.sto.qna.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Ankit Khatri
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByUserId(final Long userId, final Pageable pageable);

    @Query("SELECT q FROM Question q LEFT JOIN q.upVotes uv " +
            "WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(q.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "GROUP BY q " +
            "ORDER BY COUNT(uv) DESC, q.createdAt DESC")
    Page<Question> searchByTitleOrContent(@Param("query") String query, Pageable pageable);

}
