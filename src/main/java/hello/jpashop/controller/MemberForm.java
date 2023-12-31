package hello.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {
	
	@NotNull
	@NotEmpty(message = "회원이름은 필수 입니다!")
	private String name;
	
	private String city;
	private String street;
	private String zipcode;

}
