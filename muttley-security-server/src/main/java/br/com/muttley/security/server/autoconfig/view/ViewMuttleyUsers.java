package br.com.muttley.security.server.autoconfig.view;

import br.com.muttley.mongo.views.source.ViewSource;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 15/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ViewMuttleyUsers implements ViewSource {
    private final String VERSION = "1.0.5";
    private final String NAME = "view_muttley_users";
    private final String SOURCE = "muttley-users";
    private final String DESCRIPTION = "A view foi criada para facilitar a listagem de usuários e seus owners já linkados";

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getViewName() {
        return NAME;
    }

    @Override
    public String getViewOn() {
        return SOURCE;
    }


    @Override
    public List<? extends Bson> getPipeline() {
        /**
         db.getCollection("muttley-users").aggregate([
         //removendo usuários do odin
         {$match:{odinUser : false}},
         //pegando apenas os dados necessários
         {$project:{_id:1, _class:1, name:1, userName:1, description:1}},
         //pegando os possíveis owners linkados ao usuário
         {$lookup:{
         from: 'view_muttley_work_teams',
         localField: '_id',
         foreignField: 'userId',
         as: 'owners'
         }},
         //exibindo apenas os dados necessários
         {$project:{_id:1, _class:1, name:1, userName:1, owners:{"$setUnion":["$owners.owner",[]]}, description:1}},
         ])
         */
        return asList(
                //removendo usuários do odin
                new BsonDocument("$match", new BsonDocument("odinUser", new BsonBoolean(false))),
                //pegando apenas os dados necessários
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", _TRUE),
                                        new BsonElement("_class", _TRUE),
                                        new BsonElement("name", _TRUE),
                                        new BsonElement("userName", _TRUE),
                                        new BsonElement("email", _TRUE),
                                        new BsonElement("nickUsers", _TRUE),
                                        new BsonElement("description", _TRUE)
                                )
                        )
                ),
                //pegando os possíveis owners linkados ao usuário
                new BsonDocument("$lookup",
                        new BsonDocument(
                                asList(
                                        new BsonElement("from", new BsonString("view_muttley_work_teams")),
                                        new BsonElement("localField", new BsonString("_id")),
                                        new BsonElement("foreignField", new BsonString("userId")),
                                        new BsonElement("as", new BsonString("owners"))
                                )
                        )
                ),
                //exibindo apenas os dados necessários
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", _TRUE),
                                        new BsonElement("_class", _TRUE),
                                        new BsonElement("name", _TRUE),
                                        new BsonElement("userName", _TRUE),
                                        new BsonElement("email", _TRUE),
                                        new BsonElement("nickUsers", _TRUE),
                                        new BsonElement("owners", new BsonDocument("$setUnion", new BsonArray(asList(new BsonString("$owners.owner"), new BsonArray())))),
                                        new BsonElement("description", _TRUE)
                                )
                        )
                )
        );

    }

    public String getDescription() {
        return DESCRIPTION;
    }
}
