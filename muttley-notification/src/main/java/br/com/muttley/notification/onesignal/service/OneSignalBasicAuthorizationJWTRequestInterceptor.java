package br.com.muttley.notification.onesignal.service;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

public class OneSignalBasicAuthorizationJWTRequestInterceptor implements RequestInterceptor {
    private final String tokenHeader;
    private final String tokenValue;

    public OneSignalBasicAuthorizationJWTRequestInterceptor(@Value("${muttley.onesignal.tokenHeader:Authorization}") final String tokenHeader, @Value("${muttley.onesignal.tokenValue}") final String tokenValue) {
        this.tokenHeader = tokenHeader;
        this.tokenValue = tokenValue;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(this.tokenHeader, this.tokenValue);
    }
}
