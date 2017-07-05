package de.wwu.pi.wh.mock;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple mock for stock information.
 * 
 * @author Andreas Fuchs
 */
public class StockMock {

	/**
	 * A stock map for goods.
	 * key = goodId
	 * value = amount of goods
	 */
	protected Map<String, Integer> stockMap;
	
	/**
	 * Singleton instance.
	 */
	private static StockMock instance;
	
	private StockMock() {
		this.stockMap = new HashMap<>();
		buildStockMock();
	}

	private void buildStockMock() {
		this.stockMap.put("g001", 129);
		this.stockMap.put("g002", 10);
		this.stockMap.put("g003", 3);
		this.stockMap.put("g004", 1204);
		this.stockMap.put("g005", 331);
		this.stockMap.put("g006", 23);
		this.stockMap.put("g007", 4214);
		this.stockMap.put("g008", 542);
		this.stockMap.put("g009", 2);
	}
	
	/**
	 * Get stock amount of a given good.
	 * @param goodId the id of the good.
	 * @return the stock amount
	 */
	public Integer getStock(String goodId) {
		return this.stockMap.get(goodId);
	}
	
	public static synchronized StockMock getInst() {
		if(instance == null) {
			instance = new StockMock();
		}
		return instance;
	}
}
