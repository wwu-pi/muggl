package de.wwu.pi.wh.entity;

public class Item {

	protected String itemId;
	protected String name;
	protected long weightInGram;
	protected long reorderQuantity;
	
	public Item() {
	}

	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getWeightInGram() {
		return weightInGram;
	}
	public void setWeightInGram(long weightInGram) {
		this.weightInGram = weightInGram;
	}
	public long getReorderQuantity() {
		return reorderQuantity;
	}
	public void setReorderQuantity(long reorderQuantity) {
		this.reorderQuantity = reorderQuantity;
	}

	@Override
	public String toString() {
		return "name="+name+", weightInGram="+weightInGram;
	}
}
