package br.com.muttley.security.infra.feign;

import br.com.muttley.feign.autoconfig.FeignTimeoutConfig;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.rolesconfig.AvaliableRoles;
import br.com.muttley.security.infra.server.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security-server.name-server}", path = "/api/v1/work-teams", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface WorkTeamServiceClient extends RestControllerClient<WorkTeam> {

    @RequestMapping(value = "/roles/current-roles", method = GET, consumes = {APPLICATION_JSON_UTF8_VALUE})
    Set<Role> loadCurrentRoles();

    @RequestMapping(value = "/avaliable-roles", method = GET, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public AvaliableRoles loadAvaliableRoles();
}