package dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.caceresenzo.disposableemaildomains.DisposableEmailDomains;
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

		if (properties.isDailyUpdatedDomains()) {
			builder.githubDailyDisposableEmailDomains();
		}

		final var customDomains = properties.getCustomDomains();
		if (customDomains != null && !customDomains.isEmpty()) {
			builder.staticDomains(customDomains);
		}

		return builder.build();
	}

}