package dev.caceresenzo.disposableemaildomains.checker;

public interface Checker {

	boolean test(String domain);

	void reload(boolean force);

}