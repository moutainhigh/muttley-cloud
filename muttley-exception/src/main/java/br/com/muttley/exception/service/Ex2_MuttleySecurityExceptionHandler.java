package br.com.muttley.exception.service;

import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUserNameOrPasswordInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A classe começa com o nome Ex2_ por conta de precedencia de exceptions do Spring
 *
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@ControllerAdvice
@RestController
public class Ex2_MuttleySecurityExceptionHandler {

    private final ErrorMessageBuilder errorMessageBuilder;

    @Autowired
    public Ex2_MuttleySecurityExceptionHandler(final ErrorMessageBuilder errorMessageBuilder) {
        this.errorMessageBuilder = errorMessageBuilder;
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity usernameNotFoundException(final UsernameNotFoundException ex) {
        MuttleySecurityUserNameOrPasswordInvalidException exx = new MuttleySecurityUserNameOrPasswordInvalidException();
        exx.addSuppressed(ex);
        return usernameNotFoundException(exx);
    }

    @ExceptionHandler(value = MuttleySecurityUserNameOrPasswordInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity usernameNotFoundException(final MuttleySecurityUserNameOrPasswordInvalidException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }

    @ExceptionHandler(value = MuttleySecurityUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity usernameNotFoundException(final MuttleySecurityUnauthorizedException ex) {
        return errorMessageBuilder.build(ex).toResponseEntity();
    }
}