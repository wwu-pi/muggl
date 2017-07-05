package de.wwu.pi.wh.ejb;

import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import de.wwu.pi.wh.entity.Item;
import de.wwu.pi.wh.entity.OrderDetails;
import de.wwu.pi.wh.entity.Stock;
import de.wwu.pi.wh.ex.WarehouseException;

@Startup
@Singleton
public class StartupBean {

	@Inject
	protected WarehouseBean whBean;
	
	@PostConstruct
	public void init() {
		Item item = new Item();
		item.setItemId("g003");
		item.setName("startup-item");
		item.setWeightInGram(1000L);
		
		OrderDetails details = new OrderDetails();
		details.setItem(item);
		details.setQuantity(321L);
		details.setDate(Calendar.getInstance().getTime());
		
		try {
			this.whBean.removeFromStock("g003", 100);
		} catch (WarehouseException e) {
			e.printStackTrace();
		}
		
		
		Stock stock = this.whBean.getStock("g001");
		System.out.println("Stock of g001 -> quantity: " + stock.getQuantity());
		System.out.println("Stock of g001 -> item: " + stock.getItem());
		
		boolean b001 = this.whBean.goodsAvailable("g001", 3);
		System.out.println("Stock of 3 for good g001 available: " + b001);
		
		boolean b002 = this.whBean.goodsAvailable("g001", 3000);
		System.out.println("Stock of 3000 for good g001 available: " + b002);
		
		boolean b003 = this.whBean.goodsAvailable("g002", 10);
		System.out.println("Stock of 10 for good g002 available: " + b003);
	}
	
}
