package br.com.muttley.security.server.controller;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryIdIsNullException;
import br.com.muttley.exception.throwables.repository.MuttleyRepositoryOwnerNotInformedException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.model.security.AccessPlanImpl;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.enumeration.Authorities;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import br.com.muttley.security.server.service.AccessPlanService;
import br.com.muttley.security.server.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/access-plan", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class AccessPlanController extends AbstractRestController<AccessPlanImpl, ObjectId> {

    @Autowired
    public AccessPlanController(final AccessPlanService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(
            @RequestBody final AccessPlanImpl value,
            final HttpServletResponse response,
            @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity,
            @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader) {

        final AccessPlan record = service.save(null, value);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final AccessPlanImpl model,
                                 @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader) {
        model.setId(id);
        return ResponseEntity.ok(service.update(null, model));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteById(@PathVariable("id") final String id,
                                     @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader) {

        service.deleteById(null, deserializerId(id));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findById(@PathVariable("id") final String id, final HttpServletResponse response,
                                   @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader) {

        final AccessPlan value = service.findById(null, deserializerId(id));

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/first", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity first(final HttpServletResponse response,
                                @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader) {

        final AccessPlan value = service.findFirst(null);

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/{id}/historic", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity loadHistoric(@PathVariable("id") final String id, final HttpServletResponse response,
                                       @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader) {
        final Historic historic = service.loadHistoric(null, deserializerId(id));

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(historic);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageableResource> list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams,
                                                 @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader){
        return ResponseEntity.ok(
                toPageableResource(eventPublisher, response, this.service, null, allRequestParams)
        );
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public final ResponseEntity<String> count(@RequestParam final Map<String, Object> allRequestParams,
                                              @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String tokenHeader) {
        return ResponseEntity.ok(String.valueOf(service.count(null, allRequestParams)));
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
        /*if (!user.inAnyRole(roles)) {
            throw new MuttleySecurityCredentialException("Você não tem permissão para acessar este recurso ")
                    .addDetails("isNecessary", roles);
        }*/
    }

    protected final void checkCredentials(final Authorities... roles) {
        /*if (!user.inAnyRole(roles)) {
            throw new MuttleySecurityCredentialException("Você não tem permissão para acessar este recurso ")
                    .addDetails("isNecessary", roles);
        }*/
    }

    @Override
    protected ObjectId deserializerId(final String id) {
        return new ObjectId(id);
    }
}