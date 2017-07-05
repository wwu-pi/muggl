package de.wwu.pi.wh.ejb;

import java.util.Calendar;

import javax.ejb.Stateless;

import de.wwu.pi.wh.entity.Item;
import de.wwu.pi.wh.entity.OrderDetails;

/**
 * A simple bean that handles order tasks.
 * 
 * @author Andreas Fuchs
 *
 */
@Stateless
public class OrderBean {

	public void orderGoods(String gid, int amount) {
		// call another system to order goods (for demo, not needed)
	}

	public OrderDetails orderItems(String itemId, long quantity) {
		Item item = new Item();
		item.setItemId(itemId);
		item.setName("order-details-item");
		item.setWeightInGram(1000L);
		
		OrderDetails details = new OrderDetails();
		details.setItem(item);
		details.setQuantity(quantity);
		details.setDate(Calendar.getInstance().getTime());
		
		return details;
	}
}
