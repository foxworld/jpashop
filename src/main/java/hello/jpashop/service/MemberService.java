package hello.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.jpashop.domain.Member;
import hello.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final MemberRepository memberRepository;
	
	//회원가입
	@Transactional(readOnly = false)
	public Long join(Member member) {
		validateDuplicateMember(member);
		memberRepository.save(member);
		return member.getId();
	}
	
	private void validateDuplicateMember(Member member) {
		List<Member> findMembers = memberRepository.findByName(member.getName());
		if(!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
	}
	
	//회원전체조회
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}
	
	public Member findOne(Long memberId) {
		return memberRepository.findOne(memberId);
	}
	
	@Transactional
	public void update(Long id, String name) {
		/*
		 * 조회시 DB와 연속성이 존재하므로 set에서 값만 변경하면 끝날때 Update 한다
		 */
		Member findMember = memberRepository.findOne(id);
		findMember.setName(name);
	}
	
}
