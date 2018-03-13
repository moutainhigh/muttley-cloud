package br.com.muttley.mongo.service.repository.impl;

import br.com.muttley.exception.throwables.repository.MuttleyRepositoryOwnerNotInformedException;
import br.com.muttley.model.Historic;
import br.com.muttley.model.Model;
import br.com.muttley.model.Owner;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.muttley.mongo.service.infra.Aggregate.createAggregations;
import static br.com.muttley.mongo.service.infra.Aggregate.createAggregationsCount;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class CustomMongoRepositoryImpl<T extends Model<ID>, ID extends ObjectId> extends DocumentMongoRepositoryImpl<T, ID> implements br.com.muttley.mongo.service.repository.CustomMongoRepository<T, ID> {

    public CustomMongoRepositoryImpl(final MongoEntityInformation<T, ID> metadata, final MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }

    @Override
    public final T save(final Owner owner, final T value) {
        validateOwner(owner);
        value.setOwner(owner);
        return super.save(value);
    }

    @Override
    public final T findOne(final Owner owner, final ID id) {
        validateOwner(owner);
        validateId(id);
        return operations.findOne(
                new Query(
                        where("owner.$id").is(owner.getId())
                                .and("id").is(id)
                ), CLASS
        );
    }

    @Override
    public T findFirst(final Owner owner) {
        validateOwner(owner);
        return operations
                .findOne(
                        new Query(
                                where("owner.$id").is(owner.getId())
                        ), CLASS
                );
    }

    @Override
    public final void delete(final Owner user, final ID id) {
        validateOwner(user);
        validateId(id);
        operations.remove(
                new Query(
                        where("owner.$id").is(user.getId())
                                .and("id").is(id)
                ), CLASS
        );
    }

    @Override
    public final void delete(final Owner owner, final T value) {
        delete(owner, value.getId());
    }

    @Override

    public final List<T> findAll(final Owner owner, final Map<String, Object> queryParams) {
        validateOwner(owner);
        return operations.aggregate(
                newAggregation(
                        createAggregations(
                                CLASS,
                                new HashMap<>(addOwnerQueryParam(owner, ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>())))
                        )
                ),
                COLLECTION, CLASS)
                .getMappedResults();
    }

    @Override
    public final long count(final Owner owner, final Map<String, Object> queryParams) {
        validateOwner(owner);
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        createAggregationsCount(
                                CLASS,
                                new HashMap<>(addOwnerQueryParam(owner, ((queryParams != null && !queryParams.isEmpty()) ? queryParams : new HashMap<>())))
                        )),
                COLLECTION, ResultCount.class);
        return result.getUniqueMappedResult() != null ? ((ResultCount) result.getUniqueMappedResult()).getCount() : 0;
    }

    @Override
    public final boolean exists(final Owner owner, final T value) {
        return exists(owner, value.getId());
    }

    @Override
    public final boolean exists(final Owner owner, final ID id) {
        validateOwner(owner);
        return operations.exists(
                new Query(
                        where("owner.$id").is(owner.getId())
                                .and("id").is(id)
                ), CLASS
        );
    }

    @Override
    public Historic loadHistoric(final Owner owner, final T value) {
        return this.loadHistoric(owner, value.getId());
    }

    @Override
    public Historic loadHistoric(final Owner owner, final ID id) {
        final AggregationResults result = operations.aggregate(
                newAggregation(
                        match(where("owner.$id").is(owner.getId())
                                .and("_id").is(id)
                        ), project().and("$historic.createdBy").as("createdBy")
                                .and("$historic.dtCreate").as("dtCreate")
                                .and("$historic.dtChange").as("dtChange")
                ), COLLECTION, Historic.class);

        return result.getUniqueMappedResult() != null ? ((Historic) result.getUniqueMappedResult()) : null;
    }

    private final void validateOwner(final Owner owner) {
        if (owner == null) {
            throw new MuttleyRepositoryOwnerNotInformedException(this.CLASS);
        }
    }

    private final Map<String, Object> addOwnerQueryParam(final Owner owner, final Map<String, Object> queryParams) {
        final Map<String, Object> query = new HashMap<>(1);
        query.put("owner.$id.$is", owner.getId());
        if (queryParams != null) {
            query.putAll(queryParams);
        }
        return query;
    }
}