package de.wwu.pi.wh.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * A simple session bean that connects to a RESTful web service of a warehouse system
 * and provides methods to interact with that system.
 * 
 * @author Andreas Fuchs
 */
@Stateless
public class WarehouseBean {

	protected WebTarget baseTarget; // the base target of the REST resources from the warehouse RESTful web service
	
	protected final String whWebUrl = "http://127.0.0.1:8080/wh-web/webapi/customer/all";
	
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
		Response rStock = baseTarget.path("/stock").queryParam("gid", gid).request().get();
		
		if(rStock.getStatus() == Status.OK.getStatusCode()) {
			Integer stockAmount = (Integer)rStock.getEntity();
			if(stockAmount != null) {
				if(stockAmount - amount < safetyStock) {
					orderBean.orderGoods(gid, 2*safetyStock);
				}
				return stockAmount >= amount;
			}
		}
		
		return false;
	}
}
