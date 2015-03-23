package toonly.dbmanager.base;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.dbmanager.lowlevel.DT;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by caoyouxin on 15-2-12.
 */
public class ECCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ECCalculator.class);

    private final Map<String, Object> data;
    private final Map<String, DT> dataType;
    private final Entity source;
    private final List<String> duplicatedFs;
    private final List<String> keyFs;
    private final List<String> notNullFs;

    private boolean isScaned = false;

    public ECCalculator(Entity aEntity) {
        this.source = aEntity;
        this.data = new HashMap<>();
        this.duplicatedFs = new ArrayList<>();
        this.keyFs = new ArrayList<>();
        this.notNullFs = new ArrayList<>();
        this.dataType = new HashMap<>();
    }

    private void scan() {
        if (this.isScaned) {
            return;
        }

        try {
            scanEC(this.source.getClass());
            this.isScaned = true;
        } catch (IllegalAccessException e) {
            LOGGER.info("entity class scan failed.");
        }
    }

    private void scanEC(Class<? extends Entity> aClass) throws IllegalAccessException {
        if (Entity.class.equals(aClass)) {
            return;
        }

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (this.readColumn(declaredField)) {
                continue;
            }

            this.readAssisted(declaredField);
        }

        Class<?> superclass = aClass.getSuperclass();
        if (null != superclass) {
            scanEC((Class<? extends Entity>) superclass);
        }
    }

    private boolean readColumn(Field declaredField) throws IllegalAccessException {
        Column declaredAnnotation = declaredField.getDeclaredAnnotation(Column.class);
        if (null == declaredAnnotation)
            return true;
        boolean accessible = declaredField.isAccessible();
        declaredField.setAccessible(true);
        Object value = declaredField.get(this.source);
        declaredField.setAccessible(accessible);
        this.data.put(declaredField.getName(), value);
        if (null != value) {
            this.notNullFs.add(declaredField.getName());
        }
        return false;
    }

    private void readAssisted(Field declaredField) {
        DuplicatedColumn duplicatedColumn = declaredField.getDeclaredAnnotation(DuplicatedColumn.class);
        if (null != duplicatedColumn) {
            this.duplicatedFs.add(declaredField.getName());
        }

        KeyColumn keyColumn = declaredField.getDeclaredAnnotation(KeyColumn.class);
        if (null != keyColumn) {
            this.keyFs.add(declaredField.getName());
        }

        DT type = declaredField.getDeclaredAnnotation(DT.class);
        if (null == type) {
            throw new RuntimeException("no db type defined");
        }
        this.dataType.put(declaredField.getName(), type);
    }

    public void dtForEach(@NotNull BiConsumer<String, DT> biConsumer) {
        scan();
        this.dataType.forEach(biConsumer);
    }

    public void dataForEach(@NotNull BiConsumer<String, Object> biConsumer) {
        scan();
        this.data.forEach(biConsumer);
    }

    public List<String> getFields() {
        scan();
        return new ArrayList<>(this.data.keySet());
    }

    public List<String> getDuplicatedFields() {
        scan();
        return this.duplicatedFs;
    }

    public List<String> getKeyFields() {
        scan();
        return this.keyFs;
    }

    public List<String> getNotNullFields() {
        scan();
        return this.notNullFs;
    }

    public List<Object> getValues() {
        scan();
        return new ArrayList<>(this.data.values());
    }

    public List<String> getFieldsExclude(List<String> keyColumns) {
        scan();
        ArrayList<String> columns = new ArrayList<>(this.data.keySet());
        keyColumns.forEach(key -> {
            if (columns.contains(key)) {
                columns.remove(key);
            }
        });
        return columns;
    }

    public List<Object> getValuesInOrder(List<String> columns) {
        scan();
        List<Object> values = new ArrayList<>();
        columns.forEach(c -> values.add(this.data.get(c)));
        return values;
    }

    public boolean setValue(String fieldName, Object value) {
        try {
            return this.setValue(this.source.getClass(), fieldName, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.info("set value failed");
        }
        return false;
    }

    private boolean setValue(Class<? extends Entity> aClass, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        if (Entity.class.equals(aClass)) {
            return false;
        }

        Field declaredField = aClass.getDeclaredField(fieldName);
        if (null == declaredField) {
            Class<?> superclass = aClass.getSuperclass();
            if (null != superclass) {
                return setValue((Class<? extends Entity>) superclass, fieldName, value);
            }
        }

        boolean accessible = declaredField.isAccessible();
        declaredField.setAccessible(true);
        declaredField.set(this.source, value);
        declaredField.setAccessible(accessible);
        return true;
    }
}
