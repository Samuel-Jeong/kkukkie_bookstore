package dev.kkukkie_bookstore.repository.member;

import dev.kkukkie_bookstore.model.member.condition.MemberSearchCondition;
import dev.kkukkie_bookstore.model.member.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition memberSearchCondition);

    Page<MemberTeamDto> searchPaging(MemberSearchCondition memberSearchCondition, Pageable pageable);

}
