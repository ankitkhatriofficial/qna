package org.khatri.sto.qna.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Ankit Khatri
 */

@Data
@Entity
@Table(name = "upvote")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpVote implements Serializable {

    private static final long serialVersionUID = 78439748489393L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private UpVoteType upVoteType;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    private Date createdAt;
    private Date updatedAt;

    @Getter
    public enum UpVoteType{
        USEFUL, NOT_USEFUL
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
