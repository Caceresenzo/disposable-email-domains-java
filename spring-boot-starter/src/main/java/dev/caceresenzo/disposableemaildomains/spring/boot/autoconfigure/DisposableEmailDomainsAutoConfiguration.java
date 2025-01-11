package dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import dev.caceresenzo.disposableemaildomains.DisposableEmailDomains;
import dev.caceresenzo.disposableemaildomains.checker.HttpChecker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DisposableEmailDomains.class)
@EnableConfigurationProperties
public class DisposableEmailDomainsAutoConfiguration {

	@Bean(DisposableEmailDomainsProperties.BEAN_NAME)
	DisposableEmailDomainsProperties disposableEmailDomainsProperties() {
		return new DisposableEmailDomainsProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	DisposableEmailDomains disposableEmailDomains(
		DisposableEmailDomainsProperties properties
	) throws IOException {
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

	@Bean
	@ConditionalOnProperty(name = DisposableEmailDomainsProperties.PREFIX_CHECKERS_RELOAD_ENABLED, havingValue = "true", matchIfMissing = true)
	@ConditionalOnBean(ScheduledAnnotationBeanPostProcessor.class)
	DisposableEmailDomainsReloadTask disposableEmailDomainsReloadTask(
		DisposableEmailDomains disposableEmailDomains
	) {
		return new DisposableEmailDomainsReloadTask(
			disposableEmailDomains
		);
	}

}