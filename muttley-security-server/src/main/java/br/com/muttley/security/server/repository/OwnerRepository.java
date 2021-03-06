package br.com.muttley.security.server.repository;

import br.com.muttley.model.security.Owner;
import br.com.muttley.mongo.repository.SimpleTenancyMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Repositório específico para owner de clientes
 */
@Repository
public interface OwnerRepository extends SimpleTenancyMongoRepository<Owner> {

    Owner findByName(final String nome);
}
