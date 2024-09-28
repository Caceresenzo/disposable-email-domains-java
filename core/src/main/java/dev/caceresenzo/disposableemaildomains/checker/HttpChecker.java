package dev.caceresenzo.disposableemaildomains.checker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class HttpChecker implements Checker {

	private final @NonNull URI uri;
	private final @NonNull Optional<Cache> cache;
	private final @NonNull HttpClient httpClient;

	private Set<String> domains = Collections.emptySet();

	@Override
	public boolean test(String domain) {
		return domains.contains(domain);
	}

	@SneakyThrows
	private void download() {
		final var temporaryPath = Files.createTempFile("list", ".txt");

		try {
			log.info("download domains - uri=`{}`", uri);

			final var request = HttpRequest.newBuilder()
				.uri(uri)
				.GET()
				.build();

			final var response = httpClient.send(request, BodyHandlers.ofFile(temporaryPath));

			final var statusCode = response.statusCode();
			if (statusCode != 200) {
				throw new IllegalStateException("%s response is not 200: %s".formatted(uri, statusCode));
			}

			loadFrom(temporaryPath);

			if (cache.isPresent()) {
				final var cachePath = cache.get().path();

				log.debug("update cache - from=`{}` to=`{}`", temporaryPath, cachePath);

				Files.move(
					temporaryPath,
					cachePath,
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.ATOMIC_MOVE
				);
			}
		} finally {
			Files.deleteIfExists(temporaryPath);
		}
	}

	@SneakyThrows
	private void loadFrom(Path path) {
		log.debug("load domains - path=`{}`", path);

		try (
			final var lines = Files.lines(path)
		) {
			final var domains = lines
				.filter(Predicate.not(String::isBlank))
				.collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

			log.debug("loaded domains - size={}", domains.size());

			this.domains = domains;
		}
	}

	private boolean isCacheFresh() {
		if (cache.isEmpty()) {
			return false;
		}

		return cache.get().isFresh();
	}

	@Override
	public void reload(boolean force) {
		if (force || !isCacheFresh()) {
			download();
		} else if (domains.isEmpty()) {
			loadFrom(cache.get().path());
		}
	}

	@Override
	public String toString() {
		return "HttpChecker[uri=%s, size=%s]".formatted(uri, domains.size());
	}

	public record Cache(
		Path path,
		Duration freshness
	) {

		@SneakyThrows
		public boolean isFresh() {
			try {
				final var lastModifiedTime = Files.getLastModifiedTime(path);

				return lastModifiedTime.toInstant()
					.plus(freshness)
					.isAfter(Instant.now());
			} catch (NoSuchFileException __) {
				return false;
			}
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	@Data
	@Accessors(fluent = true)
	public static class Builder {

		public static final Duration DEFAULT_CACHE_FRESHNESS = Duration.ofHours(8);

		private URI uri;
		private Cache cache;
		private HttpClient httpClient;

		@Tolerate
		public Builder uri(String uri) {
			return uri(URI.create(uri));
		}

		@Tolerate
		public Builder noCache() {
			return cache((Cache) null);
		}

		@Tolerate
		public Builder cache(Path path) {
			return cache(new Cache(path, DEFAULT_CACHE_FRESHNESS));
		}

		@Tolerate
		public Builder cache(Path path, Duration freshness) {
			return cache(new Cache(path, freshness));
		}

		public HttpChecker build() {
			if (httpClient == null) {
				httpClient = HttpClient.newBuilder().build();
			}

			return new HttpChecker(
				uri,
				Optional.ofNullable(cache),
				httpClient
			);
		}

	}

}