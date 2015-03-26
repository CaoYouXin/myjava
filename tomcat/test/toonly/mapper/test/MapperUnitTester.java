package toonly.mapper.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by caoyouxin on 15-2-25.
 */
public class MapperUnitTester {

    private static final String CONTENT_TYPE = "Content-Type";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer();

    @Test
    public void putReqTest() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        params.put("name", "fucker");

        HttpPut httpPut = new HttpPut("http://localhost:8080/api/v1/entity/user?f=modify&un=test");
        httpPut.addHeader(new BasicHeader(CONTENT_TYPE, "application/json"));
        httpPut.setEntity(new StringEntity(objectWriter.writeValueAsString(params), "UTF-8"));

        this.reqTest(() -> httpPut);
    }

    @Test
    public void postReqTest() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("code", "a");
        params.put("name", "b");
        params.put("color", "2");
        params.put("size", "2");

        HttpPost httpPost = new HttpPost("http://localhost:8080/api/v1/entity/goods/add");
        httpPost.addHeader(new BasicHeader(CONTENT_TYPE, "text/html"));

        List<NameValuePair> params2 = new ArrayList<>();
        params.forEach((key, value) -> params2.add(new NameValuePair() {
            @Override
            public String getName() {
                return key;
            }

            @Override
            public String getValue() {
                return value.toString();
            }
        }));
        httpPost.setEntity(new UrlEncodedFormEntity(params2));

        this.reqTest(() -> httpPost);
    }

    @Test
    public void getReqTest() throws IOException {
        HttpGet httpGet = new HttpGet("http://localhost:8080/api/v1/entity/goods/select?un=test");

        this.reqTest(() -> httpGet);
    }

    private void reqTest(Supplier<HttpUriRequest> supplier) throws IOException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(20);
        cm.setDefaultMaxPerRoute(4);
        HttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        HttpResponse response = httpClient.execute(supplier.get(), HttpClientContext.create());
        Integer status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String content = entity != null ? EntityUtils.toString(entity) : null;
        LoggerFactory.getLogger(MapperUnitTester.class).info("status: [{}], content: [{}]", status, content);
    }

}
