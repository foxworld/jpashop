package hello.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.jpashop.domain.Delivery;
import hello.jpashop.domain.Member;
import hello.jpashop.domain.Order;
import hello.jpashop.domain.OrderItem;
import hello.jpashop.domain.item.Item;
import hello.jpashop.repository.ItemRepository;
import hello.jpashop.repository.MemberRepository;
import hello.jpashop.repository.OrderRepository;
import hello.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly=false)
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final ItemRepository itemRepository;
	
	/**
	 * 주문
	 */
	@Transactional
	public Long order(Long memberId, Long itemId, int count) {
		// 엔티티 조회
		Member member = memberRepository.findOne(memberId);
		Item item = itemRepository.findOne(itemId);
		
		//배송정보 생성
		Delivery delivery = new Delivery();
		delivery.setAddress(member.getAddress());
		
		//주문상품 생성
		OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
		
		//주문 생성
		Order order= Order.createOrder(member, delivery, orderItem);
		
		//주문 저장
		orderRepository.save(order);
		
		return order.getId();
		
	}
	
	@Transactional
	public void cancelOrder(Long orderId) {
		//주문 앤티티 조회
		Order order = orderRepository.findOne(orderId);
		
		//주문 취소
		order.cancel();
	}

	/**
	 * 검색
	 */
	public List<Order> findOrders(OrderSearch orderSearch) {
		return orderRepository.findAllByString(orderSearch);
	}

}
