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

	default boolean testEmail(String email) {
		if (!email.contains("@")) {
			return testDomain(email);
		}

		final var parts = email.split("@", 2);
		return testDomain(parts[1]);
	}

	boolean testDomain(String domain);

	void reload(boolean force);

	public static Builder builder() {
		return new Builder();
	}

	@Data
	@Accessors(fluent = true)
	public static class Builder {

		private List<Checker> checkers = new ArrayList<>();

		public Builder checker(Checker checker) {
			checkers.add(checker);
			return this;
		}

		public Builder githubDailyDisposableEmailDomains() {
			final var temporaryLocation = System.getProperty("java.io.tmpdir");

			return checker(
				HttpChecker.builder()
					.uri("https://disposable.github.io/disposable-email-domains/domains.txt")
					.cache(Path.of(temporaryLocation, "disposable-email-domains.txt"), Duration.ofHours(23))
					.build()
			);
		}

		public Builder file(Path path) {
			return checker(new FileChecker(path));
		}

		public Builder file(Path path, boolean ignoreIfMissing) {
			return checker(new FileChecker(path, ignoreIfMissing));
		}

		public Builder staticDomains(String... domains) {
			return staticDomains(Arrays.asList(domains));
		}

		public Builder staticDomains(Collection<String> domains) {
			return checker(new StaticChecker(domains));
		}

		public DisposableEmailDomains build() {
			return new Default(checkers);
		}

	}

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