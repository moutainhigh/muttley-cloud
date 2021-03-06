package br.com.muttley.security.server.controller;

import br.com.muttley.model.Historic;
import br.com.muttley.model.security.Owner;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import br.com.muttley.security.server.service.OwnerService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import static br.com.muttley.security.server.property.MuttleySecurityProperty.TOKEN_HEADER_JWT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/owners", produces = APPLICATION_JSON_VALUE)
public class OwnerController extends AbstractRestController<Owner> {

    @Autowired
    public OwnerController(final OwnerService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@RequestBody final Owner value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity,
                               @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final Owner record = service.save(null, value);
        publishCreateResourceEvent(this.eventPublisher, response, record);
        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final Owner model, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        model.setId(id);
        return ResponseEntity.ok(service.update(null, model));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteById(@PathVariable("id") final String id, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        service.deleteById(null, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findById(@PathVariable("id") final String id, final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final Owner value = service.findById(null, id);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/first", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity first(final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final Owner value = service.findFirst(null);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @RequestMapping(value = "/{id}/historic", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity loadHistoric(@PathVariable("id") final String id, final HttpServletResponse response, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        final Historic historic = service.loadHistoric(null, id);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(historic);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageableResource<Owner>> list(final HttpServletResponse response, @RequestParam final Map<String, String> allRequestParams, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        return ResponseEntity.ok(toPageableResource(eventPublisher, response, this.service, null, allRequestParams));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public final ResponseEntity count(@RequestParam final Map<String, String> allRequestParams, @RequestHeader(value = TOKEN_HEADER_JWT, defaultValue = "") final String tokenHeader) {
        return ResponseEntity.ok(String.valueOf(service.count(null, allRequestParams)));
    }
}
