package hello.jpashop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import hello.jpashop.domain.item.Item;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
	private final EntityManager em;
	
	public void save(Item item) {
		if(item.getId() == null) {
			// insert 
			em.persist(item);
		} else {
			// update
			em.merge(item);
		}
	}
	
	public Item findOne(Long id) {
		return em.find(Item.class, id);
	}
	
	public List<Item> findAll() {
		return em.createQuery("select i from from Item i", Item.class).getResultList();
	}

}
