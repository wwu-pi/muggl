package de.wwu.pi.customer.app.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class Customer {

	@Id
	private String customerId;
	
	@NotNull
	private String name;
	
	@Min(10)
	private int salary;
	
	@OneToMany
	private ArrayList<CustomerOrder> orders;
	
	public Customer() {
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public List<CustomerOrder> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<CustomerOrder> orders) {
		this.orders = orders;
	}

}
