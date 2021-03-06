package br.com.muttley.security.server.controller;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.security.server.service.JwtTokenUtilService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/api/v1/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {

    private final UserService service;
    private final JwtTokenUtilService tokenUtil;

    @Autowired
    public UserController(final UserService service, final JwtTokenUtilService tokenUtil) {
        this.service = service;
        this.tokenUtil = tokenUtil;
    }

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public ResponseEntity save(@RequestBody final UserPayLoad value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {
        final User record = service.save(new User(value));
        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record.setPreferences(null).toJson());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{userName}", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity update(@PathVariable("userName") final String userName, @RequestHeader(value = "${muttley.security.jwt.controller.tokenHeader-jwt}", defaultValue = "") final String token, @RequestBody final User user) {
        if (isNullOrEmpty(token)) {
            throw new MuttleyBadRequestException(null, null, "informe um token válido");
        }

        final String usernameFromToken = tokenUtil.getUsernameFromToken(token);

        if (isNullOrEmpty(usernameFromToken)) {
            throw new MuttleyBadRequestException(null, null, "informe um token válido");
        }

        if (!usernameFromToken.equals(userName)) {
            throw new MuttleyBadRequestException(null, null, "O token informado não contem o userName " + userName);
        }
        user.setId(service.findByUserName(userName).getId());
        //é necessário válidar a regra de négocio no processo de crud de usuário
        //throw new MuttleyMethodNotAllowedException(null, null, "Verifique a regra de negócios");


        return ResponseEntity.ok(service.update(user, new JwtToken(token)));
    }

    @RequestMapping(value = "/passwd", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity updatePasswd(@RequestBody final Passwd passwd) {
        service.updatePasswd(passwd);
        return ResponseEntity.ok().build();
    }

    /**
     * Faz a deleção por userName ao invez de ID
     */
    @RequestMapping(method = DELETE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public ResponseEntity deleteByUserName(@RequestParam("userName") final String userName) {
        service.removeByUserName(userName);
        return ResponseEntity.ok().build();
    }

    /**
     * Faz a pesquisa pelo userName ao invez do ID
     */
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findByUserName(@RequestParam("userName") final String userName, final HttpServletResponse response) {
        return ResponseEntity.ok(service.findByUserName(userName).toJson());
    }

    @RequestMapping(value = "/email-or-username-or-nickUsers", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findUserByEmailOrUserNameOrNickUsers(@RequestParam(value = "email", required = false) final String email, @RequestParam(value = "userName", required = false) final String userName, @RequestParam(value = "nickUsers", required = false) final Set<String> nickUsers) {
        return ResponseEntity.ok(service.findUserByEmailOrUserNameOrNickUsers(email, userName, nickUsers).toJson());
    }

    @RequestMapping(value = "/exist-email-or-username-or-nickUsers", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity existUserByEmailOrUserNameOrNickUsers(@RequestParam(value = "email", required = false) final String email, @RequestParam(value = "userName", required = false) final String userName, @RequestParam(value = "nickUsers", required = false) final Set<String> nickUsers) {
        return ResponseEntity.ok(service.findUserByEmailOrUserNameOrNickUsers(email, userName, nickUsers).toJson());
    }

    @RequestMapping(value = "/user-from-token", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity getUserFromToken(@RequestBody final JwtToken token) {
        return ResponseEntity.ok(this.service.getUserFromToken(token).toJson());
    }

}
