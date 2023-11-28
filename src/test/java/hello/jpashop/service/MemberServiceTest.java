package hello.jpashop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hello.jpashop.domain.Member;
import hello.jpashop.repository.MemberRepository;
import hello.jpashop.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MemberServiceTest {
	
	@Autowired MemberService memberService;
	@Autowired MemberRepository memberRepository;
	@Autowired EntityManager em;

	@Test
	public void 회원가입() throws Exception {
		// given
		Member member = new Member();
		member.setName("peter");
		
		// when
		Long savedId = memberService.join(member);
		
		// then
		em.flush();
		assertEquals(member, memberRepository.findOne(savedId));
	}
	
	@Test
	public void 중복_회원_예외() throws Exception {
		// given
		Member member1 = new Member();
		member1.setName("peter");
		Member member2 = new Member();
		member2.setName("peter");
		
		// when
		memberService.join(member1);

		// then
		// 중복오류가 발생하면 정상임
		assertThrows(IllegalStateException.class, () -> {
			memberService.join(member2);
        });
	}
}
