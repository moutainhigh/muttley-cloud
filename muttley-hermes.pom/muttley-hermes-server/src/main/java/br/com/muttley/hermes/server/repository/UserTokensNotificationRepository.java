package br.com.muttley.hermes.server.repository;

import br.com.muttley.model.hermes.notification.UserTokensNotification;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserView;
import br.com.muttley.mongo.repository.SimpleTenancyMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Joel Rodrigues Moreira on 04/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Repository
public interface UserTokensNotificationRepository extends SimpleTenancyMongoRepository<UserTokensNotification> {
    UserTokensNotification findByUser(final User user);

    @Query("{'user': {'$ref' : ?#{@documentNameConfig.getNameCollectionUser()}, '$id': ?#{[0].getId()}}}")
    UserTokensNotification findByUser(final UserView user);

    @Query("{'user': {'$ref' : ?#{@documentNameConfig.getNameCollectionUser()}, '$id': ?0}}")
    UserTokensNotification findByUser(final String userId);
}
