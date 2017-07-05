package de.wwu.pi.wh.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.wwu.pi.wh.entity.OrderDetails;
import de.wwu.pi.wh.entity.Stock;
import de.wwu.pi.wh.ex.WarehouseException;

import static javax.ws.rs.core.MediaType.*;

/**
 * A simple session bean that connects to a RESTful web service of a warehouse system
 * and provides methods to interact with that system.
 * 
 * @author Andreas Fuchs
 */
@Stateless
public class WarehouseBean {

	protected WebTarget baseTarget; // the base target of the REST resources from the warehouse RESTful web service
	
	protected final String whWebUrl = "http://127.0.0.1:9080/warehouse-web-0.3.1-SNAPSHOT/webapi/wh";
	
	protected final int safetyStock = 100; // safety stock for all goods (very simple strategy...)
	
	@Inject OrderBean orderBean;
	
	@PostConstruct private void init() {
        baseTarget = ClientBuilder.newClient().target(whWebUrl);
    }
	
	/**
	 * Check if enough goods are available in the warehouse.
	 * @param gid the id of the good
	 * @param amount the amount of the good
	 * @return true, if enough goods are available - otherwise false
	 */
	public boolean goodsAvailable(String gid, int amount) {
		Response rStock = baseTarget.path("stock/{gid}").resolveTemplate("gid", gid).request().get();
		
		if(rStock.getStatus() == Status.OK.getStatusCode()) {
			String stockAmountString = rStock.readEntity(String.class);
			Integer stockAmount = Integer.parseInt(stockAmountString);
			if(stockAmount != null) {
				if(stockAmount - amount < safetyStock) {
					orderBean.orderGoods(gid, 2*safetyStock);
				}
				return stockAmount >= amount;
			}
		}
		
		return false;
	}
	
	public Stock getStock(String itemId) {
		Stock stock = baseTarget.path("stocknew/{itemId}").resolveTemplate("itemId", itemId).request().get(Stock.class);
		return stock;
	}
	
	/**
	 * Remove the given quantity of the given item from the stock of the warehouse.
	 * @param itemId the id of the item
	 * @param quantity the quantity to remove, must be > 0
	 * @return true, if successfully removed from the stock - otherwise false
	 * @throws WarehouseException
	 */
	public boolean removeFromStock(String itemId, long quantity) throws WarehouseException {
		if(quantity <= 0) {
			throw new WarehouseException("Quantity must at least 1!"); 
		}
		
		// get the current stock from the warehouse
		Stock stock = baseTarget.path("stocknew/{itemId}").resolveTemplate("itemId", itemId).request().get(Stock.class);
		
		if(stock != null) {
			if(stock.getQuantity() - quantity < safetyStock) {
				// order new items
				OrderDetails d = orderBean.orderItems(itemId, stock.getItem().getReorderQuantity());
				Response r = baseTarget.path("order").request().post(Entity.entity(d, APPLICATION_JSON));
				if(r.getStatus() != Status.OK.getStatusCode()) {
					throw new WarehouseException("Could not order!");
				}
			}
			Response r = baseTarget.path("remove/{i}/{q}")
					.resolveTemplate("i", itemId)
					.resolveTemplate("q", quantity)
					.request().get();
			return r.getStatus() == Status.OK.getStatusCode();
		}
		
		throw new WarehouseException("Stock for item:" + itemId + " is not available!");
	}
}
