package dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component(DisposableEmailDomainsProperties.BEAN_NAME)
@ConfigurationProperties(prefix = DisposableEmailDomainsProperties.PREFIX)
public class DisposableEmailDomainsProperties {

	public static final String BEAN_NAME = "disposableEmailDomainsProperties";
	
	public static final String PREFIX = "disposable-email-domains";
	public static final String PREFIX_CHECKERS = PREFIX + ".checkers";
	public static final String PREFIX_CHECKERS_STATIC_DOMAINS = PREFIX_CHECKERS + ".static-domains";

	private CheckersProperties checkers = new CheckersProperties();

	@Data
	public static class CheckersProperties {

		private Duration reloadFixedRate = Duration.ofMinutes(10);

		private boolean dailyUpdatedDomains = true;
		private List<FileCheckerProperties> file = new ArrayList<>();
		private List<HttpCheckerProperties> http = new ArrayList<>();
		private List<String> staticDomains = new ArrayList<>();

		@Data
		public static class FileCheckerProperties {

			private String path;
			private boolean ignoreIfMissing;

		}

		@Data
		public static class HttpCheckerProperties {

			private URI uri;
			private String cachePath;
			private Duration cacheDuration;

		}

	}

}