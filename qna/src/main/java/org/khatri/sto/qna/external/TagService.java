package org.khatri.sto.qna.external;

import org.khatri.sto.qna.RefType;
import org.khatri.sto.qna.dto.external.TagMappingDto;

import java.util.List;

/**
 * @author Ankit Khatri
 */
public interface TagService {

    void mapTagWithReference(final Long refId, final RefType refType, List<String> tags);
    List<TagMappingDto> findRefIdByTags(final Object request, final RefType refType);
}
