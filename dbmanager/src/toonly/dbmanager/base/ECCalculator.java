package toonly.dbmanager.base;

import com.sun.istack.internal.NotNull;
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

    private final Map<String, Object> _data;
    private final Map<String, DT> _dataType;
    private final Entity _source;
    private final List<String> _duplicatedFs;
    private final List<String> _keyFs;
    private final List<String> _notNullFs;

    private boolean _isScaned = false;

    public ECCalculator(Entity aEntity) {
        this._source = aEntity;
        this._data = new HashMap<>();
        this._duplicatedFs = new ArrayList<>();
        this._keyFs = new ArrayList<>();
        this._notNullFs = new ArrayList<>();
        this._dataType = new HashMap<>();
    }

    private void scan() {
        if (this._isScaned) return;
        try {
            scanEC(this._source.getClass());
            this._isScaned = true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void scanEC(Class<? extends Entity> aClass) throws IllegalAccessException {
        if (Entity.class.equals(aClass))
            return;

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Column declaredAnnotation = declaredField.getDeclaredAnnotation(Column.class);
            if (null == declaredAnnotation)
                continue;
            boolean accessible = declaredField.isAccessible();
            declaredField.setAccessible(true);
            Object value = declaredField.get(this._source);
            this._data.put(declaredField.getName(), value);
            if (null == value) {
                this._notNullFs.add(declaredField.getName());
            }
            declaredField.setAccessible(accessible);

            DuplicatedColumn duplicatedColumn = declaredField.getDeclaredAnnotation(DuplicatedColumn.class);
            if (null != duplicatedColumn) {
                this._duplicatedFs.add(declaredField.getName());
            }

            KeyColumn keyColumn = declaredField.getDeclaredAnnotation(KeyColumn.class);
            if (null != keyColumn) {
                this._keyFs.add(declaredField.getName());
            }

            DT type = declaredField.getDeclaredAnnotation(DT.class);
            if (null == type) {
                throw new RuntimeException("no db type defined");
            }
            this._dataType.put(declaredField.getName(), type);
        }

        Class<?> superclass = aClass.getSuperclass();
        if (null != superclass) {
            scanEC((Class<? extends Entity>) superclass);
        }
    }

    public void dtForEach(@NotNull BiConsumer<String, DT> biConsumer) {
        scan();
        this._dataType.forEach(biConsumer);
    }

    public void dataForEach(@NotNull BiConsumer<String, Object> biConsumer) {
        scan();
        this._data.forEach(biConsumer);
    }

    public List<String> getFields() {
        scan();
        return new ArrayList<>(this._data.keySet());
    }

    public List<String> getDuplicatedFields() {
        scan();
        return this._duplicatedFs;
    }

    public List<String> getKeyFields() {
        scan();
        return this._keyFs;
    }

    public List<String> getNotNullFields() {
        scan();
        return this._notNullFs;
    }

    public List<Object> getValues() {
        scan();
        return new ArrayList<>(this._data.values());
    }

    public List<String> getFieldsExclude(List<String> keyColumns) {
        scan();
        ArrayList<String> columns = new ArrayList<>(this._data.keySet());
        keyColumns.forEach((key) -> {
            if (columns.contains(key)) {
                columns.remove(key);
            }
        });
        return columns;
    }

    public List<Object> getValuesInOrder(List<String> columns) {
        scan();
        List<Object> values = new ArrayList<>();
        columns.forEach((c) -> values.add(this._data.get(c)));
        return values;
    }

    public boolean setValue(String fieldName, Object value) {
        try {
            return this.setValue(this._source.getClass(), fieldName, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean setValue(Class<? extends Entity> aClass, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        if (Entity.class.equals(aClass))
            return false;

        Field declaredField = aClass.getDeclaredField(fieldName);
        if (null == declaredField) {
            Class<?> superclass = aClass.getSuperclass();
            if (null != superclass) {
                return setValue((Class<? extends Entity>) superclass, fieldName, value);
            }
        }

        boolean accessible = declaredField.isAccessible();
        declaredField.setAccessible(true);
        declaredField.set(this._source, value);
        declaredField.setAccessible(accessible);
        return true;
    }
}
