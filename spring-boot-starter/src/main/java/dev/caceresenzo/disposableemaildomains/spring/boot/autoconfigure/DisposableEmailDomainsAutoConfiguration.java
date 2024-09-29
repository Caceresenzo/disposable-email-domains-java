package dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.caceresenzo.disposableemaildomains.DisposableEmailDomains;
import dev.caceresenzo.disposableemaildomains.checker.HttpChecker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DisposableEmailDomains.class)
@EnableConfigurationProperties(DisposableEmailDomainsProperties.class)
public class DisposableEmailDomainsAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	DisposableEmailDomains disposableEmailDomains(DisposableEmailDomainsProperties properties) throws IOException {
		log.info("Configuring Disposable Email Domains");

		final var builder = DisposableEmailDomains.builder();

		final var checkers = properties.getCheckers();

		if (checkers.isDailyUpdatedDomains()) {
			builder.githubDailyDisposableEmailDomains();
		}

		for (final var fileProperties : checkers.getFile()) {
			builder.file(
				Path.of(fileProperties.getPath()),
				fileProperties.isIgnoreIfMissing()
			);
		}

		for (final var httpProperties : checkers.getHttp()) {
			final var httpBuilder = HttpChecker.builder()
				.uri(httpProperties.getUri());

			final var cachePath = httpProperties.getCachePath();
			if (cachePath != null) {
				final var path = Path.of(cachePath);
				final var cacheDuration = httpProperties.getCacheDuration();

				if (cacheDuration != null) {
					httpBuilder.cache(path, cacheDuration);
				} else {
					httpBuilder.cache(path);
				}
			}

			builder.checker(httpBuilder.build());
		}

		final var staticDomains = checkers.getStaticDomains();
		if (!staticDomains.isEmpty()) {
			builder.staticDomains(staticDomains);
		}

		return builder.build();
	}

}