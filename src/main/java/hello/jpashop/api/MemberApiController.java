package hello.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hello.jpashop.api.MemberApiController.MemberDto;
import hello.jpashop.domain.Address;
import hello.jpashop.domain.Member;
import hello.jpashop.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
	
	private final MemberService memberService;
	
	@GetMapping("/api/v1/members")
	public List<Member> membersV1() {
		return memberService.findMembers();
	}

	@GetMapping("/api/v2/members")
	public Result membersV2() {
		List<Member> findMembers = memberService.findMembers();
		// 엔티티 -> DTO 변환
		List<MemberDto> collect = findMembers.stream()
				.map(m -> new MemberDto(m.getName(), m.getAddress()))
				.collect(Collectors.toList());
		return new Result(collect.size(), collect);
	}
	
	@Data
	@AllArgsConstructor
	static class Result<T> {
		private int count;
		private T data;
	}

	@Data
	@AllArgsConstructor
	static class MemberDto {
		private String name;
		private Address address;
	}	
	
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	/*
	 * API 정석은 DTO를 만들어야 한다
	 */
	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		Member member = new Member();
		member.setName(request.getName());

		Long id = memberService.join(member);

		return new CreateMemberResponse(id);
	}
	
	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
			@RequestBody @Valid UpdateMemberRequest request) {

		Member member = new Member();
		member.setName(request.getName());

		memberService.update(id, request.getName());
		
		Member findMember = memberService.findOne(id);

		return new UpdateMemberResponse(findMember.getId(), findMember.getName());
	}
	
	@Data
	static class CreateMemberRequest{
		@NotEmpty
		private String name;
	}
	
	@Data
	@AllArgsConstructor
	static class CreateMemberResponse{
		private Long id;
	}

	@Data
	static class UpdateMemberRequest{
		@NotEmpty
		private String name;
	}
	
	@Data
	@AllArgsConstructor
	static class UpdateMemberResponse{
		private Long id;
		private String name;
	}
	
}
