package dev.jmagni.repository.member;

import dev.jmagni.model.condition.MemberSearchCondition;
import dev.jmagni.model.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition memberSearchCondition);

    Page<MemberTeamDto> searchPaging(MemberSearchCondition memberSearchCondition, Pageable pageable);

}
