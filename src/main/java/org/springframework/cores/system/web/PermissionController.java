package org.springframework.cores.system.web;

/**
 * <p>Title: PermissionController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: www.hanshow.com</p>
 *
 * @author guolin
 * @version 1.0
 * @date 2018-07-27 16:06
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ctl.utils.ConfigUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cores.common.persistence.Page;
import org.springframework.cores.common.utils.StringUtils;
import org.springframework.cores.common.web.BaseController;
import org.springframework.cores.system.entity.Config;
import org.springframework.cores.system.entity.NotSqlEntity;
import org.springframework.cores.system.entity.Person;
import org.springframework.cores.system.service.PermissionService;
import org.springframework.cores.system.utils.Constants;
import org.springframework.cores.system.utils.MacAddress;
import org.springframework.cores.system.utils.RedisAPI;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"system/permission"})
public class PermissionController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    public PermissionController() {
    }

    @RequestMapping({"i/allDatabaseListForNoSQL/{id}"})
    @ResponseBody
    public List<Map<String, Object>> allDatabaseListForNoSQL(@PathVariable("id") String databaseConfigId) throws Exception {
        Object listDb = new ArrayList();

        try {
            Map<String, Object> map = this.permissionService.getConfig(databaseConfigId);
            Constants.DATABASETYPE = (String)map.get("databaseType");
            if (Constants.DATABASETYPE.equals("Memcache")) {
                listDb = this.permissionService.getAllDataBaseForMemcached(databaseConfigId);
            }

            if (Constants.DATABASETYPE.equals("Redis")) {
                listDb = this.permissionService.getAllDataBaseForReids(databaseConfigId);
            }
            return (List)listDb;
        } catch (Exception var4) {
            logger.error("Permission异常", var4);
            return null;
        }
    }

    public Map<String, Object> executeSqlNotRes(String sql, String dbName) {
        String mess = "";
        String status = "";
        Date b1 = new Date();
        int i = 0;
        try {
            i = this.permissionService.executeSqlNotRes(sql, dbName);
            mess = "执行成功！";
            status = "success";
        } catch (Exception var11) {
            mess = var11.getMessage();
            status = "fail";
            logger.error("Permission异常", var11);
        }

        Date b2 = new Date();
        long y = b2.getTime() - b1.getTime();
        Map<String, Object> map = new HashMap();
        map.put("totalCount", i);
        map.put("time", y);
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/saveSearchHistory"}, method = {RequestMethod.POST})
    @ResponseBody
    public String saveSearchHistory(HttpServletRequest request) {
        String id = request.getParameter("id");
        String sql = request.getParameter("sql");
        String dbName = request.getParameter("dbName");
        String name = request.getParameter("name");
        sql = sql.replaceAll("'", "''");
        boolean bool = true;
        if (id != null && !"".equals(id)) {
            bool = this.permissionService.updateSearchHistory(id, name, sql, dbName);
        } else {
            bool = this.permissionService.saveSearchHistory(name, sql, dbName);
        }
        return bool ? "success" : "fail";
    }

    @RequestMapping(value = {"i/deleteSearchHistory"},method = {RequestMethod.POST})
    @ResponseBody
    public String deleteSearchHistory(@RequestBody TempDto tem, HttpServletRequest request) {
        String id = tem.getId();
        boolean bool = true;
        if (id != null || !"".equals(id)) {
            bool = this.permissionService.deleteSearchHistory(id);
        }
        return bool ? "success" : "fail";
    }

    @RequestMapping(value = {"i/selectSearchHistory"},method = {RequestMethod.GET})
    @ResponseBody
    public List<Map<String, Object>> selectSearchHistory() {
        List<Map<String, Object>> list = this.permissionService.selectSearchHistory();
        List<Map<String, Object>> list2 = new ArrayList();
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            Map<String, Object> map = (Map)var4.next();
            String tempName = (String)map.get("name");
            map.put("name", tempName);
            map.put("pid", "0");
            map.put("icon", "icon-hamburg-zoom");
            list2.add(map);
        }

        return list2;
    }

    @RequestMapping(value = {"i/config"}, method = {RequestMethod.GET})
    public String config(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String username = (String)session.getAttribute("LOGIN_USER_NAME");
        return username == null ? "system/login" : "system/configList";
    }

    @RequestMapping(value = {"i/addConfigForm"}, method = {RequestMethod.GET})
    public String addConfigForm(Model model) {
        return "system/configForm";
    }

    @RequestMapping(value = {"i/editConfigForm/{id}"}, method = {RequestMethod.GET})
    public String editConfigForm(@PathVariable("id") String id, Model model) {
        Object map = new HashMap();

        try {
            map = this.permissionService.getConfig(id);
        } catch (Exception var5) {
            logger.error("Permission异常", var5);
        }

        model.addAttribute("config", map);
        return "system/configForm";
    }

    @RequestMapping(value = {"i/configUpdate"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> configUpdate(@ModelAttribute @RequestBody Config config, Model model) {
        String databaseType = config.getDatabaseType();
        String ip = config.getIp();
        String port = config.getPort();
        String dbName = config.getDatabaseName();
        String url = "";
        if (databaseType.equals("MySql")) {
            url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName;
        }

        if (databaseType.equals("Oracle")) {
            url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbName;
        }

        if (databaseType.equals("PostgreSQL")) {
            url = "jdbc:postgresql://" + ip + ":" + port + "/" + dbName;
        }

        if (databaseType.equals("MSSQL")) {
            url = "jdbc:sqlserver://" + ip + ":" + port + ";database=" + dbName;
        }

        String mess = "";
        String status = "";
        config.setUrl(url);

        try {
            this.permissionService.configUpdate(config);
            mess = "修改成功";
            status = "success";
        } catch (Exception var11) {
            mess = var11.getMessage();
            status = "fail";
            logger.error("Permission异常", var11);
        }

        Map<String, Object> map = new HashMap();
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/changePass"}, method = {RequestMethod.GET})
    public String changePass(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String username = (String)session.getAttribute("LOGIN_USER_NAME");
        return username == null ? "system/login" : "system/changePass";
    }

    @RequestMapping(value = {"i/searchHistory"}, method = {RequestMethod.GET})
    public String searchHistory(Model model) {
        return "system/searchHistory";
    }

    @RequestMapping(value = {"i/changePassUpdate"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> changePassUpdate(HttpServletRequest request) {
        String mess = "";
        String status = "";
        HttpSession session = request.getSession(true);
        String userByName = (String)session.getAttribute("LOGIN_USER_NAME");
        String oldPass = request.getParameter("oldPass");
        String newPass = request.getParameter("newPass");
        List<Map<String, Object>> list = this.permissionService.selectUserByName(userByName);
        String oldPass2 = "";
        String userId = "";
        if (list.size() > 0) {
            Map<String, Object> map = (Map)list.get(0);
            oldPass2 = (String)map.get("password");
            userId = "" + map.get("id");
        } else {
            mess = "error";
            status = "fail";
        }

        oldPass = StringUtils.MD5(oldPass + ConfigUtils.getType("md5.pass.encrypt.str") + userByName);
        if (!oldPass.equals(oldPass2)) {
            mess = "旧密码不符！";
            status = "fail";
        } else {
            try {
                this.permissionService.changePassUpdate(userId, StringUtils.MD5(newPass + ConfigUtils.getType("md5.pass.encrypt.str") + userByName));
                mess = "修改密码成功";
                status = "success";
            } catch (Exception var12) {
                mess = var12.getMessage();
                status = "fail";
                logger.error("Permission异常", var12);
            }
        }

        Map<String, Object> map = new HashMap();
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/addRow/{tableName}/{databaseName}"}, method = {RequestMethod.GET})
    public String addRows(@PathVariable("tableName") String tableName, @PathVariable("databaseName") String databaseName, HttpServletRequest request) throws Exception {
        List<Map<String, Object>> listAllColumn = this.permissionService.getTableColumns(databaseName, tableName);
        List<Map<String, Object>> listAllColumn2 = new ArrayList();

        for(int i = 0; i < listAllColumn.size(); ++i) {
            Map<String, Object> map2 = (Map)listAllColumn.get(i);
            boolean isAutoIncrement = (Boolean)map2.get("isAutoIncrement");
            if (!isAutoIncrement) {
                listAllColumn2.add(map2);
            }
        }

        request.setAttribute("databaseName", databaseName);
        request.setAttribute("tableName", tableName);
        request.setAttribute("listAllColumn", listAllColumn2);
        return "system/addRowOne";
    }

    @RequestMapping(value = {"i/saveRows"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> saveRows(HttpServletRequest request) {
        String databaseName = request.getParameter("databaseName");
        String tableName = request.getParameter("tableName");
        Map<String, Object> mapResult = new HashMap();
        Map maps = new HashMap();
        Map<String, String[]> map = request.getParameterMap();
        Set<Entry<String, String[]>> set = map.entrySet();
        Iterator<Entry<String, String[]>> it = set.iterator();
        String column = "";
        String value = "";
        String mess = "";
        String status = "";

        while(it.hasNext()) {
            Entry<String, String[]> entry = (Entry)it.next();
            column = (String)entry.getKey();
            String[] var17;
            int var16 = (var17 = (String[])entry.getValue()).length;

            for(int var15 = 0; var15 < var16; ++var15) {
                String i = var17[var15];
                value = i.replaceAll("'", "''");
            }

            maps.put(column, value);
        }

        maps.remove("databaseName");
        maps.remove("tableName");

        try {
            this.permissionService.saveRows(maps, databaseName, tableName);
            mess = "新增成功！";
            status = "success";
        } catch (Exception var18) {
            mess = var18.getMessage();
            status = "fail";
        }

        mapResult.put("mess", mess);
        mapResult.put("status", status);
        return mapResult;
    }

    @RequestMapping(value = {"i/editRows/{tableName}/{databaseName}/{id}/{idValues}"}, method = {RequestMethod.GET})
    public String editRows(@PathVariable("tableName") String tableName, @PathVariable("databaseName") String databaseName, @PathVariable("id") String id, @PathVariable("idValues") String idValues, HttpServletRequest request) {
        List<Map<String, Object>> listAllColumn = this.permissionService.getOneRowById(databaseName, tableName, id, idValues);
        List<Map<String, Object>> newList = new ArrayList();

        for(int i = 0; i < listAllColumn.size(); ++i) {
            Map<String, Object> map3 = (Map)listAllColumn.get(i);
            String data_type = (String)map3.get("data_type");
            if (data_type.equals("VARCHAR")) {
                String column_value = (String)map3.get("column_value");
                column_value = htmlEscape(column_value);
                map3.put("column_value", column_value);
            }

            newList.add(map3);
        }

        request.setAttribute("databaseName", databaseName);
        request.setAttribute("tableName", tableName);
        request.setAttribute("listAllColumn", newList);
        request.setAttribute("id", id);
        request.setAttribute("idValues", idValues);
        return "system/editRowOne";
    }

    @RequestMapping(value = {"i/updateRows"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> updateRows(HttpServletRequest request) {
        String mess = "";
        String status = "";
        Map<String, Object> mapResult = new HashMap();
        String databaseName = request.getParameter("databaseName");
        String tableName = request.getParameter("tableName");
        String id = request.getParameter("id");
        String idValues = request.getParameter("idValues");
        Map maps = new HashMap();
        Map<String, String[]> map = request.getParameterMap();
        Set<Entry<String, String[]>> set = map.entrySet();
        Iterator<Entry<String, String[]>> it = set.iterator();
        String column = "";
        String value = "";

        while(it.hasNext()) {
            Entry<String, String[]> entry = (Entry)it.next();
            column = (String)entry.getKey();
            String[] var19;
            int var18 = (var19 = (String[])entry.getValue()).length;

            for(int var17 = 0; var17 < var18; ++var17) {
                String i = var19[var17];
                value = i;
            }

            value = value.replaceAll("'", "''");
            maps.put(column, value);
        }

        maps.remove("databaseName");
        maps.remove("tableName");
        maps.remove("id");
        maps.remove("idValues");

        try {
            this.permissionService.updateRows(maps, databaseName, tableName, id, idValues);
            mess = " 更新成功！";
            status = "success";
        } catch (Exception var20) {
            mess = var20.getMessage();
            status = "fail";
            logger.error("Permission异常", var20);
        }

        mapResult.put("mess", mess);
        mapResult.put("status", status);
        return mapResult;
    }

    public static String htmlEscape(String strData) {
        if (strData == null) {
            return "";
        } else {
            strData = replaceString(strData, "&", "&amp;");
            strData = replaceString(strData, "<", "&lt;");
            strData = replaceString(strData, ">", "&gt;");
            strData = replaceString(strData, "'", "&apos;");
            strData = replaceString(strData, "\"", "&quot;");
            return strData;
        }
    }

    public static String replaceString(String strData, String regex, String replacement) {
        if (strData == null) {
            return null;
        } else {
            int index = strData.indexOf(regex);
            String strNew = "";
            if (index < 0) {
                return strData;
            } else {
                while(index >= 0) {
                    strNew = strNew + strData.substring(0, index) + replacement;
                    strData = strData.substring(index + regex.length());
                    index = strData.indexOf(regex);
                }

                strNew = strNew + strData;
                return strNew;
            }
        }
    }

    @RequestMapping(value = {"i/contribute"}, method = {RequestMethod.GET})
    public String contribute(HttpServletRequest request) {
        return "system/contribute";
    }

    @RequestMapping(value = {"i/help"}, method = {RequestMethod.GET})
    public String help(HttpServletRequest request) {
        return "system/help";
    }

    @RequestMapping(value = {"i/testConn"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> testConn(@RequestBody Config config) {
        Map<String, Object> mapResult = new HashMap();
        String databaseType = config.getDatabaseType();
        String databaseName = config.getDatabaseName();
        String ip = config.getIp();
        String port = config.getPort();
        String user = config.getUserName();
        String pass = config.getPassword();
        String mess = "";
        String status = "";

        boolean bl;
        try {
            bl = this.permissionService.testConn(databaseType, databaseName, ip, port, user, pass);
        } catch (Exception var13) {
            bl = false;
        }

        if (bl) {
            mess = "连接成功！";
            status = "success";
        } else {
            mess = "连接失败！";
            status = "fail";
        }

        mapResult.put("mess", mess);
        mapResult.put("status", status);
        return mapResult;
    }

    @RequestMapping(value = {"i/showTableData/{tableName}/{databaseName}"}, method = {RequestMethod.GET})
    public String showTableData(@PathVariable("tableName") String tableName, @PathVariable("databaseName") String databaseName, HttpServletRequest request) {
        request.setAttribute("databaseName", databaseName);
        request.setAttribute("tableName", tableName);
        return "system/showTableData";
    }

    @RequestMapping(value = {"i/showResult/{sqlIndex}"}, method = {RequestMethod.GET})
    public String showResult(@PathVariable("sqlIndex") String sqlIndex, HttpServletRequest request) {
        request.setAttribute("sqlIndex", sqlIndex);
        return "system/showResult";
    }

    @RequestMapping(value = {"i/selectSqlStudy"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Map<String, Object>> selectSqlStudy(HttpServletRequest request) throws Exception {
        new ArrayList();
        List<Map<String, Object>> list = this.permissionService.selectSqlStudy();
        return list;
    }

    @RequestMapping(value = {"i/updateRow/{tableName}/{databaseName}"}, method = {RequestMethod.GET})
    public Map<String, Object> updateRow(@PathVariable("tableName") String tableName, @PathVariable("databaseName") String databaseName, HttpServletRequest request) {
        Map<String, Object> mapResult = new HashMap();
        String mess = "";
        String status = "";
        mess = "update成功！";
        status = "success";
        mapResult.put("mess", mess);
        mapResult.put("status", status);
        return mapResult;
    }

    @RequestMapping(value = {"i/jsonFormat"}, method = {RequestMethod.GET})
    public String jsonFormat(HttpServletRequest request) throws Exception {
        return "system/jsonFormat";
    }

    @RequestMapping(value = {"i/backupDatabase/{databaseName}"}, method = {RequestMethod.GET})
    public String backupDatabase(@PathVariable("databaseName") String databaseName, HttpServletRequest request) throws Exception {
        request.setAttribute("databaseName", databaseName);
        return "system/backupDatabase";
    }

    @RequestMapping(value = {"i/infoData/{databaseConfigId}"}, method = {RequestMethod.GET})
    public String infoData(@PathVariable("databaseConfigId") String databaseConfigId, HttpServletRequest request) throws Exception {
        request.setAttribute("databaseConfigId", databaseConfigId);
        Map<String, Object> map = this.permissionService.getConfig(databaseConfigId);
        String ip = (String)map.get("ip");
        String port = (String)map.get("port");
        String databaseName = ip + ":" + port;
        request.setAttribute("databaseName", databaseName);
        return "system/infoData";
    }

    @RequestMapping(value = {"i/showNoSQLDBData/{NoSQLDbName}/{databaseConfigId}"}, method = {RequestMethod.GET})
    public String showNoSQLDBData(@PathVariable("NoSQLDbName") String NoSQLDbName, @PathVariable("databaseConfigId") String databaseConfigId, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String username = (String)session.getAttribute("LOGIN_USER_NAME");
        if (username == null) {
            return "system/login";
        } else {
            request.setAttribute("databaseConfigId", databaseConfigId);
            request.setAttribute("NoSQLDbName", NoSQLDbName);
            return "system/showNoSQLDBData";
        }
    }

    @RequestMapping(value = {"i/showNoSQLDBValue/{NoSQLDbName}/{databaseConfigId}/{selectKey}/{selectValue}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> showNoSQLDBValue(@PathVariable("NoSQLDbName") String NoSQLDbName, @PathVariable("databaseConfigId") String databaseConfigId, @PathVariable("selectKey") String selectKey, @PathVariable("selectValue") String selectValue, HttpServletRequest request) {
        Page page = this.getPage(request);

        try {
            if (Constants.DATABASETYPE.equals("Redis")) {
                page = this.permissionService.getNoSQLDBForRedis(page, NoSQLDbName, databaseConfigId, selectKey, selectValue);
            }

            if (Constants.DATABASETYPE.equals("Memcache")) {
                page = this.permissionService.getNoSQLDBForMemcached(page, NoSQLDbName, databaseConfigId, selectKey, selectValue);
            }
        } catch (Exception var8) {
            logger.error("Permission异常", var8);
            return this.getEasyUIData(page);
        }

        return this.getEasyUIData(page);
    }

    @RequestMapping({"i/getConfigAllDataBase"})
    @ResponseBody
    public List<Map<String, Object>> getConfigAllDataBase() throws Exception {
        new ArrayList();

        try {
            List<Map<String, Object>> listDb = this.permissionService.getConfigAllDataBase();
            return listDb;
        } catch (Exception var3) {
            logger.error("Permission异常", var3);
            return null;
        }
    }

    @RequestMapping(value = {"i/deleteNoSQLKeys"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> deleteNoSQLKeys(@RequestBody IdsDto tem) {
        String databaseName = tem.getDatabaseName();
        String NoSQLDbName = tem.getNoSQLDbName();
        String databaseConfigId = tem.getDatabaseConfigId();
        String[] ids = tem.getIds();
        int i = 0;
        String mess = "";
        String status = "";

        try {
            if (Constants.DATABASETYPE.equals("Redis")) {
                this.permissionService.deleteNoSQLKeyForRedis(databaseConfigId, NoSQLDbName, ids);
            }

            if (Constants.DATABASETYPE.equals("Memcache")) {
                this.permissionService.deleteNoSQLKeyForMemcached(databaseConfigId, NoSQLDbName, ids);
            }

            mess = "删除成功";
            status = "success";
        } catch (Exception var10) {
            mess = var10.getMessage();
            status = "fail";
            logger.error("Permission异常", var10);
        }

        Map<String, Object> map = new HashMap();
        map.put("totalCount", Integer.valueOf(i));
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/addOrEditNotSql/{NoSQLDbName}/{databaseConfigId}"}, method = {RequestMethod.GET})
    public String addOrEditNotSql(@PathVariable("NoSQLDbName") String NoSQLDbName, @PathVariable("databaseConfigId") String databaseConfigId, HttpServletRequest request) {
        request.setAttribute("NoSQLDbName", NoSQLDbName);
        request.setAttribute("databaseConfigId", databaseConfigId);
        return "system/addNoSQLDBData";
    }

    @RequestMapping(value = {"i/editNotSqlData"}, method = {RequestMethod.GET})
    public String editNotSqlData(@ModelAttribute @RequestBody NotSqlEntity notSqlEntity2, Model model, HttpServletRequest request) {
        String NoSQLDbName = notSqlEntity2.getNoSQLDbName();
        String databaseConfigId = notSqlEntity2.getDatabaseConfigId();
        String key = notSqlEntity2.getKey();
        NotSqlEntity notSqlEntity = new NotSqlEntity();

        try {
            if (Constants.DATABASETYPE.equals("Redis")) {
                notSqlEntity = this.permissionService.selectNotSqlDataForRedis(key, NoSQLDbName, databaseConfigId);
            }

            if (Constants.DATABASETYPE.equals("Memcache")) {
                notSqlEntity = this.permissionService.selectNotSqlDataForMemcached(key, NoSQLDbName, databaseConfigId);
            }
        } catch (Exception var9) {
            logger.error("Permission异常", var9);
        }

        model.addAttribute("notSqlEntity", notSqlEntity);
        request.setAttribute("NoSQLDbName", NoSQLDbName);
        request.setAttribute("databaseConfigId", databaseConfigId);
        return "system/addNoSQLDBData";
    }

    @RequestMapping(value = {"i/saveNotSqlData/{NoSQLDbName}/{databaseConfigId}"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> saveNotSqlData(@ModelAttribute @RequestBody NotSqlEntity notSqlEntity, Model model, @PathVariable("NoSQLDbName") String NoSQLDbName, @PathVariable("databaseConfigId") String databaseConfigId) {
        String mess = "";
        String status = "";

        try {
            boolean bl = true;
            if ("".equals(notSqlEntity.getExTime())) {
                notSqlEntity.setExTime("0");
            }

            if (Constants.DATABASETYPE.equals("Redis")) {
                bl = this.permissionService.saveNotSqlDataForRedis(notSqlEntity, databaseConfigId, NoSQLDbName);
            }

            if (Constants.DATABASETYPE.equals("Memcache")) {
                bl = this.permissionService.saveNotSqlDataForMemcached(notSqlEntity, databaseConfigId);
            }

            if (bl) {
                mess = "保存成功";
                status = "success";
            } else {
                mess = "保存失败";
                status = "fail";
            }
        } catch (Exception var8) {
            mess = var8.getMessage();
            status = "fail";
            logger.error("Permission异常", var8);
        }

        Map<String, Object> map = new HashMap();
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/selectNoSQLDBStatus/{databaseConfigId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> selectNoSQLDBStatus(@PathVariable("databaseConfigId") String databaseConfigId, HttpServletRequest request) {
        Page<Map<String, Object>> page = this.getPage(request);
        Map<String, Object> map = new HashMap();
        String mess = "";
        String status = "";

        try {
            if (Constants.DATABASETYPE.equals("Redis")) {
                page = this.permissionService.selectNoSQLDBStatusForRedis(page, databaseConfigId);
            }

            if (Constants.DATABASETYPE.equals("Memcache")) {
                page = this.permissionService.selectNoSQLDBStatusForMemcached(page, databaseConfigId);
            }

            map.put("rows", page.getResult());
            map.put("total", page.getTotalCount());
            map.put("columns", page.getColumns());
            map.put("primaryKey", page.getPrimaryKey());
            mess = "查询成功";
            status = "success";
        } catch (Exception var8) {
            System.out.println(" 系统当前状态参数查询失败,可能参数配置有误！ " + var8.getMessage());
            mess = var8.getMessage();
            status = "fail";
            logger.error("Permission异常", var8);
        }

        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/showSQLMess"}, method = {RequestMethod.GET})
    public String showSQLMess(HttpServletRequest request) {
        return "system/showSQLMess";
    }

    @RequestMapping(value = {"i/backupDatabaseData/{random}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> backupDatabaseData(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap();
        new ArrayList();
        String path = request.getSession().getServletContext().getRealPath("/") + "backup";
        List<Map<String, Object>> list = this.permissionService.selectBackupList(path);
        map.put("rows", list);
        map.put("total", list.size());
        return map;
    }

    @RequestMapping(value = {"i/configList"}, method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> configList(HttpServletRequest request) throws Exception {
        Page page = this.getPage(request);

        try {
            page = this.permissionService.configList(page);
        } catch (Exception var4) {
            logger.error("Permission异常", var4);
            return this.getEasyUIData(page);
        }

        return this.getEasyUIData(page);
    }

    @RequestMapping(value = {"i/backupNotSqlDatabase"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> backupNotSqlDatabase(@RequestBody IdsDto tem, HttpServletRequest request) {
        String databaseConfigId = tem.getDatabaseConfigId();
        Map<String, Object> mapResult = new HashMap();
        String mess = "";
        String status = "";
        String path = request.getSession().getServletContext().getRealPath("/");

        try {
            if (Constants.DATABASETYPE.equals("Redis")) {
                this.permissionService.backupNotSqlDatabaseForRedis(databaseConfigId, path);
                Thread.sleep(3000L);
                mess = "备份完成！";
                status = "success";
            }

            if (Constants.DATABASETYPE.equals("Memcache")) {
                Thread.sleep(3000L);
                mess = "不支持Memcache备份！";
                status = "success";
            }
        } catch (FileNotFoundException var9) {
            mess = "备份数据库失败,需与redis安装在同一台电脑！";
            status = "fail";
            logger.error("Permission异常", var9);
        } catch (Exception var10) {
            System.out.println(var10.getMessage());
            mess = "备份数据库失败！";
            status = "fail";
            logger.error("Permission异常", var10);
        }

        mapResult.put("mess", mess);
        mapResult.put("status", status);
        return mapResult;
    }

    @RequestMapping(value = {"i/deleteBackupFile"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> deleteBackupFile(@RequestBody IdsDto tem, HttpServletRequest request) {
        String[] ids = tem.getIds();
        String mess = "";
        String status = "";
        String path = request.getSession().getServletContext().getRealPath("/") + "backup" + File.separator;

        try {
            this.permissionService.deleteBackupFile(ids, path);
            mess = "删除成功";
            status = "success";
        } catch (Exception var8) {
            mess = var8.getMessage();
            status = "fail";
            logger.error("Permission异常", var8);
        }

        Map<String, Object> map = new HashMap();
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/backupFileDownload/{fileName:.+}"}, method = {RequestMethod.GET})
    @ResponseBody
    public void backupFileDownload(@PathVariable("fileName") String fileName, HttpServletRequest request, HttpServletResponse response) {
        String path = request.getSession().getServletContext().getRealPath("/") + "backup" + File.separator;
        File file = new File(path + fileName);
        response.setContentType("multipart/form-data");
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.addHeader("Content-Length", "" + file.length());

        try {
            FileInputStream inputStream = new FileInputStream(file);
            ServletOutputStream out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            boolean var10 = false;

            int len;
            while((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            inputStream.close();
            out.close();
            out.flush();
        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }

    @RequestMapping(value = {"i/restoreDBForRedis"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> restoreDBForRedis(@RequestBody IdsDto tem, HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("/") + "backup" + File.separator;
        Map<String, Object> map = new HashMap();
        String mess = "";
        String status = "";
        String fileName = "";
        String databaseConfigId = tem.getDatabaseConfigId();
        String[] ids = tem.getIds();
        if (Constants.DATABASETYPE.equals("Memcache")) {
            mess = "不支持Memcache还原操作！";
            status = "success";
            map.put("mess", mess);
            map.put("status", status);
            return map;
        } else {
            try {
                fileName = ids[0];
                File srcFile = new File(path + fileName);
                Map<String, Object> map3 = this.permissionService.getConfig(databaseConfigId);
                String redisDir = RedisAPI.getConfig2(map3, "dir");
                File tarFile = new File(redisDir + File.separator + "dump.rdb");
                if (tarFile.exists()) {
                    tarFile.delete();
                }

                FileUtils.copyFile(srcFile, tarFile);
                mess = "数据库还原成功,请重新启动Redis!";
                status = "success";
            } catch (Exception var14) {
                System.out.println(var14.getMessage());
                mess = "数据库还原出错!";
                status = "fail";
                logger.error("Permission异常", var14);
            }

            map.put("mess", mess);
            map.put("status", status);
            return map;
        }
    }

    @RequestMapping({"i/queryInfoItem/{databaseConfigId}"})
    @ResponseBody
    public Map<String, Object> queryInfoItem(@PathVariable("databaseConfigId") String databaseConfigId) {
        Map<String, Object> map = new HashMap();
        String mess = "";
        String status = "";

        try {
            Map<String, Object> map33 = this.permissionService.getConfig(databaseConfigId);
            String databaseType = (String)map33.get("databaseType");
            if (databaseType.equals("Redis")) {
                map = this.permissionService.queryInfoItemForRedis(databaseConfigId);
            }

            if (databaseType.equals("Memcache")) {
                map = this.permissionService.queryInfoItemForMemcached(databaseConfigId);
            }

            mess = "查询成功";
            status = "success";
        } catch (Exception var7) {
            mess = var7.getMessage();
            System.out.println("mess=" + mess);
            status = "fail";
            logger.error("Permission异常", var7);
            return null;
        }

        ((Map)map).put("mess", mess);
        ((Map)map).put("status", status);
        return (Map)map;
    }

    @RequestMapping({"i/getMemoryConsumption/{databaseConfigId}"})
    @ResponseBody
    public Map<String, Object> getMemoryConsumption(@PathVariable("databaseConfigId") String databaseConfigId) {
        new ArrayList();
        Map<String, Object> map = new HashMap();
        String mess = "";
        String status = "";

        try {
            if (Constants.DATABASETYPE.equals("Redis")) {
                this.permissionService.getMemoryConsumptionForRedis(databaseConfigId);
            }

            if (Constants.DATABASETYPE.equals("Memcache")) {
                map = this.permissionService.queryInfoItemForMemcached(databaseConfigId);
            }

            mess = "查询成功";
            status = "success";
        } catch (Exception var7) {
            mess = var7.getMessage();
            status = "fail";
            logger.error("Permission异常", var7);
            return null;
        }

        ((Map)map).put("mess", mess);
        ((Map)map).put("status", status);
        return (Map)map;
    }

    @RequestMapping(value = {"i/deleteConfig"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> deleteConfig(@RequestBody IdsDto tem, HttpServletRequest request) {
        String[] ids = tem.getIds();
        String mess = "";
        String status = "";

        try {
            this.permissionService.deleteConfig(ids);
            mess = "删除成功";
            status = "success";
        } catch (Exception var7) {
            mess = var7.getMessage();
            status = "fail";
            logger.error("Permission异常", var7);
        }

        Map<String, Object> map = new HashMap();
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/person"}, method = {RequestMethod.GET})
    public String person(Model model) {
        return "system/personList";
    }

    @RequestMapping(value = {"i/personList"}, method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> personList(HttpServletRequest request) throws Exception {
        Page page = this.getPage(request);

        try {
            page = this.permissionService.personList(page);
        } catch (Exception var4) {
            logger.error("Permission异常", var4);
            return this.getEasyUIData(page);
        }

        return this.getEasyUIData(page);
    }

    @RequestMapping(value = {"i/addPersonForm"}, method = {RequestMethod.GET})
    public String addPersonForm(Model model) {
        return "system/personForm";
    }

    @RequestMapping(value = {"i/editPersonForm/{id}"}, method = {RequestMethod.GET})
    public String editPersonForm(@PathVariable("id") String id, Model model) {
        Object map = new HashMap();

        try {
            map = this.permissionService.getPerson(id);
        } catch (Exception var5) {
            logger.error("Permission异常", var5);
        }

        model.addAttribute("person", map);
        return "system/personForm";
    }

    @RequestMapping(value = {"i/personUpdate"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> personUpdate(@RequestBody Person person, Model model) {
        String mess = "";
        String status = "";
        Map<String, Object> map = new HashMap();
        boolean isvalidate = this.permissionService.identifying();
        if (!isvalidate) {
            mess = "软件未注册！请购买商业授权！";
            status = "fail";
            map.put("mess", mess);
            map.put("status", status);
            return map;
        } else {
            try {
                this.permissionService.personUpdate(person);
                mess = "操作成功";
                status = "success";
            } catch (Exception var8) {
                mess = "error:" + var8.getMessage();
                status = "fail";
                logger.error("Permission异常", var8);
            }

            map.put("mess", mess);
            map.put("status", status);
            return map;
        }
    }

    @RequestMapping(value = {"i/deletePerson"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> deletePerson(@RequestBody IdsDto tem, HttpServletRequest request) {
        String[] ids = tem.getIds();
        String mess = "";
        String status = "";

        try {
            this.permissionService.deletePerson(ids);
            mess = "删除成功";
            status = "success";
        } catch (Exception var7) {
            mess = var7.getMessage();
            status = "fail";
            logger.error("Permission异常", var7);
        }

        Map<String, Object> map = new HashMap();
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/resetPersonPass"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> resetPersonPass(@RequestBody IdsDto tem, HttpServletRequest request) {
        String[] ids = tem.getIds();
        String mess = "";
        String status = "";

        try {
            this.permissionService.resetPersonPass(ids);
            mess = "操作成功，新密码为："+ConfigUtils.getType("md5.pass.encrypt.str");
            status = "success";
        } catch (Exception var7) {
            mess = var7.getMessage();
            status = "fail";
            logger.error("Permission异常", var7);
        }

        Map<String, Object> map = new HashMap();
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }

    @RequestMapping(value = {"i/register"}, method = {RequestMethod.GET})
    public String register(HttpServletRequest request) {
        String personNumber = MacAddress.getMac();
        Map<String, Object> map = this.permissionService.getRegisterMess();
        request.setAttribute("personNumber", personNumber);
        request.setAttribute("company", map.get("company"));
        request.setAttribute("token", map.get("token"));
        request.setAttribute("mess", map.get("mess"));
        return "system/register";
    }

    @RequestMapping(value = {"i/registerUpdate"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> registerUpdate(@RequestBody TempDto tem, HttpServletRequest request) {
        String mess = "";
        String status = "";

        try {
            boolean bl = this.permissionService.registerUpdate(tem);
            if (bl) {
                mess = "注册成功!";
                status = "success";
            } else {
                mess = "注册失败!";
                status = "success";
            }
        } catch (Exception var6) {
            mess = var6.getMessage();
            status = "fail";
            logger.error("Permission异常", var6);
        }

        Map<String, Object> map = new HashMap();
        map.put("mess", mess);
        map.put("status", status);
        return map;
    }
}
