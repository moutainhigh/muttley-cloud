package br.com.muttley.security.server.controller;


import br.com.muttley.model.security.UserView;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.UserViewService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/users-view", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class UserViewController extends AbstractRestController<UserView> {

    public UserViewController(final UserViewService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
    }
}
