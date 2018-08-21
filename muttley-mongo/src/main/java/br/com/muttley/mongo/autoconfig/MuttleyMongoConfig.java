package br.com.muttley.mongo.autoconfig;

import br.com.muttley.mongo.converters.BigDecimalToDecimal128Converter;
import br.com.muttley.mongo.converters.Decimal128ToBigDecimalConverter;
import br.com.muttley.mongo.repository.impl.CustomMongoRepositoryImpl;
import br.com.muttley.mongo.service.MuttleyConvertersService;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.MongoCredential.createCredential;
import static java.util.Collections.singletonList;

/**
 * Classe de configuração de conexão do mongodb<br/>
 * <p><b> Para realizar a configuração de conexão com o MongoDB, siga o exemplo abaixao</b></p>
 * <ul>
 * <li>Após herdar a classe, torne-a uma classe de configuração com @{@link org.springframework.context.annotation.Configuration}</li>
 * <li>Adicione a anotação @{@link org.springframework.data.mongodb.repository.config.EnableMongoRepositories} e informe onde fica os devidos repositórios em <b>basePackages</b> e também se vier ao caso a classe base em <b>repositoryBaseClass</b> </li>
 * <li>A classe base padrão a ser utilizada é {@link CustomMongoRepositoryImpl}</li>
 * </ul>
 *
 * @author Joel Rodrigues Moreira on 10/01/18.
 * @project muttley-cloud
 */
@Configuration
@EnableMongoRepositories(repositoryBaseClass = CustomMongoRepositoryImpl.class)
public class MuttleyMongoConfig extends AbstractMongoConfiguration implements InitializingBean {
    @Autowired
    protected MongoProperties properties;
    @Autowired
    private ObjectProvider<MuttleyConvertersService> convertersSErviceProvider;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(
                singletonList(new ServerAddress(this.properties.getHost(), this.properties.getPort())),
                createCredential(this.properties.getUsername(), this.properties.getDatabase(), this.properties.getPassword()),
                MongoClientOptions
                        .builder()
                        .build()
        );
    }

    @Override
    protected String getDatabaseName() {
        return this.properties.getDatabase();
    }

    @Override
    public final org.springframework.data.convert.CustomConversions customConversions() {
        //pegando os conversores padrão
        final List converters = new ArrayList(2);
        converters.add(new BigDecimalToDecimal128Converter());
        converters.add(new Decimal128ToBigDecimalConverter());

        //pegando instancia do serviço de conversores caso exista
        final MuttleyConvertersService convertersService = convertersSErviceProvider.getIfAvailable();
        if (convertersService != null) {
            //pegando o conversores customizados
            final Converter[] customConversions = convertersService.getCustomConverters();
            //dicionando conversores personalizados
            if (customConversions != null && customConversions.length > 0) {
                for (Converter con : customConversions) {
                    converters.add(con);
                }
            }
        }
        return new MongoCustomConversions(converters);
    }


    @Bean
    @ConditionalOnMissingBean
    public MongoRepositoryFactory getMongoRepositoryFactory() {
        try {
            return new MongoRepositoryFactory(this.mongoTemplate());
        } catch (Exception e) {
            throw new RuntimeException("error creating mongo repository factory", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String message = "Configure MongoDB with host \"" + this.properties.getHost() + "\", port \"" + this.properties.getPort() + "\", data base \"" + getDatabaseName() + "\", with userName \"" + this.properties.getUsername() + "\" and password \"";
        if (properties.getPassword() != null) {
            final char[] passwdLenght = properties.getPassword();
            for (int i = 0; i < passwdLenght.length; i++) {
                passwdLenght[i] = '*';
            }
            message += new String(passwdLenght);
        }
        message += "\"";

        LoggerFactory.getLogger(MuttleyMongoConfig.class).info(message);
    }
}