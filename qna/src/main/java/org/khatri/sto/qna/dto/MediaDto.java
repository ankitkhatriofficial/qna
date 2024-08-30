package org.khatri.sto.qna.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.khatri.sto.qna.entity.Media;

import java.io.Serializable;

/**
 * @author Ankit Khatri
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MediaDto implements Serializable {

    private static final long serialVersionUID = 7988943894398l;

    private Media.MediaType mediaType;
    private String url;

}
