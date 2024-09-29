package dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = DisposableEmailDomainsProperties.PREFIX)
public class DisposableEmailDomainsProperties {

	public static final String PREFIX = "disposable-email-domains";

	private CheckersProperties checkers = new CheckersProperties();

	@Data
	public static class CheckersProperties {

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