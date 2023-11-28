package hello.jpashop.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hello.jpashop.domain.item.Book;
import jakarta.persistence.EntityManager;

@SpringBootTest
public class ItemUpdateTest {
	
	@Autowired EntityManager em;
	
	@Test
	public void updateTest() {
		Book book=em.find(Book.class, 1L);
		
		//TX
		book.setName("abcde");
		
		//변경감지 == dirty checking
	}

}
