package toonly.hipchatclient.test;

import io.evanwong.oss.hipchat.v2.HipChatClient;
import io.evanwong.oss.hipchat.v2.rooms.MessageColor;
import io.evanwong.oss.hipchat.v2.rooms.MessageFormat;
import io.evanwong.oss.hipchat.v2.rooms.SendRoomNotificationRequest;
import io.evanwong.oss.hipchat.v2.rooms.SendRoomNotificationRequestBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by caoyouxin on 15-2-25.
 */
public class HipchatUnitTester {

    @Test
    public void executorTest() {
        String defaultAccessToken = "9zN2DuwhwU00OxsLeXofa8oerJuz8hELHHvTMyxt";
        HipChatClient client = new HipChatClient(defaultAccessToken);
        SendRoomNotificationRequestBuilder builder = client
                .prepareSendRoomNotificationRequestBuilder("Nullzone", "哈哈哈哈");
        SendRoomNotificationRequest request = builder.setMessageFormat(MessageFormat.TEXT)
                .setColor(MessageColor.YELLOW).setNotify(true).build();

        Method method = null;
        try {
            method = request.getClass().getSuperclass().getDeclaredMethod("request");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        final Method finalMethod = method;
        executorService.execute(() -> {
            System.out.println("fuck first");

            try {
                HttpResponse response = (HttpResponse) finalMethod.invoke(request);
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String content = entity != null ? EntityUtils.toString(entity) : null;
                System.out.println(String.format("status : [%d]; content : [%s]", status, content));
            } catch (IOException | IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }

            System.out.println("fuck last");
        });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
