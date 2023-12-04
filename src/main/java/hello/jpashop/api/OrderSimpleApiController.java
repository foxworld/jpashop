package hello.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.jpashop.domain.Address;
import hello.jpashop.domain.Order;
import hello.jpashop.domain.OrderStatus;
import hello.jpashop.repository.OrderRepository;
import hello.jpashop.repository.OrderSearch;
import hello.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/*
 * xToOne(many to one, one to one)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
	private final OrderRepository orderRepository;
	
	@GetMapping("/api/v1/simple-orders")
	public  List<Order> orderV1() {
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		for(Order order : all) {
			order.getMember().getName(); //Lazy 강제 초기화
			order.getDelivery().getAddress(); // Lazy 강제 초기화
		}
		return all;
	}
	
	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDto> orderV2() {
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		return orders.stream()
			.map(o -> new SimpleOrderDto(o))
			.collect(Collectors.toList());
	}

	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDto> orderV3() {
		List<Order> orders = orderRepository.findAllWithMemberDelivery();
		return orders.stream()
				.map(o -> new SimpleOrderDto(o))
				.collect(Collectors.toList());
	}

	
	@GetMapping("/api/v4/simple-orders")
	public List<OrderSimpleQueryDto> orderV4() {
		return orderRepository.findOrderDtos();
	}
	
	@Data
	static class SimpleOrderDto {
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		
		public SimpleOrderDto(Order order) {
			orderId = order.getId();
			name = order.getMember().getName(); // LAZY 초기화
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress(); // LAZY 초기화
		}
		
	}

}
