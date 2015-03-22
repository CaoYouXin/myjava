package toonly.mapper.ret;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import toonly.wrapper.Bool;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cls on 15-3-4.
 */
public class RB {

    private Map<String, Object> _normal_data;

    public RB put(String suc, boolean b) {
        return this.put(suc, (b ? Bool.TRUE : Bool.FALSE).toString());
    }

    public RB put(String key, String value) {
        return this._put(key, value);
    }

    public RB put(String key, int value) {
        return this._put(key, value);
    }

    public RB put(String key, RB value) {
        return this._put(key, value);
    }

    public RB put(String key, RBArray array) {
        return this._put(key, array);
    }

    private RB _put(String key, Object value) {
        if (null == this._normal_data) {
            this._normal_data = new HashMap<>();
        }
        this._normal_data.put(key, value);
        return this;
    }

    public String toJson() throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonFactory jsonF = new JsonFactory();
        JsonGenerator jg = jsonF.createGenerator(stringWriter);
        jg.setCodec(new ObjectMapper());
        TreeNode root = new ObjectNode(JsonNodeFactory.instance, getTreeNode(this));
        jg.writeTree(root);
        jg.flush();
        jg.close();
        stringWriter.flush();
        return stringWriter.getBuffer().toString();
    }

    private Map<String, JsonNode> getTreeNode(RB builder) {
        Map<String, JsonNode> nodes = new HashMap<>();
        builder._normal_data.forEach((key, value) -> {
            if (value instanceof RB) {
                nodes.put(key, new ObjectNode(JsonNodeFactory.instance, getTreeNode((RB) value)));
            } else if (value instanceof RBArray) {
                ArrayNode jsonNodes = new ArrayNode(JsonNodeFactory.instance);
                ((RBArray) value).forEach((rb) -> jsonNodes.add(new ObjectNode(JsonNodeFactory.instance, getTreeNode(rb))));
                nodes.put(key, jsonNodes);
            } else if (value instanceof String) {
                nodes.put(key, new TextNode((String) value));
            } else if (value instanceof Integer) {
                nodes.put(key, new IntNode((Integer) value));
            }
        });
        return nodes;
    }

}
