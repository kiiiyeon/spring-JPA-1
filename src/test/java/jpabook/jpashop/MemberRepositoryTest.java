package jpabook.jpashop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional //Test에 있는 Transactional은 테스트 끝나고 바로 roll back을 해서 DB에 정보가 보이지 않음
    @Rollback(false) //rollback 안함
    void testMember() {
        //given
        Member member = new Member();
        member.setUsername("memberA");


        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);


        //then
        org.assertj.core.api.Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        org.assertj.core.api.Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        org.assertj.core.api.Assertions.assertThat(findMember).isEqualTo(member);
    }
}