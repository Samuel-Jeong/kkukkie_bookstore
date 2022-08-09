package dev.jmagni.controller.member;

import dev.jmagni.model.condition.MemberSearchCondition;
import dev.jmagni.model.dto.MemberGroupDto;
import dev.jmagni.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    /**
     * http://localhost:8080/v1/members?teamName=teamB&ageGoe=31&ageLoe=35&username=member31
     */
    @GetMapping("/v3/members")
    public Page<MemberGroupDto> searchMemberV3(MemberSearchCondition memberSearchCondition, Pageable pageable) {
        return memberRepository.searchByPaging(memberSearchCondition, pageable);
    }

}
