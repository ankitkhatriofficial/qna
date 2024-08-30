package org.khatri.sto.qna.dto.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Ankit Khatri
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaseExceptionDto implements Serializable {

    private static final long serialVersionUID = 26382382334l;

    private Integer code;
    private String message;
    private String details;
}
