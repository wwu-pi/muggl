package de.wwu.pi.wh.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.wwu.pi.wh.mock.StockMock;

@Path("/wh")
public class WarehouseService {

	@GET
	@Path("stock/{gid}")
	public Response getStock(@PathParam("gid") String gid) {
		Integer amount = StockMock.getInst().getStock(gid);
		
		if(amount == null) {
			// good not in stock!
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.status(Status.OK).entity(amount).build();
	}
	
}
