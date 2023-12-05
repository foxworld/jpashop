package hello.jpashop.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
	private final EntityManager em;
	/*
	 * 1대다(OneToManey) 인경우는 분리해서 조회해서 만든다 
	 * 왜냐면 오브젝트형태의 구성을 만들기 위해
	 */
	public List<OrderQueryDto> findOrderQueryDtos() {
		List<OrderQueryDto> result = findOrders();
		result.forEach(o -> {
			List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
			o.setOrderItems(orderItems);
		});
		return result;
	}
	
	private List<OrderItemQueryDto> findOrderItems(Long orderId) {
		return em.createQuery(
				"select new hello.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"+ 
				" from OrderItem oi" +
				" join oi.item i" +
				" where oi.order.id = :orderId", OrderItemQueryDto.class)
				.setParameter("orderId", orderId)
				.getResultList();
	}
	
	public List<OrderQueryDto> findOrders() {
		return em.createQuery(
				"select new hello.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
				" from Order o" +
				" join o.member m" +
				" join o.delivery d", OrderQueryDto.class)
				.getResultList();
		
	}

	public List<OrderQueryDto> findAllByDto_optimization() {
		List<OrderQueryDto> result = findOrders();
		
		// 읽어온 Order 키값을 List 형태로 만들어 OrderItem 읽을때 키 값으로 활용한다
		List<Long> orderIds = toOrderIds(result);
	
		// List<OrderItemQueryDto> 를 Map 형태로 구성하여 데이터 가져로기 쉽게 바꾼다
		Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);
	
		// result 건건히 Orderitems를 넣어준다
		result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
		//log.info("orderItemMap: {}", result);
		
		return result;
	}
	
	private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
		List<OrderItemQueryDto> orderItems = em.createQuery(
				"select new hello.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"+ 
				" from OrderItem oi" +
				" join oi.item i" +
				" where oi.order.id in :orderIds", OrderItemQueryDto.class)
		.setParameter("orderIds", orderIds)
		.getResultList();
		
		// List<OrderItemQueryDto> 를 Map 형태로 구성하여 데이터 가져로기 쉽게 바꾼다
		Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
				.collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
		
		return orderItemMap;
		
	}
	
	private List<Long> toOrderIds(List<OrderQueryDto> result) {
		return  result.stream()
				.map(o -> o.getOrderId())
				.collect(Collectors.toList());
	}

	public List<OrderFlatDto> findAllByDto_flat() {
		return em.createQuery(
				"select new " + 
				" hello.jpashop.repository.order.query.OrderFlatDto" + 
				" (o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
				" from Order o" +
				" join o.member m" +
				" join o.delivery d" +
				" join o.orderItems oi" +
				" join oi.item i", OrderFlatDto.class)
				.getResultList();
	}

}
