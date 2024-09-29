package dev.caceresenzo.disposableemaildomains.checker;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FileChecker implements Checker {

	private final @NonNull Path path;
	private final boolean ignoreIfMissing;

	private Instant lastModifiedTime;
	private Set<String> domains = Collections.emptySet();

	public FileChecker(@NonNull Path path) {
		this(path, true);
	}

	@Override
	public boolean test(String domain) {
		return domains.contains(domain);
	}

	@SneakyThrows
	private void load() {
		log.debug("load domains - path=`{}`", path);

		try (
			final var lines = Files.lines(path)
		) {
			final var domains = lines
				.filter(Predicate.not(String::isBlank))
				.collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

			log.debug("loaded domains - size={}", domains.size());

			this.domains = domains;
		} catch (NoSuchFileException exception) {
			if (ignoreIfMissing) {
				log.debug("file is missing - path=`{}`", path);
			} else {
				throw exception;
			}
		}
	}

	@SneakyThrows
	private boolean hasBeenModified() {
		try {
			final var currentLastModifiedTime = Files.getLastModifiedTime(path).toInstant();
			final var isSame = currentLastModifiedTime.equals(this.lastModifiedTime);

			this.lastModifiedTime = currentLastModifiedTime;

			return !isSame;
		} catch (NoSuchFileException __) {
			return false;
		}
	}

	@Override
	public void reload(boolean force) {
		if (force || hasBeenModified()) {
			load();
		}
	}

	@Override
	public String toString() {
		return "FileChecker[path=`%s`, size=%s]".formatted(path, domains.size());
	}

}