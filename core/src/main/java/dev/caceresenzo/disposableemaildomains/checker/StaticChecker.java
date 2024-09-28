package dev.caceresenzo.disposableemaildomains.checker;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class StaticChecker implements Checker {

	private final Set<String> domains;

	public StaticChecker(Collection<String> domains) {
		this.domains = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		this.domains.addAll(domains);
	}

	@Override
	public boolean test(String domain) {
		return domains.contains(domain);
	}

	@Override
	public void reload(boolean force) {}

	@Override
	public String toString() {
		return "StaticChecker[size=%s]".formatted(domains.size());
	}

}