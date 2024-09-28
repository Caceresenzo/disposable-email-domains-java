package dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = DisposableEmailDomainsProperties.PREFIX)
public class DisposableEmailDomainsProperties {

	public static final String PREFIX = "disposable-email-domains";

	private boolean dailyUpdatedDomains = true;
	private List<String> customDomains;

}