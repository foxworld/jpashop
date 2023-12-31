package hello.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hello.jpashop.domain.item.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자를 protected 로 선언
public class OrderItem {
	
	@Id @GeneratedValue
	@Column(name="order_item_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="item_id")
	private Item item;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
	private int orderPrice;
	private int count;
	
	//==생성 매서드
	public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
		OrderItem orderItem = new OrderItem();
		orderItem.setItem(item);
		orderItem.setOrderPrice(orderPrice);
		orderItem.setCount(count);
		
		item.removeStock(count);
		
		return orderItem;
	}
	
	//==비지니스 로직
	public void cancel() {
		getItem().addStock(count);
	}
	
	//=조회 로직
	public int getTotalPrice() {
		return getOrderPrice() * getCount();
	}

}
