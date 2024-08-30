package org.khatri.sto.qna.constant;

import lombok.Getter;

/**
 * @author Ankit Khatri
 */
@Getter
public enum ExceptionCode {

    INVALID_REQUEST(1001, "Invalid request"),
    NOT_AUTHORIZED(1002, "Not Authorized"),
    NOT_APPLICABLE(1003, "Not Applicable");

    private Integer code;
    private String message;

    ExceptionCode(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
