package dev.jmagni.repository.member;


import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.jmagni.model.condition.MemberSearchCondition;
import dev.jmagni.model.dto.MemberGroupDto;
import dev.jmagni.model.dto.QMemberGroupDto;
import dev.jmagni.model.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static dev.jmagni.model.group.QGroup.group;
import static dev.jmagni.model.member.QMember.member;
import static org.springframework.util.StringUtils.hasText;

/**
 * MemberRepositoryImpl > JPARepository 를 상속받은 인터페이스 이름 : MemberRepository
 *
 * 클랠스 네이밍 규칙 : MemberRepository + Impl
 */
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    public MemberRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<MemberGroupDto> search(MemberSearchCondition memberSearchCondition) {
        return getMemberGroupDtos(memberSearchCondition);
    }

    private BooleanExpression isUsernameEqual(String username) {
        return hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression isGroupnameEqual(String groupName) {
        return hasText(groupName) ? group.name.eq(groupName) : null;
    }

    private BooleanExpression isAgeGoeExist(Integer agGoe) {
        return agGoe != null ? member.age.goe(agGoe) : null;
    }

    private BooleanExpression isAgeLoeExist(Integer agLoe) {
        return agLoe != null ? member.age.loe(agLoe) : null;
    }

    /**
     * 멤버 리스트와 전체 개수를 분리해서 조회
     */
    @Override
    public Page<MemberGroupDto> searchByPaging(MemberSearchCondition memberSearchCondition, Pageable pageable) {
        List<MemberGroupDto> memberGroupDtos = getMemberGroupDtosByPaging(memberSearchCondition, pageable)
                .fetch();

        //long total = getTotal(memberSearchCondition);

        /**
         * @ Count 쿼리 최적화
         *
         * Count 쿼리가 생략 가능한 경우 생략해서 처리 > QueryDSL 에서 함수로 제공함
         *  1. 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
         *  2. 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함)
         */
        return PageableExecutionUtils
                .getPage(
                        memberGroupDtos,
                        pageable,
                        () -> getMemberDtoQuery(memberSearchCondition).fetchCount()
                );
        //return new PageImpl<>(memberGroupDtos, pageable, total);
    }

    private List<MemberGroupDto> getMemberGroupDtos(MemberSearchCondition memberSearchCondition) {
        return getMemberDtoQuery(memberSearchCondition)
                .fetch();
    }

    private long getTotal(MemberSearchCondition memberSearchCondition) {
        return jpaQueryFactory
                .select(member)
                .from(member)
                .leftJoin(member.group, group)
                .where(
                        isUsernameEqual(memberSearchCondition.getUsername()),
                        isGroupnameEqual(memberSearchCondition.getGroupName()),
                        isAgeGoeExist(memberSearchCondition.getAgeGoe()),
                        isAgeLoeExist(memberSearchCondition.getAgeLoe())
                )
                .fetchCount();
    }

    private JPAQuery<MemberGroupDto> getMemberGroupDtosByPaging(MemberSearchCondition memberSearchCondition, Pageable pageable) {
        return getMemberDtoQuery(memberSearchCondition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
    }


    private JPAQuery<MemberGroupDto> getMemberDtoQuery(MemberSearchCondition memberSearchCondition) {
        return jpaQueryFactory
                .select(
                        new QMemberGroupDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                group.id.as("groupId"),
                                group.name.as("groupName")
                        )
                )
                .from(member)
                .leftJoin(member.group, group)
                .where(
                        isUsernameEqual(memberSearchCondition.getUsername()),
                        isGroupnameEqual(memberSearchCondition.getGroupName()),
                        isAgeGoeExist(memberSearchCondition.getAgeGoe()),
                        isAgeLoeExist(memberSearchCondition.getAgeLoe())
                );
    }

}
