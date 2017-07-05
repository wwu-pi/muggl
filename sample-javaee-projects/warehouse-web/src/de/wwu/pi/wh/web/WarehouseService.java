package de.wwu.pi.wh.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.wwu.pi.wh.mock.StockMock;
import de.wwu.pi.wh.res.Item;
import de.wwu.pi.wh.res.OrderDetails;
import de.wwu.pi.wh.res.Stock;

@Path("/wh")
public class WarehouseService {

	@GET
	@Path("stock/{gid}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getStock(@PathParam("gid") String gid) {
		Integer amount = StockMock.getInst().getStock(gid);
		if(amount == null) {
			// good not in stock!
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.status(Status.OK).entity(amount.toString()).build();
	}
	
	@GET
	@Path("stocknew/{gid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Stock getStockNew(@PathParam("gid") String gid) {
		
		Item item = new Item();
		item.setName("Rote Tasse Sorte XY");
		item.setWeightInGram(300L);
		
		Stock stock = new Stock();
		stock.setItem(item);		
		stock.setQuantity(2000L);
		
		return stock;
	}
	
	@POST
	@Path("order")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response order(OrderDetails order) {
		System.out.println("** got a new order: "+ order);
		return Response.status(Status.OK).build();
	}
	
	@GET
	@Path("remove/{itemId}/{quantity}")
	public Response remove(@PathParam("itemId") String itemId, @PathParam("quantity") long quantity) {
		System.out.println("Remove quantity :" + quantity +" from item with id: "+ itemId);
		return Response.status(Status.OK).build();
	}
}
