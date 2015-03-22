package toonly.dbmanager.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.wrapper.SW;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by caoyouxin on 15-3-3.
 */
public interface Jsonable extends Entity {

    static final Logger log = LoggerFactory.getLogger(Jsonable.class);

    default boolean fromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = objectMapper.reader().readTree(json);
        } catch (IOException e) {
            log.info("cannot read from json : {}", json);
        }

        if (null == root) {
            return false;
        }

        ECCalculator ecc = new ECCalculator(this);

        SW<Boolean> suc = new SW(true);
        final JsonNode finalRoot = root;
        ecc.dtForEach((f, dt) -> {
            JsonNode jsonNode = finalRoot.get(f);
            if (null != jsonNode) {
                Object o = null;
                switch (dt.type()) {
                    case integer:
                        o = jsonNode.asInt();
                        break;
                    case bitint:
                        o = jsonNode.asLong();
                        break;
                    case shorttext:
                    case longtext:
                        o = jsonNode.asText();
                        break;
                    case bool:
                        o = jsonNode.asBoolean();
                        break;
                    case datetime:
                        //TODO 还要看前端API如何
                        o = LocalDateTime.parse(jsonNode.asText(), DateTimeFormatter.ISO_DATE_TIME);
                        break;
                    default:
                        throw new RuntimeException("no such data type");
                }
                ecc.setValue(f, o);
            } else {
                log.info("cannot read completely from json : {} , field : {}", json, f);
                if (suc.val()) suc.val(false);
            }
        });

        return suc.val();
    }

    default String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.info("cannot write to json from class : [{}]", this.getClass().getName());
            return null;
        }
    }

}
