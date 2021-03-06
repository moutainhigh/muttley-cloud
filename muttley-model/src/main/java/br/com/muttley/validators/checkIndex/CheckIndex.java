package br.com.muttley.validators.checkIndex;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Joel Rodrigues Moreira on 12/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Constraint(validatedBy = {CheckIndexValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(CheckIndex.List.class)
public @interface CheckIndex {
    String message() default "Já existe um registro no banco de dados com essa caracteristica";

    /**
     * Campos que compõe o index
     */
    String[] fields();

    /**
     * Campo que é dono do index.
     * Caso der algum erro de index, a exception {@link br.com.muttley.exception.throwables.MuttleyConflictException}
     * receberá como campo o valor aqui informado
     */
    String fieldOwner();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        CheckIndex[] value();
    }
}
