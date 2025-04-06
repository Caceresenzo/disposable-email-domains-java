package dev.caceresenzo.disposableemaildomains;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dev.caceresenzo.disposableemaildomains.checker.Checker;
import dev.caceresenzo.disposableemaildomains.checker.FileChecker;
import dev.caceresenzo.disposableemaildomains.checker.HttpChecker;
import dev.caceresenzo.disposableemaildomains.checker.StaticChecker;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

public interface DisposableEmailDomains {

	/**
	 * Test if the given email is disposable.
	 * 
	 * @param email Email to test.
	 * @return <code>true</code> if the email's domain is on a list of disposable domains, <code>false</code> otherwise.
	 * @implNote If the email does not contain an '@' character, it is interpreted as a domain. Otherwise, the email is split at the '@' and the second part is considered a domain.
	 */
	default boolean testEmail(String email) {
		if (!email.contains("@")) {
			return testDomain(email);
		}

		final var parts = email.split("@", 2);
		return testDomain(parts[1]);
	}

	/**
	 * Test if a domain provide disposable emails.
	 * 
	 * @param email Domain to test.
	 * @return <code>true</code> if the domain is on a list of disposable domains, <code>false</code> otherwise.
	 */
	boolean testDomain(String domain);

	/**
	 * Reload all checkers. <br />
	 * For dynamic checkers (like {@link HttpChecker}), it will update the cache by downloading the file again.
	 * 
	 * @param force <code>true</code> to force reloading of all checkers, <code>false</code> to reload only checkers that have expired.
	 */
	void reload(boolean force);

	/** @return A new {@link Default default} builder. */
	public static Builder builder() {
		return new Builder();
	}

	@Data
	@Accessors(fluent = true)
	public static class Builder {

		private List<Checker> checkers = new ArrayList<>();

		/**
		 * Add a checker to the list of checkers.
		 * 
		 * @param checker Checker to add.
		 * @return <code>this</code>
		 */
		public Builder checker(Checker checker) {
			checkers.add(checker);
			return this;
		}

		/**
		 * Add the <a href="https://github.com/disposable/disposable-email-domains">Daily Updated Disposable Email Domains list from Github</a>. <br />
		 * It is implemented as a {@link HttpChecker} with a cache of 23 hours.
		 * 
		 * @return <code>this</code>
		 */
		public Builder githubDailyDisposableEmailDomains() {
			final var temporaryLocation = System.getProperty("java.io.tmpdir");

			return checker(
				HttpChecker.builder()
					.uri("https://disposable.github.io/disposable-email-domains/domains.txt")
					.cache(Path.of(temporaryLocation, "disposable-email-domains.txt"), Duration.ofHours(23))
					.build()
			);
		}

		/**
		 * Add a checker that loads a list from a file. <br />
		 * If the file does not exist, it is simply ignored.
		 * 
		 * @param path Path to the file.
		 * @return <code>this</code>
		 */
		public Builder file(Path path) {
			return checker(new FileChecker(path));
		}

		/**
		 * Add a checker that loads a list from a file.
		 * 
		 * @param path Path to the file.
		 * @param ignoreIfMissing If <code>true</code>, the checker will throw an exception if the file does not exist.
		 * @return <code>this</code>
		 */
		public Builder file(Path path, boolean ignoreIfMissing) {
			return checker(new FileChecker(path, ignoreIfMissing));
		}

		/**
		 * Specify static domains directly. <br />
		 * If the array is empty, no checker is added.
		 * 
		 * @param domains Domains to consider as disposable.
		 * @return <code>this</code>
		 */
		public Builder staticDomains(String... domains) {
			return staticDomains(Arrays.asList(domains));
		}

		/**
		 * Specify static domains directly. <br />
		 * If the collection is empty, no checker is added.
		 * 
		 * @param domains Domains to consider as disposable.
		 * @return <code>this</code>
		 */
		public Builder staticDomains(Collection<String> domains) {
			if (domains.isEmpty()) {
				return this;
			}

			return checker(new StaticChecker(domains));
		}

		/** @return The built {@link DisposableEmailDomains} instance. */
		public DisposableEmailDomains build() {
			return new Default(checkers);
		}

	}

	/**
	 * Default implementation of {@link DisposableEmailDomains}. <br />
	 * It is a simple wrapper around a list of {@link Checker} instances.
	 * 
	 * @implNote The {@link DisposableEmailDomains#reload(boolean) reload(false)} method is called in the constructor.
	 */
	@Slf4j
	@ToString
	public static class Default implements DisposableEmailDomains {

		private List<Checker> checkers;

		public Default(List<Checker> checkers) {
			this.checkers = checkers;

			reload(false);
		}

		public List<Checker> getCheckers() {
			return Collections.unmodifiableList(checkers);
		}

		@Override
		public boolean testDomain(String domain) {
			for (final var checker : checkers) {
				if (checker.test(domain)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public void reload(boolean force) {
			for (final var checker : checkers) {
				try {
					checker.reload(force);
				} catch (Exception exception) {
					log.error("could not reload - checker=`%s` force=%s".formatted(checker, force), exception);
				}
			}
		}

	}

}