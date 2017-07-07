package de.wwu.pi.customer.app.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.wwu.pi.customer.app.entities.Customer;

@Stateless
public class CustomerService {

	@PersistenceContext
	protected EntityManager em;

	public int checkCustomerOrderSize(Customer customer) {
		if(customer.getOrders().size() <= 7) {
			return 11;
		}
		return 22;
	}
	
	public boolean doesCustomerExist(String cid) {
		return em.find(Customer.class, cid) != null;
	}
	
	public void incrementStatus(int s, Date d, long r) {
		String ql = "SELECT oi.order.customer " 
				+ "FROM OrderItem oi " 
				+ "  JOIN oi.order o " 
				+ "  JOIN o.customer c "
				+ "WHERE o.orderDate > :d "
				+ "  AND oi.price <= 10 "
				+ "GROUP BY c.id " 
				+ "HAVING SUM(oi.price) > :r";
		List<Customer> cList = em.createQuery(ql, Customer.class)
				.setParameter("r", r)
				.setParameter("d", d)
				.getResultList();
		for (Customer c : cList) {
			if (c.getStatus() < s) {
				c.setStatus(s);
			}
		}
	}
}
