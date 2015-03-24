package toonly.dbmanager.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.dbmanager.lowlevel.DT;
import toonly.wrapper.SW;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by caoyouxin on 15-3-3.
 */
public interface Jsonable extends Entity {

    default public boolean fromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = objectMapper.reader().readTree(json);
        } catch (IOException e) {
            LoggerFactory.getLogger(Jsonable.class).info("cannot read from json : {}", json);
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
                setValue(ecc, f, dt, jsonNode);
            } else {
                LoggerFactory.getLogger(Jsonable.class).info("cannot read completely from json : {} , field : {}", json, f);
                if (suc.val()) suc.val(false);
            }
        });

        return suc.val();
    }

    public static void setValue(ECCalculator ecc, String f, DT dt, JsonNode jsonNode) {
        Object o = null;
        switch (dt.type()) {
            case INTEGER:
                o = jsonNode.asInt();
                break;
            case LONG:
                o = jsonNode.asLong();
                break;
            case SHORTTEXT:
            case LONGTEXT:
                o = jsonNode.asText();
                break;
            case BOOLEAN:
                o = jsonNode.asBoolean();
                break;
            case DATETIME:
                //TODO 还要看前端API如何
                o = LocalDateTime.parse(jsonNode.asText(), DateTimeFormatter.ISO_DATE_TIME);
                break;
            default:
                LoggerFactory.getLogger(Jsonable.class).info("no such data type");
        }
        ecc.setValue(f, o);
    }

    default public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LoggerFactory.getLogger(Jsonable.class).info("cannot write to json from class : [{}]", this.getClass().getName());
            return null;
        }
    }

}
