package org.khatri.sto.qna.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Ankit Khatri
 */

@Data
@Entity
@Table(name = "answers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer implements Serializable {

    private static final long serialVersionUID = 38723792398324L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "answer")
    private List<Media> medias;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @ToString.Exclude
    private Question question;

    @OneToMany(mappedBy = "answer")
    private List<UpVote> upVotes;

    @ManyToOne
    @JoinColumn(name = "parent_answer_id")
    private Answer parentAnswer;

    @OneToMany(mappedBy = "parentAnswer")
    private List<Answer> childAnswers;

    private Date createdAt;
    private Date updatedAt;

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
