package dev.jmagni.repository.member;

import dev.jmagni.model.condition.MemberSearchCondition;
import dev.jmagni.model.dto.MemberGroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberGroupDto> search(MemberSearchCondition memberSearchCondition);

    Page<MemberGroupDto> searchByPaging(MemberSearchCondition memberSearchCondition, Pageable pageable);

}
