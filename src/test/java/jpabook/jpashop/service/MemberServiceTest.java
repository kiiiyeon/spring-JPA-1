package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional //DB에 커밋안함(insert X)
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kiyeon");

        //when
        Long saveId = memberService.join(member);


        //then
        assertEquals(member, memberRepository.findOne(saveId)); //같은 Transactional 안에서 PK가 같으면  같은 영속성 컨텍스트에서 관리됨

    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kiyeon");
        Member member2 = new Member();
        member2.setName("kiyeon");

        //when
        memberService.join(member1);
        memberService.join(member2); //예외 발생

        //then
        fail("예외가 발생해야 한다"); //테스트 실패했을 경우

    }

}