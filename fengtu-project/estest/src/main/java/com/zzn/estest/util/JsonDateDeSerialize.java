package com.zzn.estest.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.time.FastDateParser;
import org.joda.time.DateTimeUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class JsonDateDeSerialize extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            if(jsonParser!=null&& StringUtils.isNotEmpty(jsonParser.getText())){
                Date parse = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").parse(jsonParser.getText());
                return parse;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
