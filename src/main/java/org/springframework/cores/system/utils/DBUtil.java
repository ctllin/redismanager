package org.springframework.cores.system.utils;

/**
 * <p>Title: DBUtil</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: www.hanshow.com</p>
 *
 * @author guolin
 * @version 1.0
 * @date 2018-07-21 16:15
 */
import com.ctl.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {
    private static Logger logger = LoggerFactory.getLogger(DBUtil.class);

    public DBUtil() {
    }

    public boolean do_update(String sql) throws Exception {
        try {
            String dbPath = Constants.DATABASEPATH + ConfigUtils.getType("sqlite.db.path");
            ////logger.info("dbpath={}", dbPath);
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            Statement stat = conn.createStatement();
            stat.executeUpdate(sql);
            conn.close();
            return true;
        } catch (Exception e) {
            logger.error("无法找到配置文件，软件安装路径不能包含中文,空隔！", e);
            return false;
        }
    }

    public List executeQuery(String sql) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List rslist = new ArrayList();
        StringBuffer sqlPage = new StringBuffer(sql);
        sqlPage.append(" ");
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String dbPath = Constants.DATABASEPATH + ConfigUtils.getType("sqlite.db.path");
            ////logger.info("dbpath={}", dbPath);
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            pstmt = conn.prepareStatement(sqlPage.toString());
            rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap(numberOfColumns);
                for (int i = 1; i <= numberOfColumns; ++i) {
                    Object o = rs.getObject(i);
                    if ("Date".equalsIgnoreCase(rsmd.getColumnTypeName(i)) && o != null) {
                        row.put(rsmd.getColumnName(i), formatter.format(o));
                    } else {
                        row.put(rsmd.getColumnName(i), o == null ? "" : o);
                    }
                }
                rslist.add(row);
            }
        } catch (Exception var24) {
            try {
                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.error("关闭数据库异常", e);
            }
        } finally {
            try {
                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.error("关闭数据库异常", e);
            }

        }
        return rslist;
    }

    public Object setinsertData(String sql) throws Exception {
        Statement stmt = null;
        String flagOper = "0";
        Connection conn = null;
        Integer var8;
        try {
            String dbPath = Constants.DATABASEPATH + ConfigUtils.getType("sqlite.db.path");
            //logger.info("dbpath={}", dbPath);
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            stmt = conn.createStatement();
            var8 = stmt.executeUpdate(sql);
        } catch (SQLException var15) {
            flagOper = "1";
            throw new Exception(var15.getMessage());
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException var14) {
                throw new Exception(var14.getMessage());
            }
        }
        return var8;
    }

    public boolean initDbConfig() {
        boolean bl = true;
        DBUtil db1 = new DBUtil();
        List<Map<String, Object>> list = db1.executeQuery(" select * from  treesoft_config ");
        Map<String, Object> map = (Map)list.get(0);
        Constants.DATABASETYPE = (String)map.get("databaseType");
        Constants.DRIVER = (String)map.get("driver");
        Constants.URL = (String)map.get("url");
        Constants.DATABASENAME = (String)map.get("databaseName");
        Constants.USERNAME = (String)map.get("userName");
        Constants.PASSWROD = (String)map.get("passwrod");
        Constants.PORT = (String)map.get("port");
        Constants.IP = (String)map.get("ip");
        return bl;
    }

    public List<Map<String, Object>> getConfigList() {
        DBUtil db1 = new DBUtil();
        List<Map<String, Object>> list = db1.executeQuery(" select * from  treesoft_config ");
        return list;
    }

    public List<Map<String, Object>> getPersonList() {
        DBUtil db1 = new DBUtil();
        List<Map<String, Object>> list = db1.executeQuery(" select * from  treesoft_users ");
        return list;
    }
}
