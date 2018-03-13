package br.com.muttley.domain.service.impl;

import br.com.muttley.domain.service.Service;
import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.model.User;
import br.com.muttley.mongo.service.repository.DocumentMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class ServiceImpl<T extends Document, ID extends ObjectId> implements Service<T, ID> {

    protected final DocumentMongoRepository<T, ID> repository;
    protected final Class<T> clazz;

    @Autowired
    protected Validator validator;

    public ServiceImpl(final DocumentMongoRepository<T, ID> repository, final Class<T> clazz) {
        this.repository = repository;
        this.clazz = clazz;
    }

    @Override
    public T save(final User user, final T value) {
        //verificando se realmente está criando um novo registro
        if (value.getId() != null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível criar um registro com um id existente");
        }
        //garantindo que o históriconão ficará nulo
        value.setHistoric(this.createHistoric(user));
        //validando dados
        this.validator.validate(value);
        //verificando precondições
        this.checkPrecondictionSave(user, value);
        return repository.save(value);
    }

    @Override
    public void checkPrecondictionSave(final User user, final T value) {

    }

    @Override
    public T update(final User user, final T value) {
        //verificando se realmente está alterando um registro
        if (value.getId() == null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível alterar um registro sem informar um id válido");
        }
        //verificando se o registro realmente existe
        if (!this.repository.exists((ID) value.getId())) {
            throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        //gerando histórico de alteração
        value.setHistoric(generateHistoricUpdate(user, repository.loadHistoric(value)));
        //validando dados
        this.validator.validate(value);
        //verificando precondições
        checkPrecondictionUpdate(user, value);
        return repository.save(value);
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final T value) {

    }

    @Override
    public T findById(final User user, final ID id) {
        if (isNull(id)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe um id válido");
        }

        final T result = this.repository.findOne(id);
        if (isNull(id)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        return result;
    }

    @Override
    public T findFirst(final User user) {
        final T result = this.repository.findFirst();
        if (isNull(result)) {
            throw new MuttleyNotFoundException(clazz, "user", "Nenhum registro encontrado");
        }
        return result;
    }

    @Override
    public Historic loadHistoric(final User user, final ID id) {
        final Historic historic = this.repository.loadHistoric(id);
        if (isNull(historic)) {
            throw new MuttleyNotFoundException(clazz, "historic", "Nenhum registro encontrado");
        }
        return historic;
    }

    @Override
    public Historic loadHistoric(final User user, final T value) {
        return this.loadHistoric(user, (ID) value.getId());
    }

    @Override
    public void deleteById(final User user, final ID id) {
        checkPrecondictionDelete(user, id);
        if (!repository.exists(id)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        this.repository.delete(id);
        beforeDelete(user, id);
    }

    @Override
    public void delete(final User user, final T value) {
        checkPrecondictionDelete(user, (ID) value.getId());
        if (!repository.exists(value)) {
            throw new MuttleyNotFoundException(clazz, "id", value.getId() + " este registro não foi encontrado");
        }
        this.repository.delete(value);
        beforeDelete(user, value);
    }

    @Override
    public void checkPrecondictionDelete(final User user, final ID id) {

    }

    @Override
    public void beforeDelete(final User user, final T value) {

    }

    @Override
    public void beforeDelete(final User user, final ID id) {

    }

    @Override
    public Long count(final User user, final Map<String, Object> allRequestParams) {
        return this.repository.count(allRequestParams);
    }

    @Override
    public List<T> findAll(final User user, final Map<String, Object> allRequestParams) {
        final List<T> results = this.repository.findAll(allRequestParams);
        if (isEmpty(results)) {
            throw new MuttleyNoContentException(clazz, "user", "não foi encontrado nenhum registro");
        }
        return results;
    }

    protected Historic createHistoric(final User user) {
        return new Historic()
                .setCreatedBy(user)
                .setDtCreate(new Date());
    }

    protected Historic generateHistoricUpdate(final User user, final Historic historic) {
        return historic
                .setLastChangeBy(user)
                .setDtChange(new Date());
    }
}
