package org.khatri.sto.qna.exceptions;

import lombok.Data;
import org.khatri.sto.qna.constant.ExceptionCode;
import org.khatri.sto.qna.dto.exception.BaseExceptionDto;
import org.springframework.http.HttpStatus;

/**
 * @author Ankit Khatri
 */
@Data
public class InputInvalidException extends RuntimeException{

    private BaseExceptionDto exception;
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public InputInvalidException(final ExceptionCode exceptionCode, final String details){
        this.exception = new BaseExceptionDto(exceptionCode.getCode(), exceptionCode.getMessage(), details);
    }

}
