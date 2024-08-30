package org.khatri.sto.qna.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AnswerDto implements Serializable {

    private static final long serialVersionUID = 3274894890449032L;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long ansId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long quesId;

    private String answer;
}
