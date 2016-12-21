package de.wwu.muggl.vm.execution.nativeWrapping;

import java.util.Properties;

public class VMPropertiesWrapper extends Properties {
	private static final long serialVersionUID = -8869583534706524980L;

	/* (non-Javadoc)
	 * @see java.util.Properties#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String key) {
		// TODO Try getting this from local store (currently fails because Hashtable is not initialized, so arraylength on Properties returns a null pointer exception).
		/*String stored =  super.getProperty(key);
		if (stored != null) {
			return stored;
		}*/
		// Not found in local store; querying host VM
		return getPropertyFromHostVM(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#isEmpty()
	 */
	@Override
	public synchronized boolean isEmpty() {
		// No need to check whether properties are empty:
		// Host VM is definitely initialised at this point, 
		// therefore properties can always be found there.
		return false;
	}

	private native String getPropertyFromHostVM(String key);

	
}
