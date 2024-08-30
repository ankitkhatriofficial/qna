package org.khatri.sto.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PostAnswerResponse implements Serializable {

    private static final long serialVersionUID = 782374794343L;

    private Long ansId;
    private Long quesId;
    private Long userId;
    private String answer;
    private Long parentAnswerId;
    private Boolean hasReplies;
}
