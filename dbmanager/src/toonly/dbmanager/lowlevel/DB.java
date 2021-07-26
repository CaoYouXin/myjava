/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toonly.dbmanager.lowlevel;

import org.slf4j.LoggerFactory;
import toonly.debugger.Debugger;
import toonly.wrapper.SW;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static toonly.configer.FileTool.LINE_SEPARATOR;

/**
 * @author CPU
 *         <p>
 *         关系数据库操作类：
 * @* 表结构查询：目前仅支持show databases;和show tables in xxx;
 * @1 普通查询：参数必须是不含占位符的字符串
 * @2 带占位符的查询：参数可以是String(sql)，Object...
 * @3 普通增删改：参数同样必须是String(sql)
 * @4 带占位符的增删改：参数可以是String(sql)，Object...
 * @** 参数中带conn的：conn在外部可能设置自动提交模式，所以返回结果是个问题，需要测试测试。。。
 * @注 Object...需要通过instanceof检测，代换成合适的字符串
 * @另注 Object的类型除包括char之外的七大类型外，还对java.util.Date类型做了特殊处理， 替换成了long，其余各种类型，都调用toString方法
 */
public final class DB {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DB.class);

    //Begin 单例模式
    private static final DB INSTANCE = new DB();
    //Begin 常量定义
    private static final String LABEL_DATABASE = "Database";
    private static final String LABEL_TABLE = "Tables_in_%s";
    //End 单例模式
    private static final String SHOW_TABLES = "SHOW TABLES IN `%s`;";
    private static final String BATCH_DATA_ARE_NOT_SUPPORTED = "Batch Data are not supported.";
    private DS ds;

    private DB() {
//        this.ds = new DS();
//        LOGGER.info("data source:{}{}", LINE_SEPARATOR, this.ds.toString());
    }
    //End 常量定义

    public static DB instance() {
        return INSTANCE;
    }

    public static DB instance(DSConstructor sdc) {
        DS ds = sdc.construct();
        INSTANCE.ds = (null != ds) ? ds : INSTANCE.ds;
        LOGGER.info("data source:{}{}", LINE_SEPARATOR, INSTANCE.ds.toString());
        return INSTANCE;
    }

    private static boolean asExpected(boolean isInsert, int ret, int expected, boolean isDrop) {
        LOGGER.info("isInsert : {}; ret : {}; expected : {}; isDrop : {}.", isInsert, ret, expected, isDrop);
        if (isDrop) {
            return true;
        }
        return isInsert ? asExpectedWhenInsert(ret, expected) : expected == ret;
    }

    private static boolean asExpected2(boolean isInsert, int ret, int expected, boolean isDrop) {
        Debugger.debugRun(DB.class, () ->
                LOGGER.info("isInsert : {}; ret : {}; expected : {}; isDrop : {}.", isInsert, ret, expected, isDrop));
        if (isDrop) {
            return true;
        }
        return isInsert ? asExpectedWhenInsert(ret, expected) : expected == ret;
    }

    private static boolean asExpectedWhenInsert(int ret, int expected) {
        return ret >= expected && ret <= (2 * expected);
    }

    public void close() {
        this.ds.close();
    }

    private void debug(String sql, List<Object> params) {
        if (null != params && !params.isEmpty()) {
            LOGGER.info("{}SQL wa [{}]{}\tParams wa {}", LINE_SEPARATOR, sql, LINE_SEPARATOR, params);
        } else {
            LOGGER.info("{}SQL wa [{}]", LINE_SEPARATOR, sql);
        }
    }

    private void debug2(String sql, List<Object> params) {
        Debugger.debugRun(this, () -> {
            if (null != params && !params.isEmpty()) {
                LOGGER.info("{}SQL wa [{}]{}\tParams wa {}", LINE_SEPARATOR, sql, LINE_SEPARATOR, params);
            } else {
                LOGGER.info("{}SQL wa [{}]", LINE_SEPARATOR, sql);
            }
        });
    }

    private void log(SQLException ex) {
        LOGGER.info("msg : {}", ex.getLocalizedMessage());
        String locationString = null;
        for (StackTraceElement e : ex.getStackTrace()) {
            locationString = e.toString();
            if (locationString.contains(DB.class.getName())) {
                break;
            }
        }
        LOGGER.info("location in DB : {}", locationString);
    }

    public RS simpleQuery2(Connection conn, String sql, List<String> coloums) throws SQLException {
        this.debug(sql, null);

        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery(sql);
        return new RS(rs, coloums.toArray(new String[0]));
    }

    public RS simpleQuery(Connection conn, String sql) throws SQLException {
        this.debug(sql, null);

        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery(sql);
        return new RS(rs, parseLabels(sql));
    }

    public RS simpleQuery2(String sql, List<String> coloums) {
        try (Connection conn = this.ds.getConnection()) {
            return this.simpleQuery2(conn, sql, coloums);
        } catch (SQLException ex) {
            this.log(ex);
            return new RS();
        }
    }

    public RS simpleQuery(String sql) {
        try (Connection conn = this.ds.getConnection()) {
            return this.simpleQuery(conn, sql);
        } catch (SQLException ex) {
            this.log(ex);
            return new RS();
        }
    }

    public RS preparedQuery(Connection conn, String sql, List<Object> params) {
        this.debug(sql, params);

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            parsePlaceholders(stat, params);
            try (ResultSet rs = stat.executeQuery()) {
                return new RS(rs, parseLabels(sql));
            }
        } catch (SQLException ex) {
            this.log(ex);
            return new RS();
        }
    }

    public RS preparedQuery(String sql, List<Object> params) {
        try (Connection conn = this.ds.getConnection()) {
            return this.preparedQuery(conn, sql, params);
        } catch (SQLException ex) {
            this.log(ex);
            return new RS();
        }
    }

    public RS preparedQuery(Connection conn, String sql, Object... params) {
        return this.preparedQuery(conn, sql, Arrays.asList(params));
    }

    public RS preparedQuery(String sql, Object... params) {
        return this.preparedQuery(sql, Arrays.asList(params));
    }

    private String[] parseLabels(String sql) {
        String upperSql = sql.toUpperCase();
        String select = "SELECT";
        String from = "FROM";
        String labelSql = sql.substring(upperSql.indexOf(select) + select.length(), upperSql.indexOf(from));
        List<String> labels = new ArrayList<>();
        this.readCharByChar(labelSql.toCharArray(), labels);
        return labels.toArray(new String[labels.size()]);
    }

    private void readCharByChar(char[] chars, List<String> labels) {
        SW<ParseStatus> sw = new SW<>(ParseStatus.READY);
        StringBuilder sb = new StringBuilder("");
        for (char c : chars) {
            switch (c) {
                case '`':
                    this.readWrapWord(sw, sb, labels);
                    break;
                default:
                    this.readNormalWord(sw, c, sb);
            }
        }
    }

    private void readNormalWord(SW<ParseStatus> sw, char c, StringBuilder sb) {
        switch (sw.val()) {
            case START_A_NAME:
                sb.append(c);
                break;
            case READY_FOR_NAME:
                if ('a' == c || 'A' == c) {
                    sw.val(ParseStatus.START_AS);
                }
                if (',' == c) {
                    sw.val(ParseStatus.READY);
                }
                break;
            case START_A_ALIAS:
                sb.append(c);
                break;
            default:
                if (' ' == c) {
                    break;
                }
                throw new ParseLabelException(sw, c, "readNormalWord");
        }
    }

    private void readWrapWord(SW<ParseStatus> sw, StringBuilder sb, List<String> labels) {
        switch (sw.val()) {
            case READY:
                sw.val(ParseStatus.START_A_NAME);
                break;
            case START_A_NAME:
                labels.add(sb.toString());
                sb.delete(0, sb.length());
                sw.val(ParseStatus.READY_FOR_NAME);
                break;
            case START_AS:
                sw.val(ParseStatus.START_A_ALIAS);
                break;
            case START_A_ALIAS:
                labels.remove(labels.size() - 1);
                labels.add(sb.toString());
                sb.delete(0, sb.length());
                sw.val(ParseStatus.READY_FOR_NAME);
                break;
            default:
                throw new ParseLabelException(sw, '`', "readWrapWord");
        }
    }

    public List<String> showTables(String schemaName) {
        String sql = this.getShowTables(schemaName);
        this.debug(sql, null);

        try (Connection conn = this.ds.getConnection();
             Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(sql)) {
            List<String> ret = new ArrayList<>();
            while (rs.next()) {
                ret.add(rs.getString(this.getTableLabel(schemaName)));
            }
            return ret;
        } catch (SQLException ex) {
            this.log(ex);
            return new ArrayList<>();
        }
    }

    private String getTableLabel(String schemaName) {
        return String.format(DB.LABEL_TABLE, schemaName);
    }

    private String getShowTables(String schemaName) {
        return String.format(DB.SHOW_TABLES, schemaName);
    }

    public List<String> showDatabases() {
        String sql = "SHOW DATABASES;";
        this.debug(sql, null);

        try (Connection conn = this.ds.getConnection();
             Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(sql)) {
            List<String> ret = new ArrayList<>();
            while (rs.next()) {
                ret.add(rs.getString(LABEL_DATABASE));
            }
            return ret;
        } catch (SQLException ex) {
            this.log(ex);
            return new ArrayList<>();
        }
    }

    public boolean simpleExecute(String sql, int expected) {
        try (Connection conn = this.ds.getConnection()) {
            return this.simpleExecute(conn, sql, expected);
        } catch (SQLException ex) {
            this.log(ex);
            return false;
        }
    }

    public boolean simpleExecute(String sql) {
        try (Connection conn = this.ds.getConnection()) {
            return this.simpleExecute(conn, sql);
        } catch (SQLException ex) {
            this.log(ex);
            return false;
        }
    }

    public boolean simpleExecute(Connection conn, String sql, int expected) throws SQLException {
        this.debug(sql, null);

        boolean isInsert = isInsert(sql);
        boolean isDrop = isDrop(sql);
        try (Statement stat = conn.createStatement()) {
            int ret = stat.executeUpdate(sql);
            return asExpected(isInsert, ret, expected, isDrop);
        }
    }

    public boolean simpleExecute(Connection conn, String sql) throws SQLException {
        this.debug(sql, null);

        try (Statement stat = conn.createStatement()) {
            stat.executeUpdate(sql);
            return true;
        }
    }

    public boolean preparedExecute(String sql, int expected, List<Object> params) {
        return this.preparedExecute(sql, expected, params.toArray());
    }

    public boolean preparedExecute(String sql, int expected, Object... params) {
        try (Connection conn = this.ds.getConnection()) {
            return this.preparedExecute(conn, sql, expected, params);
        } catch (SQLException ex) {
            this.log(ex);
            return false;
        }
    }

    public boolean preparedExecute(Connection conn, String sql, int expected, List<Object> params) throws SQLException {
        return this.preparedExecute(conn, sql, expected, params.toArray());
    }

    public boolean preparedExecute(Connection conn, String sql, int expected, Object... params) throws SQLException {
        this.debug(sql, Arrays.asList(params));

        boolean isInsert = isInsert(sql);
        boolean isDrop = isDrop(sql);
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            parsePlaceholders(stat, Arrays.asList(params));
            int ret = stat.executeUpdate();
            return asExpected(isInsert, ret, expected, isDrop);
        }
    }

    /**
     * 批处理需要回滚
     *
     * @param sql
     * @param expected
     * @param params
     * @return
     */
    public boolean batchExecute(String sql, int expected, List<Object[]> params) {
        try (Connection conn = this.ds.getConnection()) {
            return this.batchExecute(conn, sql, expected, params);
        } catch (SQLException ex) {
            this.log(ex);
            return false;
        }
    }

    /**
     * 批处理需要回滚
     *
     * @param sql
     * @param expected
     * @param batch
     * @return
     */
    public boolean batchExecute(String sql, int expected, Batch batch) {
        try (Connection conn = this.ds.getConnection()) {
            return this.batchExecute(conn, sql, expected, batch);
        } catch (SQLException ex) {
            this.log(ex);
            return false;
        }
    }

    /**
     * 回滚和提交都交给外部处理
     *
     * @param conn
     * @param sql
     * @param expected
     * @param params
     * @return
     * @throws java.sql.SQLException
     */
    public boolean batchExecute(Connection conn, String sql, int expected, List<Object[]> params) throws SQLException {
        this.debug(sql, Arrays.asList(BATCH_DATA_ARE_NOT_SUPPORTED));

        boolean isInsert = isInsert(sql);
        boolean isDrop = isDrop(sql);
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            for (Object[] objects : params) {
                parsePlaceholders(stat, Arrays.asList(objects));
                stat.addBatch();
            }
            int[] counts = stat.executeBatch();
            int ret = 0;
            for (int count : counts) {
                ret += count;
            }
            return asExpected(isInsert, ret, expected, isDrop);
        }
    }

    /**
     * 回滚和提交都交给外部处理
     *
     * @param conn
     * @param sql
     * @param expected
     * @param batch
     * @return
     * @throws java.sql.SQLException
     */
    public boolean batchExecute(Connection conn, String sql, int expected, Batch batch) throws SQLException {
        this.debug(sql, Arrays.asList(BATCH_DATA_ARE_NOT_SUPPORTED));

        boolean isInsert = isInsert(sql);
        boolean isDrop = isDrop(sql);
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            for (int i = 0; i < expected; i++) {
                Object[] objects = batch.row(i);
                parsePlaceholders(stat, Arrays.asList(objects));
                stat.addBatch();
            }
            int[] counts = stat.executeBatch();
            int ret = 0;
            for (int count : counts) {
                ret += count;
            }
            return asExpected(isInsert, ret, expected, isDrop);
        }
    }

    public boolean transaction(Trans trans) {
        try (Connection conn = this.ds.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            trans.trans(conn);
            conn.commit();
            conn.setAutoCommit(autoCommit);
            return true;
        } catch (SQLException ex) {
            this.log(ex);
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isInsert(String sql) {
        return sql.trim().toUpperCase().startsWith("INSERT");
    }

    private boolean isDrop(String sql) {
        return sql.trim().toUpperCase().startsWith("DROP");
    }

    private void parsePlaceholders(PreparedStatement stat, List<Object> params) throws SQLException {
        int index = 1;
        for (Object obj : params) {
            if (obj instanceof Integer) {
                stat.setInt(index++, (int) obj);
            } else if (obj instanceof Boolean) {
                stat.setBoolean(index++, (boolean) obj);
            } else if (obj instanceof Long) {
                stat.setLong(index++, (long) obj);
            } else if (obj instanceof Date) {
                stat.setTimestamp(index++, new Timestamp(((Date) obj).getTime()));
            } else if (obj instanceof String) {
                stat.setString(index++, new String(((String) obj).getBytes(), Charset.forName("UTF-8")));
            } else if (obj instanceof Double) {
                stat.setDouble(index++, (double) obj);
            } else {
                stat.setString(index++, obj.toString());
            }
        }
    }

    public boolean createDatabase(String schemaName) {
        return this.simpleExecute(String.format("CREATE DATABASE `%s` DEFAULT CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';"
                , schemaName));
    }

    public boolean dropDatabase(String schemaName) {
        return this.simpleExecute(String.format("DROP DATABASE IF EXISTS `%s`", schemaName));
    }

    private enum ParseStatus {
        READY, START_A_NAME, READY_FOR_NAME, START_AS, START_A_ALIAS
    }

    private static class ParseLabelException extends RuntimeException {
        public ParseLabelException(SW<ParseStatus> sw, char c, String when) {
            super(String.format("state : %s, char : %c. when %s", sw.val().name(), c, when));
        }
    }
}
