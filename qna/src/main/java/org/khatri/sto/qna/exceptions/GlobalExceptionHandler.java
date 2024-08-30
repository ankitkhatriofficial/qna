package org.khatri.sto.qna.exceptions;

import org.khatri.sto.qna.dto.exception.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author Ankit Khatri
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InputInvalidException.class)
    public ResponseEntity<?> handleInputValidationException(InputInvalidException ex){
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getException());
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseExceptionDto(400, "Duplicate entry found", ex.getLocalizedMessage()));
    }
}
