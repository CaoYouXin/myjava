package yangaiche;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.lowlevel.DS;
import toonly.dbmanager.lowlevel.RS;
import toonly.dbmanager.sqlbuilder.Insert;
import toonly.dbmanager.sqlbuilder.Select;
import toonly.dbmanager.sqlbuilder.TableId;

import java.util.*;

/**
 * Created by caols on 15-6-2.
 */
public class TransUserCars {
    private static final String IP = "120.132.59.94";
    private static final int PORT = 4040;
    private static final String SCHEMA_SOURCE = "ycar141204";
    private static final String TABLE_TM = "tm";
    private static final List<String> TABLE_TM_COLUMNS = Arrays.asList("id", "l", "e", "s", "f", "m", "b", "p");
    private static final String TABLE_OLD_CAR_USERS = "info_car_users";
    private static final List<String> TABLE_OLD_CAR_USERS_COLUMNS = Arrays.asList("id", "phone_number");
    private static final String TABLE_OLD_INFO_CAR = "info_cars";
    private static final List<String> TABLE_OLD_INFO_CAR_COLUMNS = Arrays.asList("id", "car_model_id", "car_number", "create_time", "description", "disabled", "engine_number", "last_modified", "mileage", "owner_id", "buy_date", "vehicle_number");

    private static final String SCHEMA_TARGET = "ycarapi_ol_same";
//    private static final String SCHEMA = "ycarapi";
    private static final String TABLE_INFO_CARS = "info_cars";
    private static final List<String> TABLE_INFO_CARS_COLUMNS = Arrays.asList("boughtDate", "car_model_id", "car_user_id", "create_time", "disabled", "engine_number", "last_modified", "mileage", "number", "province", "vehicle_number");
    private static final String TABLE_CARS_BRAND = "data_car_brands";
    private static final List<String> TABLE_CAR_BRAND_COLUMNS = Arrays.asList("id", "create_time", "disabled", "first_letter", "is_popular", "last_modified", "logo_attachment_id", "logo_key", "name", "sort_key", "spell");
    private static final String TABLE_CARS_CATEGORY = "data_car_categoery";
    private static final List<String> TABLE_CAR_CATEGORY_COLUMNS = Arrays.asList("id", "brand_id", "create_time", "disabled", "last_modified", "name", "full");
    private static final String TABLE_CARS_MODEL = "data_car_models";
    private static final List<String> TABLE_CAR_MODEL_COLUMNS = Arrays.asList("id", "capacity", "category_id", "create_time", "disabled", "first_maintenance_mileage", "last_modified", "name", "oil_amount", "producer", "year", "full");
    private static final String TABLE_USERS = "info_users";
    private static final List<String> TABLE_USERS_COLUMNS = Arrays.asList("id", "phone_number");

    private static final Map<Long, Map<String, Object>> OLD_CARS_DICT = new HashMap<>();
    private static final Map<Long, Map<String, Object>> OLD_USERS_DICT = new HashMap<>();


    private static final Map<String, Map<String, Object>> CAR_BRAND_DICT = new HashMap<>();
    private static final Map<Long, List<Map<String, Object>>> CAR_CATEGORY_DICT = new HashMap<>();
    private static final Map<Long, List<Map<String, Object>>> CAR_MODEL_DICT = new HashMap<>();
    private static final Map<String, Map<String, Object>> USER_DICT = new HashMap<>();

    public static void main(String[] args) {

//        DB db = DB.instance(() -> new DS(IP, PORT, args[0], args[1]));
        DB db = DB.instance(() -> new DS(IP, PORT, "developer", "poiuy09876Ycar"));

        // 查旧库表
        makeDict(db, SCHEMA_SOURCE, TABLE_TM, TABLE_TM_COLUMNS, OLD_CARS_DICT, "id");
        makeDict(db, SCHEMA_SOURCE, TABLE_OLD_CAR_USERS, TABLE_OLD_CAR_USERS_COLUMNS, OLD_USERS_DICT, "id");

        // 新库
        makeDict3(db, SCHEMA_TARGET, TABLE_USERS, TABLE_USERS_COLUMNS, USER_DICT, "phone_number");
        makeDict3(db, SCHEMA_TARGET, TABLE_CARS_BRAND, TABLE_CAR_BRAND_COLUMNS, CAR_BRAND_DICT, "name");
        makeDict2(db, SCHEMA_TARGET, TABLE_CARS_CATEGORY, TABLE_CAR_CATEGORY_COLUMNS, CAR_CATEGORY_DICT, "brand_id");
        makeDict2(db, SCHEMA_TARGET, TABLE_CARS_MODEL, TABLE_CAR_MODEL_COLUMNS, CAR_MODEL_DICT, "category_id");

        RS rs = db.simpleQuery(new Select(new TableId(SCHEMA_SOURCE, TABLE_OLD_INFO_CAR), TABLE_OLD_INFO_CAR_COLUMNS).toSql());

        db.transaction((conn) -> {
            while (rs.next()) {
                Object[] os = new Object[TABLE_INFO_CARS_COLUMNS.size()];
                os[0] = getBoughtDate(rs.getObject("buy_date"), rs.getObject("create_time"));
                os[1] = getCarModelId(rs.getLong("car_model_id"));
                os[2] = getCarUserId(rs.getLong("owner_id"));
                os[3] = rs.getObject("create_time");
                os[4] = rs.getObject("disabled");
                os[5] = rs.getObject("engine_number");
                os[6] = rs.getObject("last_modified");
                os[7] = getMileage(rs.getObject("mileage"));
                String car_number = rs.getString("car_number");
                if (car_number.length() >= 1) {
                    os[8] = car_number.substring(1);
                    os[9] = car_number.substring(0, 1);
                } else {
                    os[8] = null;
                    os[9] = null;
                }
                os[10] = rs.getObject("vehicle_number");

                List<String> thisFields = new ArrayList<>();
                List<Object> params = new ArrayList<>();
                for (int i = 0; i<os.length; i++) {
                    Object o = os[i];
                    if (null != o) {
                        params.add(o);
                        thisFields.add(TABLE_INFO_CARS_COLUMNS.get(i));
                    }
                }
                db.preparedExecute(conn, new Insert(new TableId(SCHEMA_TARGET, TABLE_INFO_CARS), thisFields).toPreparedSql(), 1, params);
            }
            conn.commit();
        });

    }

    private static Object getMileage(Object mileage) {
        if (Objects.isNull(mileage)) {
            return 0;
        }
        try {
            return Integer.parseInt(mileage.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private static Object getCarUserId(long owner_id) {
        Map<String, Object> oldUser = OLD_USERS_DICT.get(owner_id);
        if (Objects.isNull(oldUser)) {
            throw new RuntimeException("owner not found" + owner_id);
        }
        Object phone_number = oldUser.get("phone_number");
        if (Objects.isNull(phone_number)) {
            throw new RuntimeException("phone number not found" + owner_id);
        }
        return USER_DICT.get(phone_number);
    }

    private static Object getCarModelId(long car_model_id) {
        Map<String, Object> oldCar = OLD_CARS_DICT.get(car_model_id);
        Map<String, Object> newBrand = CAR_BRAND_DICT.get(oldCar.get("b"));
        List<Map<String, Object>> newCategories = CAR_CATEGORY_DICT.get(newBrand.get("id"));
        Long newCategoryId = getNewCategoryId(oldCar.get("s"), newCategories);
        if (Objects.isNull(newCategoryId)) {
            throw new RuntimeException("car category id not found in new database.");
        }
        List<Map<String, Object>> newCarModels = CAR_MODEL_DICT.get(newCategoryId);
        return getNewCarModelId(oldCar.get("e"), oldCar.get("m"), newCarModels);
    }

    private static Object getNewCarModelId(Object producer, Object model, List<Map<String, Object>> newCarModels) {
        for (Map<String, Object> car : newCarModels) {
            if (Objects.isNull(producer)) {

            } else {

            }
        }
        throw new RuntimeException("car model id not found in new database.");
    }

    private static Long getNewCategoryId(Object s, List<Map<String, Object>> newCategories) {
        for (Map<String, Object> map : newCategories) {
            if (s.equals(map.get("name"))) {
                return (Long) map.get("id");
            }
        }
        return null;
    }

    private static Object getBoughtDate(Object buy_date, Object create_time) {
        if (Objects.isNull(buy_date)) {
            return create_time;
        }
        return buy_date;
    }

    private static void makeDict(DB db, String schema, String tableName, List<String> columnList, Map map, String key) {
        RS rs = db.simpleQuery(new Select(new TableId(schema, tableName), columnList).toSql());

        while (rs.next()) {
            Map<String, Object> supplier = new HashMap<>();
            rs.forEach(supplier::put);
            map.put(rs.getLong(key), supplier);
        }
    }

    private static void makeDict3(DB db, String schema, String tableName, List<String> columnList, Map map, String key) {
        RS rs = db.simpleQuery(new Select(new TableId(schema, tableName), columnList).toSql());

        while (rs.next()) {
            Map<String, Object> supplier = new HashMap<>();
            rs.forEach(supplier::put);
            map.put(rs.getString(key), supplier);
        }
    }

    private static void makeDict2(DB db, String schema, String tableName, List<String> columnList, Map map, String key) {
        RS rs = db.simpleQuery(new Select(new TableId(schema, tableName), columnList).toSql());

        while (rs.next()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(rs.getLong(key));
            if (Objects.isNull(list)) {
                list = new ArrayList<>();
                map.put(rs.getLong(key), list);
            }
            Map<String, Object> obj = new HashMap<>();
            rs.forEach(obj::put);
            list.add(obj);
        }
    }

}
