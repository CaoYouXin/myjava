package yangaiche;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.lowlevel.DS;
import toonly.dbmanager.lowlevel.RS;
import toonly.dbmanager.sqlbuilder.*;
import toonly.wrapper.Bool;
import toonly.wrapper.SW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caols on 15/7/9.
 */
public class Main3 {

    private static final String IP = "120.132.59.94";
    private static final int PORT = 4040;
    private static final String SOURCE_TABLE_ACTIVITY_PAYMENT = "info_activity_record_payments";
    private static final List<String> SOURCE_TABLE_ACTIVITY_PAYMENT_COLUMNS = Arrays.asList("id", "description");
    private static final String SOURCE_TABLE_PAYMENT = "info_payments";
    private static final List<String> SOURCE_TABLE_PAYMENT_COLUMNS = Arrays.asList("id", "description");

    private static String SOURCE_SCHEMA = "ycarapi";
    private static int COUNT = 0;

    // 功能：字符串全角转换为半角
    // 说明：全角空格为12288，半角空格为32
    //       其他字符全角(65281-65374)与半角(33-126)的对应关系是：均相差65248
    // 输入参数：input -- 需要转换的字符串
    // 输出参数：无：
    // 返回值: 转换后的字符串
    private static String fullToHalf(String input) {
        char[] c = input.toCharArray();
        boolean is_counted = false;
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) //全角空格
            {
                c[i] = (char) 32;
                if (!is_counted) {
                    is_counted = true;
                    COUNT++;
                }
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
                if (!is_counted) {
                    is_counted = true;
                    COUNT++;
                }
            }
        }
        return new String(c);
    }

    private static String trans(String value) {
        return replace(replace(value, "\"subject\":\\s*\"(.*?)\""), "\"body\":\\s*\"(.*?)\"");
    }

    private static String replace(String str, String patten) {
        Pattern p = Pattern.compile(patten);
        Matcher matcher = p.matcher(str);
        matcher.find();
        try {
            String replaceFirst = matcher.replaceFirst(fullToHalf(matcher.group(0)));
            return replaceFirst;
        } catch (Exception e) {
            return str;
        }
    }

    public static void main(String[] args) {

//        String str = "{   \"id\": \"ch_yLqHeHXnX1WPHiDGW95iDWzT\",   \"object\": \"charge\",   \"created\": 1434729676,   \"livemode\": true,   \"paid\": false,   \"refunded\": false,   \"app\": \"app_0e1avLbjnPuLmLu5\",   \"channel\": \"alipay_wap\",   \"order_no\": \"201506200001163421\",   \"client_ip\": \"127.0.0.1\",   \"amount\": 990,   \"amount_settle\": 0,   \"currency\": \"cny\",   \"subject\": \"养爱车－9.9元－洗车＋打蜡\",   \"body\": \"养爱车－9.9元－洗车＋打蜡\",   \"time_paid\": null,   \"time_expire\": 1434816076,   \"time_settle\": null,   \"transaction_no\": null,   \"refunds\": {     \"object\": \"list\",     \"url\": \"/v1/charges/ch_yLqHeHXnX1WPHiDGW95iDWzT/refunds\",     \"has_more\": false,     \"data\": []   },   \"amount_refunded\": 0,   \"failure_code\": null,   \"failure_msg\": null,   \"metadata\": {},   \"credential\": {     \"object\": \"credential\",     \"alipay_wap\": {       \"_input_charset\": \"utf-8\",       \"format\": \"xml\",       \"partner\": \"2088611495340946\",       \"req_data\": \"\\u003cauth_and_execute_req\\u003e\\u003crequest_token\\u003e20150620c3fb12c92da47193f62aaa5e909a9a3a\\u003c/request_token\\u003e\\u003c/auth_and_execute_req\\u003e\",       \"sec_id\": \"0001\",       \"service\": \"alipay.wap.auth.authAndExecute\",       \"v\": \"2.0\",       \"sign\": \"mTPpxFjVvOxfyxOfcPHQU+5kDehObvShyMsFVHoL76C2Y1AwEryxDS7d6D3uX9gOL8hyWp8RZfmNZHh2DKR00VyMp3SfTaLAL77HTbIDIX7g6Ftu/c4zAN+Tq7LOZrbRr7L411zZDTs0x5bzkRpiTTVOC/vJgNIdA99iYTzKZqk\\u003d\"     }   },   \"extra\": {     \"success_url\": \"http://pay.yangaiche.com/normal/external_sale_pay_success.html\",     \"cancel_url\": \"http://pay.yangaiche.com/normal/external_sale_pay_fail.html\"   },   \"description\": null }";
//
//        System.out.println(str);
//        System.out.println(trans(str));

        DB db = DB.instance(() -> new DS(IP, PORT, args[0], args[1]));

        if (args.length == 3) {
            SOURCE_SCHEMA = args[2];
        }

        int total = 0;

        transTable(db, SOURCE_SCHEMA, SOURCE_TABLE_ACTIVITY_PAYMENT, SOURCE_TABLE_ACTIVITY_PAYMENT_COLUMNS);

        System.out.println(String.format("fixed %d rows in %s", COUNT, SOURCE_TABLE_ACTIVITY_PAYMENT));
        total += COUNT;
        COUNT = 0;

        transTable(db, SOURCE_SCHEMA, SOURCE_TABLE_PAYMENT, SOURCE_TABLE_PAYMENT_COLUMNS);

        System.out.println(String.format("fixed %d rows in %s", COUNT, SOURCE_TABLE_PAYMENT));
        total += COUNT;
        System.out.println(String.format("fixed %d rows in all", total));
    }

    private static void transTable(DB db, String sourceSchema, String tableName, List<String> columnList) {
        TableId tableId = new TableId(sourceSchema, tableName);
        RS rs = db.simpleQuery(new Select(tableId, columnList).toSql());

        List<Object[]> data = new ArrayList<>();
        while (rs.next()) {
            List<Object> row = new ArrayList<>();

            String value = rs.getString(columnList.get(1));

            if (Objects.isNull(value)) {
                continue;
            }

            row.add(rs.getLong(columnList.get(0)));
            row.add(trans(value));
            row.add(rs.getLong(columnList.get(0)));

            data.add(row.toArray());
        }

//        System.out.println(Arrays.toString(data.get(0)));

        Update update = new Update(tableId, new Equal(tableId, columnList.get(0)));
        for (int i = 1; i < columnList.size(); i++) {
            update.set(new Equal(tableId, columnList.get(i)));
        }
        update.where(new Where(new Equal(tableId, columnList.get(0))));

        db.batchExecute(update.toPreparedSql(), data.size(), data);
    }
}
