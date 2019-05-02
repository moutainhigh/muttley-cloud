package br.com.muttley.security.server.service;

import br.com.muttley.domain.service.Service;
import br.com.muttley.model.security.UserView;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface UserViewService extends Service<UserView> {
    List<UserView> list(final String criterio, final String idOwner);

    long count(final String criterio, final String idOwner);
}
