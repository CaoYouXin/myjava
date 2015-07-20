package yangaiche;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.lowlevel.DS;
import toonly.dbmanager.lowlevel.RS;
import toonly.dbmanager.sqlbuilder.Select;
import toonly.dbmanager.sqlbuilder.TableId;

import java.io.*;
import java.util.*;

/**
 * Created by caols on 15-6-1.
 */
public class Main2 {

    private static final String IP = "120.132.59.94";
    private static final int PORT = 4040;
    private static final String SOURCE_SCHEMA = "ycarapi";
    private static final String SOURCE_TABLE_ORDER = "info_orders";
    private static final List<String> SOURCE_TABLE_ORDER_COLUMNS = Arrays.asList("id", "address_id", "car_id", "car_keeper_rating", "car_user_id", "complete_time", "coupon_id", "create_time", "customer_name", "customer_phone_number", "description", "evaluation", "give_back_keeper_id", "give_back_time", "last_modified", "number", "operator_id", "order_rating", "pick_time", "pick_time_segment", "price", "peer_source", "source_order_id", "start_time", "status", "supplier_id", "supplier_rating", "take_keeper_id", "take_time", "type", "current_keeper_id", "customer_gender", "service_type", "product_comment", "customer_evaluated", "disabled", "paid", "snapshot", "snapshot_time", "snapshot_version", "operator_comment", "paid_operator_id", "give_back_start_time", "pay_mode", "sale_source", "coupon_used", "sale_person_id");
    private static final String SOURCE_TABLE_SUPPLIERS = "data_suppliers";
    private static final List<String> SOURCE_TABLE_SUPPLIERS_COLUMNS = Arrays.asList("id", "address", "address_name", "car_keeper_id", "confirm_score", "contact_name", "create_time", "disabled", "last_modified", "latitude", "layoff", "layoff_end_time", "layoff_start_time", "longitude", "mobile_number", "name", "part_score", "phone_number", "price_score", "rating", "service_score", "time_score", "work_score", "evaluation");
    private static final String SOURCE_TABLE_INFO_CARS = "info_cars";
    private static final List<String> SOURCE_TABLE_INFO_CARS_COLUMNS = Arrays.asList("id", "boughtDate", "car_model_id", "car_user_id", "create_time", "disabled", "engine_number", "last_modified", "mileage", "number", "province", "vehicle_number");
    private static final String SOURCE_TABLE_CARS_BRAND = "data_car_brands";
    private static final List<String> SOURCE_TABLE_CAR_BRAND_COLUMNS = Arrays.asList("id", "create_time", "disabled", "first_letter", "is_popular", "last_modified", "logo_attachment_id", "logo_key", "name", "sort_key", "spell");
    private static final String SOURCE_TABLE_CARS_CATEGORY = "data_car_categoery";
    private static final List<String> SOURCE_TABLE_CAR_CATEGORY_COLUMNS = Arrays.asList("id", "brand_id", "create_time", "disabled", "last_modified", "name", "full");
    private static final String SOURCE_TABLE_CARS_MODEL = "data_car_models";
    private static final List<String> SOURCE_TABLE_CAR_MODEL_COLUMNS = Arrays.asList("id", "capacity", "category_id", "create_time", "disabled", "first_maintenance_mileage", "last_modified", "name", "oil_amount", "producer", "year", "full");
    private static final String SOURCE_TABLE_ADDRESS = "info_address";
    private static final List<String> SOURCE_TABLE_ADDRESS_COLUMNS = Arrays.asList("id", "address", "address_name", "create_time", "disabled", "last_modified", "latitude", "longitude", "user_id");
    private static final String SOURCE_TABLE_PAYMENT = "info_payments";
    private static final List<String> SOURCE_TABLE_PAYMENT_COLUMNS = Arrays.asList("id", "channel", "create_time", "description", "last_modified", "number", "order_id", "paid", "price", "source", "source_number", "status", "coupon_id");
    private static final String SOURCE_TABLE_USERS = "info_users";
    private static final List<String> SOURCE_TABLE_USERS_COLUMNS = Arrays.asList("id", "gender", "name");
    private static final String SOURCE_TABLE_ORDER_ITEMS = "info_order_items";
    private static final List<String> SOURCE_TABLE_ORDER_ITEMS_COLUMNS = Arrays.asList("id", "car_inspection_item_name", "comment", "complete_time", "create_time", "keeper_id", "labour_price", "last_modified", "name", "order_id", "product_id", "product_price", "quantity", "complete", "part_type", "pay_status", "payment_id", "selection_mode", "referee_keeper_id", "referee_operator_id", "required_pay");
    private static final String SOURCE_TABLE_PRODUCTS_CATEGORY = "data_products_categories";
    private static final List<String> SOURCE_TABLE_PRODUCTS_CATEGORY_COLUMNS = Arrays.asList("id", "name", "parent_id", "supplier_type_id");
    private static final String SOURCE_TABLE_PRODUCTS = "data_products";
    private static final List<String> SOURCE_TABLE_PRODUCTS_COLUMNS = Arrays.asList("id", "category_id", "name", "price");

    private static final Map<Long, Map<String, Object>> SUPPLIER_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> INFO_CAR_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> CAR_BRAND_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> CAR_CATEGORY_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> CAR_MODEL_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> ADDRESS_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> PAYMENT_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> USER_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> PRODUCTS_CATEGORIES_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> PRODUCTS_DICT = new HashMap<>();
    private static final Map<Long, List<Map<String, Object>>> ORDER_ITEM_DICT = new HashMap<>();

    private static final List<Long> BAOYANG = Arrays.asList(1l, 2l, 3l, 4l);
    private static final List<Long> JIANCE = Arrays.asList(238l, 249l, 272l, 350l);
    private static final List<Long> YANCHE = Arrays.asList(268l, 324l, 258l);
    private static final Map<Long, Integer> OTHER_THAN_BAOYANG = new HashMap<>();

    static {
        OTHER_THAN_BAOYANG.put(236l, 1);
        OTHER_THAN_BAOYANG.put(237l, 2);
        OTHER_THAN_BAOYANG.put(239l, 3);
    }

    public static void main(String[] args) throws IOException {
        DB db = DB.instance(() -> new DS(IP, PORT, "developer", "poiuy09876Ycar"));

        // 供应商表
        makeDict(db, SOURCE_TABLE_SUPPLIERS, SOURCE_TABLE_SUPPLIERS_COLUMNS, SUPPLIER_DICT);

        // 用户车辆表
        makeDict(db, SOURCE_TABLE_INFO_CARS, SOURCE_TABLE_INFO_CARS_COLUMNS, INFO_CAR_DICT);

        // 车相关
        makeDict(db, SOURCE_TABLE_CARS_BRAND, SOURCE_TABLE_CAR_BRAND_COLUMNS, CAR_BRAND_DICT);
        makeDict(db, SOURCE_TABLE_CARS_CATEGORY, SOURCE_TABLE_CAR_CATEGORY_COLUMNS, CAR_CATEGORY_DICT);
        makeDict(db, SOURCE_TABLE_CARS_MODEL, SOURCE_TABLE_CAR_MODEL_COLUMNS, CAR_MODEL_DICT);

        // 地点
        makeDict(db, SOURCE_TABLE_ADDRESS, SOURCE_TABLE_ADDRESS_COLUMNS, ADDRESS_DICT);

        // 用户
        makeDict(db, SOURCE_TABLE_USERS, SOURCE_TABLE_USERS_COLUMNS, USER_DICT);

        // 商品
        makeDict(db, SOURCE_TABLE_PRODUCTS, SOURCE_TABLE_PRODUCTS_COLUMNS, PRODUCTS_DICT);
        makeDict(db, SOURCE_TABLE_PRODUCTS_CATEGORY, SOURCE_TABLE_PRODUCTS_CATEGORY_COLUMNS, PRODUCTS_CATEGORIES_DICT);

        // 支付
        makeDict2(db, SOURCE_TABLE_PAYMENT, SOURCE_TABLE_PAYMENT_COLUMNS, PAYMENT_DICT);

        // 商品
        makeDict3(db, SOURCE_TABLE_ORDER_ITEMS, SOURCE_TABLE_ORDER_ITEMS_COLUMNS, ORDER_ITEM_DICT);

        // 订单表
        String sql = new Select(new TableId(SOURCE_SCHEMA, SOURCE_TABLE_ORDER), SOURCE_TABLE_ORDER_COLUMNS).toSql();

        if (args.length == 1) {
            sql += " where disabled = 0 and create_time >= '"+args[0]+"' ";
        } else if (args.length == 2) {
            sql += " where disabled = 0 and create_time >= '"+args[0]+"' and create_time <= '"+args[1]+"'";
        }

        RS rs = db.simpleQuery(sql);
//                + " where create_time >= '2015-6-1 00:00:00' and create_time <= '2015-6-16 23:59:59'");
//                + " where create_time >= '2015-6-1 00:00:00' ");
//                + " where create_time <= '" + args[0] + "'");
        List<List<Object>> printData = new ArrayList<>();
        while (rs.next()) {
            printData.add(getRow(rs));
        }

        File file = new File("data.csv");
        if (file.exists() || (!file.exists() && file.createNewFile())) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "gb2312"));
            bw.write("主键,编号,创建日期,创建时间,进度,渠道,接车日期,接车时间,还车日期,还车时间,车主姓名,性别,联系电话,车牌号,品牌车系车型,里程数,接车地点,还车地点,送修服务商名称,支付类型,支付金额,支付单号,当前管家,接车管家,还车管家,客服,销售名称,客服备注,商品备注,汽车保养,汽车美容,保险续险,管家代办定损,管家检测,代办验车,其它,商品详情");
            bw.newLine();
            for (List<Object> os : printData) {
                for (Object o : os) {
                    bw.write((null == o) ? "" : o.toString());
                    bw.write(",");
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();
            System.out.println("printed");
        } else {
            System.out.println("not printed");
        }
    }

    private static void makeDict3(DB db, String tableName, List<String> columnList, Map<Long, List<Map<String, Object>>> map) {
        RS rs = db.simpleQuery(new Select(new TableId(SOURCE_SCHEMA, tableName), columnList).toSql());

        while (rs.next()) {
            List<Map<String, Object>> order = map.get(rs.getLong("order_id"));
            if (null == order) {
                order = new ArrayList<>();
                map.put(rs.getLong("order_id"), order);
            }
            Map<String, Object> item = new HashMap<>();
            rs.forEach(item::put);
            order.add(item);
        }
    }

    private static void makeDict(DB db, String tableName, List<String> columnList, Map map) {
        RS rs = db.simpleQuery(new Select(new TableId(SOURCE_SCHEMA, tableName), columnList).toSql());

        while (rs.next()) {
            Map<String, Object> supplier = new HashMap<>();
            rs.forEach(supplier::put);
            map.put(rs.getLong("id"), supplier);
        }
    }

    private static void makeDict2(DB db, String tableName, List<String> columnList, Map map) {
        RS rs = db.simpleQuery(new Select(new TableId(SOURCE_SCHEMA, tableName), columnList).toSql());

        while (rs.next()) {
            Map<String, Object> supplier = new HashMap<>();
            rs.forEach(supplier::put);
            map.put(rs.getLong("order_id"), supplier);
        }
    }

    private static String escape(Object o) {
        if (Objects.isNull(o)) {
            return "";
        }
        String ret = o.toString().replaceAll("\\r", "");
        ret = ret.replaceAll("\\n", "");
        ret = ret.replaceAll("，", "-");
        ret = ret.replaceAll(",", "-");
        return ret.replaceAll("\\r\\n", "");
    }

    private static List<Object> getRow(RS rs) {
        List<Object> ret = new ArrayList<>();
        ret.add(escape(rs.getLong("id")));
        String number = rs.getString("number");
        number = number.substring(0, 4) + " " + number.substring(4);
        ret.add(escape(number));
        Object create_time_obj = rs.getObject("create_time");
        if (create_time_obj != null && !"".equals(create_time_obj)) {
            String[] create_time = create_time_obj.toString().split(" ");
            ret.add(escape(create_time[0]));
            if (create_time.length > 1) {
                ret.add(escape(create_time[1].substring(0, create_time[1].indexOf("."))));
            } else {
                ret.add("");
            }
        } else {
            ret.add("");
            ret.add("");
        }
        ret.add(escape(rs.getString("status").equalsIgnoreCase("complete") ? "已完成" : "未完成"));
        ret.add(escape(rs.getString("sale_source")));
        Object take_time_obj = rs.getObject("take_time");
        if (take_time_obj != null && !"".equals(take_time_obj)) {
            String[] take_time = take_time_obj.toString().split(" ");
            ret.add(escape(take_time[0]));
            if (take_time.length > 1) {
                ret.add(escape(take_time[1].substring(0, take_time[1].indexOf("."))));
            } else {
                ret.add("");
            }
        } else {
            ret.add("");
            ret.add("");
        }
        Object give_back_time_obj = rs.getObject("give_back_time");
        if (give_back_time_obj != null && !"".equals(give_back_time_obj)) {
            String[] give_back_time = give_back_time_obj.toString().split(" ");
            ret.add(escape(give_back_time[0]));
            if (give_back_time.length > 1) {
                ret.add(escape(give_back_time[1].substring(0, give_back_time[1].indexOf("."))));
            } else {
                ret.add("");
            }
        } else {
            ret.add("");
            ret.add("");
        }

        // 车主姓名－－
        ret.add(escape(rs.getString("customer_name")));
        // 性别
        ret.add(escape(rs.getString("customer_gender")));
        ret.add(escape(rs.getString("customer_phone_number")));

        // 车辆信息
        long car_id = rs.getLong("car_id");
        Map<String, Object> infoCar = INFO_CAR_DICT.get(car_id);
        ret.add(escape(infoCar.get("province").toString() + infoCar.get("number").toString()));
        long car_model_id = Long.parseLong(infoCar.get("car_model_id").toString());
        Map<String, Object> carModel = CAR_MODEL_DICT.get(car_model_id);
        long category_id = Long.parseLong(carModel.get("category_id").toString());
        Map<String, Object> carCatogery = CAR_CATEGORY_DICT.get(category_id);
        long brand_id = Long.parseLong(carCatogery.get("brand_id").toString());
        Map<String, Object> carBrand = CAR_BRAND_DICT.get(brand_id);
        ret.add(escape(String.format("%s %s %s", carBrand.get("name"), carCatogery.get("name"), carModel.get("name"))));
        ret.add(escape(infoCar.get("mileage")));

        // 地点
        long address_id = rs.getLong("address_id");
        Map<String, Object> address = ADDRESS_DICT.get(address_id);
        ret.add(escape(address.get("address")));
        ret.add(escape(address.get("address")));

        // 服务商
        long supplier_id = rs.getLong("supplier_id");
        if (0 != supplier_id) {
            Map<String, Object> supplier = SUPPLIER_DICT.get(supplier_id);
            ret.add(escape(supplier.get("name")));
        } else {
            ret.add("");
        }

        // 支付
        Map<String, Object> payment = PAYMENT_DICT.get(rs.getLong("id"));
        if (null != payment) {
            Object source_obj = payment.get("source");
            if (null != source_obj) {
                String source = source_obj.toString();
                if ("pingxx".equalsIgnoreCase(source)) {
                    ret.add("在线支付");
                } else {
                    ret.add("线下支付");
                }
            } else {
                ret.add("线下支付");
            }
            ret.add(escape(payment.get("price")));
            ret.add(escape(payment.get("source_number")));
        } else {
            ret.add("未知");
            ret.add("未知");
            ret.add("未知");
        }

        // 管家
        long current_keeper_id = rs.getLong("current_keeper_id");
        if (0 != current_keeper_id) {
            ret.add(escape(USER_DICT.get(current_keeper_id).get("name")));
        } else {
            ret.add("");
        }
        long take_keeper_id = rs.getLong("take_keeper_id");
        if (0 != take_keeper_id) {
            ret.add(escape(USER_DICT.get(take_keeper_id).get("name")));
        } else {
            ret.add("");
        }
        long give_back_keeper_id = rs.getLong("give_back_keeper_id");
        if (0 != give_back_keeper_id) {
            ret.add(escape(USER_DICT.get(give_back_keeper_id).get("name")));
        } else {
            ret.add("");
        }

        // 客服
        long operator_id = rs.getLong("operator_id");
        if (0 != operator_id) {
            ret.add(escape(USER_DICT.get(operator_id).get("name")));
        } else {
            ret.add("");
        }

        // 销售名称
        Map<String, Object> sale_person = USER_DICT.get(rs.getLong("sale_person_id"));
        if (null != sale_person) {
            ret.add(escape(sale_person.get("name")));
        } else {
            ret.add("");
        }

        // 客服备注
        ret.add(escape(rs.getString("operator_comment")));
        // 商品备注
        ret.add(escape(rs.getString("product_comment")));

        // 订单分类，0-6，汽车保养,汽车美容,保险续险,管家代办定损,管家检测,代办验车,其它
        int[] isThisCate = new int[7];

        List<Map<String, Object>> orderItems = ORDER_ITEM_DICT.get(rs.getLong("id"));
        if (null != orderItems) {
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> item : orderItems) {
                long product_id = Long.parseLong(item.get("product_id").toString());
                Map<String, Object> product = PRODUCTS_DICT.get(product_id);
                long product_category_id = Long.parseLong(product.get("category_id").toString());
                Map<String, Object> productCategory = PRODUCTS_CATEGORIES_DICT.get(product_category_id);
                sb.append(productCategory.get("name")).append(" ").append(product.get("name")).append(" x ").append(item.get("quantity")).append("||");

                if (BAOYANG.contains(product_category_id)) {
                    isThisCate[0] = 1;
                } else if (OTHER_THAN_BAOYANG.containsKey(product_id)) {
                    isThisCate[OTHER_THAN_BAOYANG.get(product_id)] = 1;
                } else if (JIANCE.contains(product_id)) {
                    isThisCate[4] = 1;
                } else if (YANCHE.contains(product_id)) {
                    isThisCate[5] = 1;
                } else {
                    isThisCate[6] = 1;
                }
            }
            for (int anIsThisCate : isThisCate) {
                if (anIsThisCate == 1) {
                    ret.add(escape(anIsThisCate));
                } else {
                    ret.add(escape(""));
                }
            }
            ret.add(escape(sb.toString()));
        } else {
            for (int anIsThisCate : isThisCate) {
                if (anIsThisCate == 1) {
                    ret.add(escape(anIsThisCate));
                } else {
                    ret.add(escape(""));
                }
            }
            ret.add("无订单商品数据");
        }

        return ret;
    }

}
