/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toonly.dbmanager.lowlevel;

import com.sun.istack.internal.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiConsumer;

/**
 *
 * @author CPU
 */
public class RS {

	private int cur;
	private List<Map<String, Object>> data;

	RS(ResultSet rs, String[] labels) throws SQLException {
		this();
		while (rs.next()) {
			Map<String, Object> row = new HashMap<>();
			for (String label : labels) {
				row.put(label, rs.getObject(label));
			}
			data.add(row);
		}
	}

	RS() {
		data = new ArrayList<>();
		cur = -1;
	}

	public boolean next() {
		return !isAfterLast() && ++cur < data.size();
	}

	private Object getObject(String columnLabel) {
		return data.get(cur).get(columnLabel);
	}

	public String getString(String columnLabel) {
		Object obj = getObject(columnLabel);
		if (!(obj instanceof String)) {
			throw new RuntimeException("[" + columnLabel + "] is not a string.");
		}
		return (String) obj;
	}

	public boolean getBoolean(String columnLabel) {
		Object obj = getObject(columnLabel);
		if (!(obj instanceof Boolean)) {
			throw new RuntimeException("[" + columnLabel + "] is not a boolean, but a "+ (null == obj ? "null" : obj.getClass().getName()) +".");
		}
		return (boolean) obj;
	}

	public byte getByte(String columnLabel) {
		Object obj = getObject(columnLabel);
		if (!(obj instanceof Byte)) {
			throw new RuntimeException("[" + columnLabel + "] is not a byte.");
		}
		return (byte) obj;
	}

	public short getShort(String columnLabel) {
		Object obj = getObject(columnLabel);
		if (!(obj instanceof Short)) {
			throw new RuntimeException("[" + columnLabel + "] is not a short.");
		}
		return (short) obj;
	}

	public int getInt(String columnLabel) {
		Object obj = getObject(columnLabel);
		if (!(obj instanceof Integer)) {
			throw new RuntimeException("[" + columnLabel + "] is not a int.");
		}
		return (int) obj;
	}

	public long getLong(String columnLabel) {
		Object obj = getObject(columnLabel);
		if (!(obj instanceof Long)) {
			throw new RuntimeException("[" + columnLabel + "] is not a long.");
		}
		return (long) obj;
	}

	public float getFloat(String columnLabel) {
		Object obj = getObject(columnLabel);
		if (!(obj instanceof Float)) {
			throw new RuntimeException("[" + columnLabel + "] is not a float.");
		}
		return (float) obj;
	}

	public double getDouble(String columnLabel) {
		Object obj = getObject(columnLabel);
		if (!(obj instanceof Double)) {
			throw new RuntimeException("[" + columnLabel + "] is not a double.");
		}
		return (double) obj;
	}

	public Date getDate(String columnLabel) {
//        Object obj = getObject(columnLabel);
//        if (!(obj instanceof Long)) {
//            throw new RuntimeException("[" + columnLabel + "] is not a long.");
//        }
//        return new Date((long) obj);
		Object obj = getObject(columnLabel);
		if (!(obj instanceof Timestamp)) {
			throw new RuntimeException("[" + columnLabel + "] is not a long.");
		}
		return new Date(((Timestamp) obj).getTime());
	}

    public void forEach(@NotNull BiConsumer<String, Object> biConsumer) {
        data.get(cur).forEach(biConsumer);
    }

	public boolean isBeforeFirst() {
		return -1 == cur;
	}

	public boolean isAfterLast() {
		return data.size() == cur;
	}

	public boolean isFirst() {
		return 0 == cur;
	}

	public boolean isLast() {
		int size = data.size();
		return size > 0 ? (size - 1) == cur : false;
	}

	public void beforeFirst() {
		cur = -1;
	}

	public void afterLast() {
		cur = data.size();
	}

	public boolean first() {
		if (data.isEmpty()) {
			return false;
		}
		cur = 0;
		return true;
	}

	public boolean last() {
		if (data.isEmpty()) {
			return false;
		}
		cur = data.size() - 1;
		return true;
	}

	public int getRow() {
		return data.size();
	}

	public boolean previous() {
		return !isBeforeFirst() && --cur >= 0;
	}

    public boolean isEmpty() {
        return 0 == this.getRow();
    }
}
