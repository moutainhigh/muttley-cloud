package br.com.muttley.exception.autoconfig;

import br.com.muttley.exception.ErrorMessageBuilder;
import br.com.muttley.exception.controllers.ConfigEndPointsErros;
import br.com.muttley.exception.controllers.ErrorsController;
import br.com.muttley.exception.feign.FeignErrorDecoder;
import br.com.muttley.exception.property.MuttleyExceptionProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 18/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@EnableConfigurationProperties(MuttleyExceptionProperty.class)
public class MuttleyExceptionConfig {

    @Bean
    @ConditionalOnMissingBean
    public FeignErrorDecoder createFeignErrorDecoder(@Autowired final ObjectMapper objectMapper) {
        return new FeignErrorDecoder(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorMessageBuilder errorMessageBuilderFactory() {
        return new ErrorMessageBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigEndPointsErros configEndPointsErrosFactory() {
        return new ConfigEndPointsErros();
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorsController errorsControllerFactory() {
        return new ErrorsController();
    }
}