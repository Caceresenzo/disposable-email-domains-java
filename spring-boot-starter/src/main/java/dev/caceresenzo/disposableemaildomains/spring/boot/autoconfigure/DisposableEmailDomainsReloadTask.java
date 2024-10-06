package dev.caceresenzo.disposableemaildomains.spring.boot.autoconfigure;

import org.springframework.scheduling.annotation.Scheduled;

import dev.caceresenzo.disposableemaildomains.DisposableEmailDomains;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DisposableEmailDomainsReloadTask {

	private final DisposableEmailDomains disposableEmailDomains;

	@Scheduled(fixedRateString = "#{@" + DisposableEmailDomainsProperties.BEAN_NAME + ".getCheckers().getReloadFixedRate()}")
	public void callReload() {
		log.trace("calling reload");

		disposableEmailDomains.reload(false);
	}

}