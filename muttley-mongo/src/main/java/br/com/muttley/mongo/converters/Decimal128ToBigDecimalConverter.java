package br.com.muttley.mongo.converters;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.muttley.model.util.BigDecimalUtil.setDefaultScale;

@Component
@ReadingConverter
public class Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {

    @Override
    public BigDecimal convert(final Decimal128 source) {
        return source == null ? null : setDefaultScale(source.bigDecimalValue());
    }
}
