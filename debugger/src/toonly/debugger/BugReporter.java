package toonly.debugger;

import io.evanwong.oss.hipchat.v2.HipChatClient;
import io.evanwong.oss.hipchat.v2.rooms.MessageColor;
import io.evanwong.oss.hipchat.v2.rooms.MessageFormat;
import io.evanwong.oss.hipchat.v2.rooms.SendRoomNotificationRequestBuilder;
import toonly.configer.ReportConfiger;
import toonly.configer.cache.UncachedException;

import java.util.Objects;

/**
 * Created by caoyouxin on 15-2-25.
 */
public class BugReporter {


    private static final String DEFAULT_ACCESS_TOKEN = "9zN2DuwhwU00OxsLeXofa8oerJuz8hELHHvTMyxt";
    private static final HipChatClient HIPCHAT_CLIENT = new HipChatClient(DEFAULT_ACCESS_TOKEN);

    public static final void closeClient() {
        HIPCHAT_CLIENT.close();
    }

    public static final void reportBug(Object invoker, String msg) {
        reportBug(invoker, msg, null);
    }

    public static void reportBug(Object invoker, String msg, Exception e) {
        _reportBug(invoker.getClass().getName(), msg, e);
    }

    public static final void reportBug(Class<?> invoker, String msg) {
        reportBug(invoker, msg, null);
    }

    public static void reportBug(Class<?> invoker, String msg, Exception e) {
        _reportBug(invoker.getName(), msg, e);
    }

    private static void _reportBug(String name, String msg, Exception e) {
        StringBuilder sb = null;
        if (null != e) {
            sb = new StringBuilder(Objects.toString(e.getMessage(), "没有异常信息")).append("\n");
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement ele : stackTrace) {
                sb.append(ele.toString()).append("\n");
            }
        }
        ReportConfiger config = getConfig();
        SendRoomNotificationRequestBuilder builder = HIPCHAT_CLIENT.prepareSendRoomNotificationRequestBuilder("Nullzone"
                , config
                .report("class", name)
                .report("msg", msg)
                .report("exp", Objects.toString(sb, "NULL"))
                .toString());
        builder.setMessageFormat(MessageFormat.TEXT).setColor(MessageColor.YELLOW).setNotify(true).build().execute();
    }

    private static ReportConfiger getConfig() {
        ReportConfiger reportConfiger = new ReportConfiger();
        try {
            return reportConfiger.cache("bug.report");
        } catch (UncachedException e) {
            return reportConfiger.config("bug.report");
        }
    }

}
