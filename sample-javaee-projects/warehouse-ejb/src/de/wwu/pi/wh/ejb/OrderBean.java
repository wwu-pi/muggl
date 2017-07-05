package de.wwu.pi.wh.ejb;

import javax.ejb.Stateless;

/**
 * A simple bean that handles order tasks.
 * 
 * @author Andreas Fuchs
 *
 */
@Stateless
public class OrderBean {

	public void orderGoods(String gid, int amount) {
		// call another system to order goods (for demo, not needed)
	}
}
