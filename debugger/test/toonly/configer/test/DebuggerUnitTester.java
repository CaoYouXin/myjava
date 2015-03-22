package toonly.configer.test;

import io.evanwong.oss.hipchat.v2.HipChatClient;
import io.evanwong.oss.hipchat.v2.rooms.MessageColor;
import io.evanwong.oss.hipchat.v2.rooms.MessageFormat;
import io.evanwong.oss.hipchat.v2.rooms.SendRoomNotificationRequestBuilder;
import org.junit.Test;
import toonly.configer.ReportConfiger;
import toonly.debugger.BugReporter;
import toonly.debugger.Debugger;
import toonly.debugger.Feature;

import java.util.Objects;

/**
 * Created by caoyouxin on 15-2-22.
 */
public class DebuggerUnitTester {

    @Test
    public void test() {
        String name = "中文", msg = "哇咔咔";
        Exception e = null;

        StringBuilder sb = null;
        if (null != e) {
            sb = new StringBuilder(e.getMessage()).append("\n");
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement ele : stackTrace) {
                sb.append(ele.toString()).append("\n");
            }
        }

        String defaultAccessToken = "9zN2DuwhwU00OxsLeXofa8oerJuz8hELHHvTMyxt";
        HipChatClient client = new HipChatClient(defaultAccessToken);
        SendRoomNotificationRequestBuilder builder = client.prepareSendRoomNotificationRequestBuilder("Nullzone"
                , new ReportConfiger().config("bug.report")
                        .report("class", name)
                        .report("msg", msg)
                        .report("exp", Objects.toString(sb, "NULL"))
                        .toString()
                );
        builder.setMessageFormat(MessageFormat.TEXT).setColor(MessageColor.YELLOW).setNotify(true).build().execute();
        //NOT optional...
//        try {
//            NoContent noContent = future.get();
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        } catch (ExecutionException ex) {
//            ex.printStackTrace();
//        }

//        new Thread(() -> {
//            try {
//                future.get();
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            } catch (ExecutionException e1) {
//                e1.printStackTrace();
//            }
//        }).start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void reportBugTest() {
        BugReporter.reportBug(this, "你好！", new Exception("哦哦哦"));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void featureTest() {
        Feature.set(6);
        Debugger.debugRun(this, () -> System.out.println("哈哈哈哈"));
    }

}
