package de.wwu.pi.wh.res;

import java.util.ArrayList;
import java.util.Date;

public class OrderDetails {

	protected Date date;
	protected Item item;
	protected long quantity;
	protected ArrayList<Stock> stocks;
	
	public OrderDetails() {
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
}
