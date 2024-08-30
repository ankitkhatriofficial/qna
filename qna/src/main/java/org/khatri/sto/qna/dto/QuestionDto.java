package org.khatri.sto.qna.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ankit Khatri
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuestionDto implements Serializable {

    private static final long serialVersionUID = 238734322893332L;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long questionId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean author;
    
    @NotEmpty(message = "Question title can't be empty")
    private String title;
    @NotEmpty(message = "Question content can't be empty")
    private String content;

    private List<String> tags;

    private List<MediaDto> medias;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long totalUpvotes;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long totalDownvotes;
}
