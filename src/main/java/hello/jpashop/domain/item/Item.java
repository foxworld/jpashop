package hello.jpashop.domain.item;

import java.util.ArrayList;
import java.util.List;

import hello.jpashop.domain.Category;
import hello.jpashop.exception.NotEnoughtStockException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="dtype")
@Getter @Setter
public abstract class Item {
	@Id @GeneratedValue
	@Column(name="item_id")
	private Long id;

	private String name;
	private int price;
	private int stockQuantity;
	
	@ManyToMany(mappedBy = "items")
	private List<Category> categories = new ArrayList<>();
	
	//==비지니스로직(재고)
	/* 
	 * 재고수량을 증가 
	*/
	public void addStock(int quantity) {
		this.stockQuantity += quantity;
	}

	/* 
	 * 재고수량을 삭제  
	*/
	public void removeStock(int quantity) {
		int restStock = this.stockQuantity - quantity;
		if(restStock < 0) {
			throw new NotEnoughtStockException("need more stock");
		}
		this.stockQuantity = restStock;
		
	}
	
}
