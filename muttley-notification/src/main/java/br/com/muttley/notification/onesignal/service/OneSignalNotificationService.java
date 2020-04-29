package br.com.muttley.notification.onesignal.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.notification.onesignal.model.Notification;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@FeignClient(value = "${muttley.onesignal.domain:https://onesignal.com}", path = "/api/v1", configuration = {OneSignalBasicAuthorizationJWTRequestInterceptor.class})
public interface OneSignalNotificationService {
    @RequestMapping(value = "/notifications", method = POST)
    public JwtToken refreshAndGetAuthenticationToken(@RequestBody Notification token);
}