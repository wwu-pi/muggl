package de.wwu.pi.customer.app.ejb;

import javax.ejb.Stateless;

import de.wwu.pi.customer.app.entities.Customer;

@Stateless
public class CustomerService {

	public int checkCustomerOrderSize(Customer customer) {
		if(customer.getOrders().size() <= 7) {
			return 11;
		}
		return 22;
	}
	
}
