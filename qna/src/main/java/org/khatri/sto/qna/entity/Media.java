package org.khatri.sto.qna.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Ankit Khatri
 */

@Data
@Entity
@Table(name = "medias")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media implements Serializable {

    private static final long serialVersionUID = 83279734939233L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private MediaType mediaType;
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    private Date createdAt;
    private Date updatedAt;

    @Getter
    public enum MediaType {
        PHOTO, VIDEO
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
