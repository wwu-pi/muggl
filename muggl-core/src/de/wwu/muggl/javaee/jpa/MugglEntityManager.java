package de.wwu.muggl.javaee.jpa;

public class MugglEntityManager {
	
	private static MugglEntityManager instance;
	
	private MugglEntityManager() {
	}

	public synchronized static MugglEntityManager getInstance() {
		if(instance == null) {
			instance = new MugglEntityManager();
		}
		return instance;
	}

}
