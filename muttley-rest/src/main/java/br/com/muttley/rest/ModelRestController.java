package br.com.muttley.rest;

import br.com.muttley.domain.service.ModelService;
import br.com.muttley.exception.throwables.security.MuttleySecurityCredentialException;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.model.enumeration.Authorities;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import br.com.muttley.security.infra.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class ModelRestController<T extends Model, ID extends Serializable> implements RestResource, RestController<T, ID> {
    private final ModelService<T, ID> modelService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public ModelRestController(final ModelService modelService, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        this.modelService = modelService;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@RequestBody final T value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {
        this.checkRoleCreate();
        final T record = modelService.save(this.userService.getCurrentUser(), value);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final T model) {
        checkRoleUpdate();
        model.setId(id);
        return ResponseEntity.ok(modelService.update(this.userService.getCurrentUser(), model));
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteById(@PathVariable("id") final ID id) {
        checkRoleDelete();
        modelService.deleteById(this.userService.getCurrentUser(), id);
        return ResponseEntity.ok().build();
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findById(@PathVariable("id") final ID id, final HttpServletResponse response) {
        checkRoleRead();

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(modelService.findById(this.userService.getCurrentUser(), id));
    }

    @Override
    @RequestMapping(value = "/first", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity first(final HttpServletResponse response) {
        checkRoleRead();
        final T value = modelService.findFirst(this.userService.getCurrentUser());

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(modelService.findFirst(this.userService.getCurrentUser()));
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageableResource> list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams) {
        checkRoleRead();
        return ResponseEntity.ok(toPageableResource(eventPublisher, response, this.modelService, this.userService.getCurrentUser(), allRequestParams));
    }

    @Override
    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public final ResponseEntity<String> count(final Map<String, Object> allRequestParams) {
        checkRoleRead();
        return ResponseEntity.ok(String.valueOf(modelService.count(this.userService.getCurrentUser(), allRequestParams)));
    }

    protected String[] getCreateRoles() {
        return null;
    }

    protected String[] getReadRoles() {
        return null;
    }

    protected String[] getUpdateRoles() {
        return null;
    }

    protected String[] getDeleteRoles() {
        return null;
    }

    protected final void checkRoleCreate() {
        if (!isNull(getCreateRoles()))
            this.checkCredentials(getCreateRoles());

    }

    protected final void checkRoleRead() {
        if (!isNull(getReadRoles()))
            this.checkCredentials(getReadRoles());
    }

    protected final void checkRoleUpdate() {
        if (!isNull(getUpdateRoles()))
            this.checkCredentials(this.getUpdateRoles());
    }

    protected final void checkRoleDelete() {
        if (!isNull(getDeleteRoles()))
            this.checkCredentials(this.getDeleteRoles());
    }

    protected final void checkCredentials(final String... roles) {
        if (!this.userService.getCurrentUser().inAnyRole(roles)) {
            throw new MuttleySecurityCredentialException("Você não tem permissão para acessar este recurso ")
                    .addDetails("isNecessary", roles);
        }
    }

    protected final void checkCredentials(final Authorities... roles) {
        if (!this.userService.getCurrentUser().inAnyRole(roles)) {
            throw new MuttleySecurityCredentialException("Você não tem permissão para acessar este recurso ")
                    .addDetails("isNecessary", roles);
        }
    }
}
