package dev.caceresenzo.disposableemaildomains.checker;

public interface Checker {

	/**
	 * Check if the given domain is disposable.
	 * 
	 * @param domain Domain to check.
	 * @return <code>true</code> if the domain is disposable, <code>false</code> otherwise.
	 */
	boolean test(String domain);

	/**
	 * Reload the domains.
	 * 
	 * @param force If <code>true</code>, the domains will be reloaded even if the data source has not expired.
	 */
	void reload(boolean force);

}