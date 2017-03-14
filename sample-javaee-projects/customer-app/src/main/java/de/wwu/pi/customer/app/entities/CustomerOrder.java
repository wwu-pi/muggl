package de.wwu.pi.customer.app.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CustomerOrder {

	@Id
	protected long orderId;
	
	@ManyToOne
	protected Customer customer;
	
}
