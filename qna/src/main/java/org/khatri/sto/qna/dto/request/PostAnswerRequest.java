package org.khatri.sto.qna.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Ankit Khatri
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostAnswerRequest implements Serializable {

    private static final long serialVersionUID = 34743894393434L;

    @NotEmpty(message = "Oops! It looks like you haven't written anything. Please add your answer.")
    private String answer;
}
