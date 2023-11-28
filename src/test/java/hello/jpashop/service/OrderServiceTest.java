package hello.jpashop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import hello.jpashop.domain.Address;
import hello.jpashop.domain.Member;
import hello.jpashop.domain.Order;
import hello.jpashop.domain.OrderStatus;
import hello.jpashop.domain.item.Book;
import hello.jpashop.domain.item.Item;
import hello.jpashop.exception.NotEnoughtStockException;
import hello.jpashop.repository.OrderRepository;
import hello.jpashop.service.OrderService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
public class OrderServiceTest {
	@Autowired EntityManager em;
	@Autowired OrderService orderService;
	@Autowired OrderRepository orderRepository;
	
	private Member createMember() {
		Member member = new Member();
		member.setName("회원1");
		member.setAddress(new Address("서울", "강가", "12313"));
		em.persist(member);
		return member;
	}
	
	private Book createBook(String name, int price, int stockQuantity) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);
		return book;
	}
	
	@Test
	public void 상품주문() throws Exception {
		//given
		Member member = createMember();
		Book book = createBook("시골 JPA", 10000, 10);
		int orderCount=2;
		
		//when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		Order getOrder = orderRepository.findOne(orderId);
		
		//then
		assertEquals(OrderStatus.ORDER, getOrder.getStatus()); // 상품주문시 상태는 order
		assertEquals(1, getOrder.getOrderItems().size());      // 주문한 상품 종류 수가 정학해야 한다
		assertEquals(10000 * orderCount, getOrder.getTotalPrice()); // 주문 가격은 가격 * 수량이다
		assertEquals(8,  book.getStockQuantity()); // 주문 수량만큼 재고가 줄어야 한다

	}
	
	@Test
	public void 상품주문_재고수량초과() throws Exception {
		//given
		Member member = createMember();
		Item item = createBook("시골 JPA", 10000, 10); // 상품수량 10개
		
		//when
		int orderCount=11;
		
		//then
		assertThrows(NotEnoughtStockException.class, () -> {
			orderService.order(member.getId(), item.getId(), orderCount);
        });
	}

	@Test
	public void 주문취소() throws Exception {
		//given
		Member member = createMember();
		Item item = createBook("시골 JPA", 10000, 10); // 상품수량 10개
		int orderCount=2;
		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
		
		//when
		orderService.cancelOrder(orderId);
		Order getOrder = orderRepository.findOne(orderId);
		
		//then
		assertEquals(OrderStatus.CANCEL, getOrder.getStatus()); // 주문 취소시 상태가 cancel 이다
		assertEquals(10, item.getStockQuantity()); // 주문이 취소된 상품은 재고가 증가해야 한다
	}
	
}
