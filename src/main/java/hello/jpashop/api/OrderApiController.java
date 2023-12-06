package hello.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hello.jpashop.domain.Address;
import hello.jpashop.domain.Order;
import hello.jpashop.domain.OrderItem;
import hello.jpashop.domain.OrderStatus;
import hello.jpashop.repository.OrderRepository;
import hello.jpashop.repository.OrderSearch;
import hello.jpashop.repository.order.query.OrderFlatDto;
import hello.jpashop.repository.order.query.OrderQueryDto;
import hello.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
	private final OrderRepository orderRepository;
	private final OrderQueryRepository orderQueryRepository;
	
	@GetMapping("/api/v1/orders")
	public List<Order> ordersV1() {
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		for(Order order : all) {
			order.getMember().getName();
			order.getDelivery().getAddress();
			List<OrderItem> orderItems = order.getOrderItems();
//			for(OrderItem orderItem : orderItems) {
//				orderItem.getItem().getName();
//			}
			orderItems.stream().forEach(o -> o.getItem().getName());
		}
		return all;
	}
	
	@GetMapping("/api/v2/orders")
	public List<OrderDto> ordersV2() {
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		List<OrderDto> collect = orders.stream()
			.map(OrderDto::new).collect(toList());
			//.map(o -> new OrderDto(o)).collect(Collectors.toList());			

		
		return collect;
	}
	
	@GetMapping("/api/v3/orders")
	public List<OrderDto> ordersV3() {
		List<Order> orders = orderRepository.findAllWithItem();
		
		List<OrderDto> collect = orders.stream()
			//.map(OrderDto::new).collect(toList());
			.map(o -> new OrderDto(o)).collect(Collectors.toList());			

		
		return collect;
	}
		
	@GetMapping("/api/v3.1/orders")
	public List<OrderDto> ordersV3_page(
			@RequestParam(value="offset", defaultValue="0") int offset, 
			@RequestParam(value="limit", defaultValue="100") int limit) {
		List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
		
		List<OrderDto> result = orders.stream()
			.map(o -> new OrderDto(o)).collect(Collectors.toList());			

		return result;
	}
	
	@GetMapping("/api/v4/orders")
	public List<OrderQueryDto> orderV4() {
		return orderQueryRepository.findOrderQueryDtos();		
	}

	@GetMapping("/api/v5/orders")
	public List<OrderQueryDto> orderV5() {
		return orderQueryRepository.findAllByDto_optimization();		
	}

	@GetMapping("/api/v6/orders")
	public List<OrderQueryDto> orderV6() {
		List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
		
		return null;
//		return flats.stream()
//                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
//                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
//                )).entrySet().stream()
//                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
//                .collect(toList());
	}


	@Getter
	static class OrderDto {
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		private List<OrderItemDto> orderItems;
		
		public OrderDto(Order order) {
			orderId = order.getId();
			name = order.getMember().getName();
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress();
			//order.getOrderItems().stream().forEach(o -> o.getItem().getName());
			orderItems = order.getOrderItems().stream()
					.map(orderItem -> new OrderItemDto(orderItem))
					.collect(Collectors.toList());
		}
	}
	
	@Getter
	static class OrderItemDto {
		private String itemName;
		private int OrderPrice;
		private int count;
		public OrderItemDto(OrderItem orderItem) {
			this.itemName = orderItem.getItem().getName();
			this.OrderPrice = orderItem.getItem().getPrice();
			this.count = orderItem.getCount();
		}
		
	}
}
