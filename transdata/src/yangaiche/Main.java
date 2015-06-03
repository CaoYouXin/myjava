package yangaiche;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.lowlevel.DS;
import toonly.dbmanager.lowlevel.RS;
import toonly.dbmanager.sqlbuilder.*;
import toonly.wrapper.SW;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 又心 on 2015/5/30.
 */
public class Main {

    private static final String IP = "120.132.59.94";
    private static final int PORT = 4040;
//    private static final String IP = "127.0.0.1";
//    private static final int PORT = 3306;
    private static final String SOURCE_SCHEMA = "ycar141204";
    private static final String SOURCE_TABLE = "info_car_users";
    private static final List<String> SOURCE_COLUMNS = Arrays.asList("create_time", "gender", "last_modified", "name", "phone_number", "wechat_open_id");

    private static final String TARGET_SCHEMA = "ycarapi_ol_same";
    private static final String TARGET_TABLE1 = "info_users";
    private static final List<String> TARGET_TABLE1_COLUMNS1 = Arrays.asList("create_time", "gender", "last_modified", "name", "phone_number", "phone_number_verified", "sing_in_origin", "open_id", "openid_type");
    private static final List<String> TARGET_TABLE1_COLUMNS2 = Arrays.asList("create_time", "gender", "last_modified", "name", "phone_number", "phone_number_verified", "sing_in_origin");
    private static final String TARGET_TABLE2 = "info_car_users";
    private static final List<String> TARGET_TABLE2_COLUMNS = Arrays.asList("id", "create_time", "disabled", "last_modified");

    public static void main(String[] args) throws IOException {

//        BufferedWriter bw1 = getBufferedWriter("insert_into_info_car.sql");
//        if (bw1 == null) return;
//
//        BufferedWriter bw2 = getBufferedWriter("insert_into_info_car_user.sql");
//        if (bw2 == null) return;

        DB db = DB.instance(() -> new DS(IP, PORT, args[0], args[1]));
//        DB db = DB.instance(() -> new DS(IP, PORT, "developer", "poiuy09876Ycar"));

        boolean suc = db.transaction((conn) -> {
            RS rs = db.simpleQuery(new Select(new TableId(SOURCE_SCHEMA, SOURCE_TABLE), SOURCE_COLUMNS).toSql());

            List<Object[]> os = new ArrayList<>();

            while (rs.next()) {
                Object[] oarray = new Object[TARGET_TABLE1_COLUMNS1.size() + 1];
                final SW<Boolean> flag = new SW<>(true);
                rs.forEach((c, o) -> {
                    if ("wechat_open_id".equals(c)) {
                        if (o == null) {
                            flag.val(false);
                        }
                        oarray[7] = o;
                    } else if ("name".equals(c)) {
                        oarray[TARGET_TABLE1_COLUMNS1.indexOf(c)] = (o == null || o.toString().contains("'")) ? "" : o;
                    } else if ("gender".equals(c)) {
                        oarray[TARGET_TABLE1_COLUMNS1.indexOf(c)] = (o == null) ? "male" : o.toString().toLowerCase();
                    } else {
                        oarray[TARGET_TABLE1_COLUMNS1.indexOf(c)] = o;
                    }
                });
                oarray[5] = 0;
                oarray[6] = "wexin";
                oarray[8] = "wexin";
                oarray[9] = !flag.val();
                os.add(oarray);
            }

//            for (Object[] oarray : os) {
//                System.out.println(Arrays.toString(oarray));
//                TableId tableId1 = new TableId(TARGET_SCHEMA, TARGET_TABLE1);
//                rs = db.simpleQuery(conn, new Select(tableId1, "id").where(new Where(new Equal(tableId1, "phone_number", oarray[TARGET_TABLE1_COLUMNS1.indexOf("phone_number")].toString(), true))).toSql());
//                boolean is = true;
//                while (rs.next()) {
//                    long id = rs.getLong("id");
//                    if (is) {
//                        if (!(boolean) oarray[9]) { // 插入openid为空的数据
//                            db.simpleExecute(conn, new Update(tableId1, new Equal(tableId1, "open_id", oarray[TARGET_TABLE1_COLUMNS1.indexOf("open_id")].toString(), true))
//                                    .set(new Equal(tableId1, "name", oarray[TARGET_TABLE1_COLUMNS1.indexOf("name")].toString(), true))
//                                    .set(new Equal(tableId1, "openid_type", "wexin", true))
//                                    .where(new Where(new Equal(tableId1, "id", "" + id, false))).toSql(), 1);
//                        }
//                        is = false;
//                    }
//                }
//                if (is) {
////                    bw1.newLine();
//                    if ((boolean) oarray[9]) { // 插入openid为空的数据
//                        Object[] shortArray = new Object[TARGET_TABLE1_COLUMNS1.size() - 2];
//                        System.arraycopy(oarray, 0, shortArray, 0, shortArray.length);
//                        db.preparedExecute(conn, new Insert(tableId1, TARGET_TABLE1_COLUMNS2).toPreparedSql(), 1, Arrays.asList(shortArray));
////                        bw1.write(String.format("INSERT INTO %s(%s) VALUES(%d, '%s', %d, '%s')", tableId1.toSql()
////                                , new StringWrapper().wrapJoin(TARGET_TABLE1_COLUMNS2, "`", ", ").val()
////                                , ));
//                    } else {
//                        Object[] shortArray = new Object[TARGET_TABLE1_COLUMNS1.size()];
//                        System.arraycopy(oarray, 0, shortArray, 0, shortArray.length);
//                        db.preparedExecute(conn, new Insert(tableId1, TARGET_TABLE1_COLUMNS1).toPreparedSql(), 1, Arrays.asList(shortArray));
//                    }
//                }
//                rs = db.simpleQuery(conn, new Select(tableId1, "id").where(new Where(new Equal(tableId1, "phone_number", oarray[TARGET_TABLE1_COLUMNS1.indexOf("phone_number")].toString(), true))).toSql());
//                while (rs.next()) {
//                    long id = rs.getLong("id");
//                    TableId tableId2 = new TableId(TARGET_SCHEMA, TARGET_TABLE2);
//                    RS anotherRS = db.simpleQuery(conn, new Select(tableId2, "id").where(new Where(new Equal(tableId2, "id", "" + id, false))).toSql());
//                    is = true;
//                    while (anotherRS.next()) {
//                        is = false;
//                    }
//                    if (is) {
//                        List<Object> params = new ArrayList<>();
//                        params.add(id);
//                        for (int i = 1; i < TARGET_TABLE2_COLUMNS.size(); i++) {
//                            int indexOf1 = TARGET_TABLE1_COLUMNS1.indexOf(TARGET_TABLE2_COLUMNS.get(i));
//                            if (indexOf1 < 0) {
//                                params.add(0);
//                            } else {
//                                params.add(oarray[indexOf1]);
//                            }
//                        }
//
////                        bw2.newLine();
////                        bw2.write(String.format("INSERT INTO %s(%s) VALUES(%d, '%s', %d, '%s')", tableId2.toSql()
////                                , new StringWrapper().wrapJoin(TARGET_TABLE2_COLUMNS, "`", ", ").val(), params.get(0)
////                                , params.get(1).toString(), params.get(2), params.get(3).toString()));
//                        db.preparedExecute(conn, new Insert(tableId2, TARGET_TABLE2_COLUMNS).toPreparedSql(), 1, params);
//                    }
//                }
//            }
//
////            bw1.flush();
//            conn.commit();
        });

        db.close();
        System.out.println("final : " + suc);

    }

//    private static BufferedWriter getBufferedWriter(String filename) throws IOException {
//        File file1 = new File(filename);
//        if (file1.exists()) {
//            System.err.println("FILE ALREADY EXISTS");
//            return null;
//        }
//
//        if (!file1.createNewFile()) {
//            System.err.println("Create sql fail, stopping...");
//            return null;
//        }
//
//        BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1));
//        return bw1;
//    }

}
