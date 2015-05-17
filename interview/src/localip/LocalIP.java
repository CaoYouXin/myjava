package localip;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by 又心 on 2015/5/7.
 */
public class LocalIP {
    /*
    * 获取本机的外网IP, 不使用第三方库
    * @return 本机的外网IP
    * */
    public static String getIP() {
        String ipcn = "http://ip.cn";
        try {
            URL url = new URL(ipcn);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String s, webContent;
            StringBuilder sb = new StringBuilder("");
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\r\n");
            }
            br.close();
            webContent = sb.toString();
            int start = webContent.indexOf("<code>") + 6;
            int end = webContent.indexOf("</code>");
//            System.out.println("webContent=" + webContent);
//            System.out.println("start=" + start);
//            System.out.println("end=" + end);
            if (start < 0 || end < 0) {
                return null;
            }
            webContent = webContent.substring(start, end);
            return webContent;
        } catch (Exception e) {
            e.printStackTrace();
            return "error open url:" + ipcn;
        }
    }

    public static String getIPBackup() {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            return ia.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("local ip: " + getIP());
    }
}
