package de.wwu.pi.wh.entity;

public class Stock {

	protected Item item;
	protected long quantity;
	
	public Stock() {
	}

	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
}
