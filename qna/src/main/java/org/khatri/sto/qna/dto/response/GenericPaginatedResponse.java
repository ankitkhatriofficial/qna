package org.khatri.sto.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class GenericPaginatedResponse <T> implements Serializable {

    private static final long serialVersionUID = 23794894032403223L;

    private long totalCount;
    private long totalPages;
    private List<T> data;
}
