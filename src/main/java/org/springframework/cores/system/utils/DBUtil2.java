//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.springframework.cores.system.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil2 {
    private static String databaseType = "MySql";
    private static String driver = "com.mysql.jdbc.Driver";
    private static String databaseName = "jty";
    private static String url;
    private static String userName;
    private static String passwrod;
    private static String port;
    private static String ip;

    static {
        url = "jdbc:mysql://localhost:3306/" + databaseName;
        userName = "root";
        passwrod = "123456";
        port = "3306";
        ip = "127.0.0.1";
    }

    public DBUtil2(String dbName) {
        if (Constants.DATABASETYPE.equals("MySql")) {
            driver = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://" + Constants.IP + ":" + Constants.PORT + "/" + dbName + "?characterEncoding=utf8&tinyInt1isBit=false";
        }

        if (Constants.DATABASETYPE.equals("Oracle")) {
            driver = "oracle.jdbc.driver.OracleDriver";
            url = "jdbc:oracle:thin:@" + Constants.IP + ":" + Constants.PORT + ":" + dbName;
        }

        if (Constants.DATABASETYPE.equals("PostgreSQL")) {
            driver = "org.postgresql.Driver";
            url = "jdbc:postgresql://" + Constants.IP + ":" + Constants.PORT + "/" + dbName;
        }

        if (Constants.DATABASETYPE.equals("MSSQL")) {
            driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            url = "jdbc:sqlserver://" + Constants.IP + ":" + Constants.PORT + ";database=" + dbName;
        }

        userName = Constants.USERNAME;
        passwrod = Constants.PASSWROD;
    }

    public static final synchronized Connection getConnection() {
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, userName, passwrod);
            return conn;
        } catch (Exception var1) {
            return null;
        }
    }

    public static boolean testConnection(String databaseType2, String databaseName2, String ip2, String port2, String user2, String pass2) {
        try {
            String url2 = "";
            if (databaseType2.equals("MySql")) {
                Class.forName("com.mysql.jdbc.Driver");
                url2 = "jdbc:mysql://" + ip2 + ":" + port2 + "/" + databaseName2 + "?characterEncoding=utf8";
            }

            if (databaseType2.equals("Oracle")) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                url2 = "jdbc:oracle:thin:@" + ip2 + ":" + port2 + ":" + databaseName2;
            }

            if (databaseType2.equals("PostgreSQL")) {
                Class.forName("org.postgresql.Driver");
                url2 = "jdbc:postgresql://" + ip2 + ":" + port2 + "/" + databaseName2;
            }

            if (databaseType2.equals("MSSQL")) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                url2 = "jdbc:sqlserver://" + ip2 + ":" + port2 + ";database=" + databaseName2;
            }

            Connection conn = DriverManager.getConnection(url2, user2, pass2);
            return conn != null;
        } catch (Exception var8) {
            return false;
        }
    }

    public static int setupdateData(String sql) throws Exception {
        Connection conn = getConnection();
        Statement stmt = null;

        int var5;
        try {
            stmt = conn.createStatement();
            var5 = stmt.executeUpdate(sql);
        } catch (Exception var12) {
            System.out.println(var12.getMessage());
            throw new Exception(var12.getMessage());
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException var11) {
                System.out.println(var11.getMessage());
                throw new Exception(var11.getMessage());
            }
        }

        return var5;
    }

    public static List<Map<String, Object>> queryForList(String sql) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        String[] fields = (String[])null;
        List<String> times = new ArrayList();
        List<Map<String, Object>> rows = new ArrayList();
        Map row = null;
        conn = getConnection();
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        rsmd = rs.getMetaData();
        int maxSize = rsmd.getColumnCount();
        fields = new String[maxSize];

        for(int i = 0; i < maxSize; ++i) {
            fields[i] = rsmd.getColumnLabel(i + 1);
            if ("java.sql.Timestamp".equals(rsmd.getColumnClassName(i + 1))) {
                times.add(fields[i]);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        while(rs.next()) {
            row = new HashMap();

            for(int i = 0; i < maxSize; ++i) {
                Object value = times.contains(fields[i]) ? rs.getTimestamp(fields[i]) : rs.getObject(fields[i]);
                if (times.contains(fields[i]) && value != null) {
                    value = sdf.format(value);
                }

                row.put(fields[i], value);
            }

            rows.add(row);
        }

        try {
            rs.close();
            pstmt.close();
            conn.close();
            return rows;
        } catch (SQLException var13) {
            throw new Exception(var13.getMessage());
        }
    }

    public static List<Map<String, Object>> queryForList2(String sql) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        String[] fields = (String[])null;
        List<Map<String, Object>> rows = new ArrayList();
        Map row = null;
        conn = getConnection();
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        rsmd = rs.getMetaData();
        int maxSize = rsmd.getColumnCount();
        fields = new String[maxSize];

        while(rs.next()) {
            row = new HashMap();

            for(int i = 0; i < maxSize; ++i) {
                row.put(rsmd.getColumnLabel(i + 1), rs.getObject(rsmd.getColumnLabel(i + 1)));
            }

            rows.add(row);
        }

        return rows;
    }

    public static List<Map<String, Object>> queryForListPage(String sql, int maxRow, int beginIndex) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        String[] fields = (String[])null;
        List<String> times = new ArrayList();
        List<Map<String, Object>> rows = new ArrayList();
        Map row = null;
        conn = getConnection();
        pstmt = conn.prepareStatement(sql, 1005, 1008);
        pstmt.setMaxRows(maxRow);
        rs = pstmt.executeQuery();
        rsmd = rs.getMetaData();
        int maxSize = rsmd.getColumnCount();
        fields = new String[maxSize];

        for(int i = 0; i < maxSize; ++i) {
            fields[i] = rsmd.getColumnLabel(i + 1);
            if ("java.sql.Timestamp".equals(rsmd.getColumnClassName(i + 1))) {
                times.add(fields[i]);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rs.absolute(beginIndex);

        while(rs.next()) {
            row = new HashMap();

            for(int i = 0; i < maxSize; ++i) {
                Object value = times.contains(fields[i]) ? rs.getTimestamp(fields[i]) : rs.getObject(fields[i]);
                if (times.contains(fields[i]) && value != null) {
                    value = sdf.format(value);
                }

                row.put(fields[i], value);
            }

            rows.add(row);
        }

        try {
            rs.close();
            pstmt.close();
            conn.close();
            return rows;
        } catch (SQLException var15) {
            throw new Exception(var15.getMessage());
        }
    }

    public static List<Map<String, Object>> queryForListWithType(String sql) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList rows2 = new ArrayList();

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData rsme = rs.getMetaData();
            int columnCount = rsme.getColumnCount();
            rs.next();

            for(int i = 1; i < columnCount + 1; ++i) {
                Map<String, Object> map = new HashMap();
                map.put("column_name", rsme.getColumnName(i));
                map.put("column_value", rs.getObject(rsme.getColumnName(i)));
                map.put("data_type", rsme.getColumnTypeName(i));
                map.put("precision", rsme.getPrecision(i));
                map.put("isAutoIncrement", rsme.isAutoIncrement(i));
                map.put("is_nullable", rsme.isNullable(i));
                map.put("isReadOnly", rsme.isReadOnly(i));
                rows2.add(map);
            }
        } catch (Exception var17) {
            System.out.println("queryForListWithType  " + var17.getMessage());
        } finally {
            try {
                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException var16) {
                ;
            }

        }

        return rows2;
    }

    public static List<Map<String, Object>> queryForColumnOnly(String sql) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList rows2 = new ArrayList();

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData rsme = rs.getMetaData();
            int columnCount = rsme.getColumnCount();

            for(int i = 1; i < columnCount + 1; ++i) {
                Map<String, Object> map = new HashMap();
                map.put("column_name", rsme.getColumnName(i));
                map.put("data_type", rsme.getColumnTypeName(i));
                map.put("precision", rsme.getPrecision(i));
                map.put("isAutoIncrement", rsme.isAutoIncrement(i));
                map.put("is_nullable", rsme.isNullable(i));
                map.put("isReadOnly", rsme.isReadOnly(i));
                rows2.add(map);
            }
        } catch (Exception var17) {
            System.out.println("queryForColumnOnly  " + var17.getMessage());
        } finally {
            try {
                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException var16) {
                ;
            }

        }

        return rows2;
    }

    public static List<Map<String, Object>> executeSqlForColumns(String sql) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        List<Map<String, Object>> rows = new ArrayList();
        conn = getConnection();
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        rsmd = rs.getMetaData();
        int maxSize = rsmd.getColumnCount();

        for(int i = 0; i < maxSize; ++i) {
            Map<String, Object> map = new HashMap();
            map.put("column_name", rsmd.getColumnLabel(i + 1));
            map.put("data_type", rsmd.getColumnTypeName(i + 1));
            rows.add(map);
        }

        rs.close();
        pstmt.close();
        conn.close();
        return rows;
    }

    public static int executeQueryForCount(String sql) {
        int rowCount = 0;
        Connection conn = getConnection();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();

            Long count;
            for(rs = stmt.executeQuery(sql); rs.next(); rowCount = count.intValue()) {
                count = (Long)rs.getObject("count(*)");
            }
        } catch (Exception var14) {
            System.out.println(var14.getMessage());
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException var13) {
                ;
            }

        }

        return rowCount;
    }

    public static int executeQueryForCount2(String sql) {
        int rowCount = 0;
        Connection conn = getConnection();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            rs.last();
            rowCount = rs.getRow();
        } catch (Exception var14) {
            System.out.println(var14.getMessage());
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException var13) {
                ;
            }

        }

        return rowCount;
    }

    public static boolean executeQuery(String sql) {
        boolean bl = false;
        Connection conn = getConnection();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                bl = true;
            }
        } catch (Exception var14) {
            ;
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException var13) {
                ;
            }

        }

        return bl;
    }

    public static boolean testConn() {
        boolean bl = false;
        Connection conn = getConnection();
        if (conn != null) {
            bl = true;
        }

        try {
            conn.close();
        } catch (Exception var3) {
            ;
        }

        return bl;
    }

    public String getPrimaryKeys(String databaseName, String tableName) {
        Connection conn = null;
        new ArrayList();

        String var8;
        try {
            conn = getConnection();
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet rs2 = metadata.getPrimaryKeys(databaseName, (String)null, tableName);
            if (!rs2.next()) {
                return "";
            }

            System.out.println("主键名称: " + rs2.getString(4));
            var8 = rs2.getString(4);
        } catch (Exception var17) {
            System.out.println("queryForColumnOnly  " + var17.getMessage());
            return "";
        } finally {
            try {
                conn.close();
            } catch (SQLException var16) {
                ;
            }

        }

        return var8;
    }

    public List<String> getPrimaryKeyss(String databaseName, String tableName) {
        Connection conn = null;
        ArrayList rows2 = new ArrayList();

        try {
            conn = getConnection();
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet rs2 = metadata.getPrimaryKeys(databaseName, (String)null, tableName);

            while(rs2.next()) {
                System.out.println("主键名称2: " + rs2.getString(4));
                rows2.add(rs2.getString(4));
            }
        } catch (Exception var15) {
            System.out.println("queryForColumnOnly  " + var15.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException var14) {
                ;
            }

        }

        return rows2;
    }

    public static int executeQueryForCountForOracle(String sql) {
        int rowCount = 0;
        Connection conn = getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        String sql3 = " select count(*) as count from  (" + sql + ")";

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql3);
            rs.next();
            rowCount = rs.getInt("count");
        } catch (Exception var15) {
            System.out.println(var15.getMessage());
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException var14) {
                ;
            }

        }

        return rowCount;
    }

    public static int executeQueryForCountForPostgreSQL(String sql) {
        int rowCount = 0;
        Connection conn = getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        String sql3 = " select count(*) as count from  (" + sql + ") t ";

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql3);
            rs.next();
            rowCount = rs.getInt("count");
        } catch (Exception var15) {
            System.out.println(var15.getMessage());
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException var14) {
                ;
            }

        }

        return rowCount;
    }
}
