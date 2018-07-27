//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.springframework.cores.system.utils;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.cores.system.entity.NotSqlEntity;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

public class RedisAPI {
  public static JedisPool pool = null;
  private static JedisPool pool2 = null;
  private static int TIMEOUT = 10000;
  private static List nodekeys;
  private Jedis jedis;

  public RedisAPI() {
  }

  public static JedisPool getPool(String ip, String port, String password) {
    if (pool == null) {
      new JedisPoolConfig();
    }

    return pool;
  }

  public static void returnResource(JedisPool pool, Jedis redis) {
    if (redis != null) {
      pool.returnResource(redis);
    }

  }

  public int getDbAmountForRedis(Map<String, Object> map) {
    String ip = (String)map.get("ip");
    String port = (String)map.get("port");
    String password = (String)map.get("password");
    this.jedis = new Jedis(ip, Integer.parseInt(port), 10000);
    if (!password.equals("")) {
      this.jedis.auth(password);
    }

    boolean var5 = true;

    try {
      List dbs = this.jedis.configGet("databases");
      int dbAmount;
      if (dbs.size() > 0) {
        dbAmount = Integer.parseInt((String)dbs.get(1));
      } else {
        dbAmount = 15;
      }

      return dbAmount;
    } catch (Exception var7) {
      System.out.println("取得reids中数据库的数量出错！ " + var7.getMessage());
      var7.printStackTrace();
      return 1;
    }
  }

  public static String getInfo(Map<String, Object> map) {
    String value = null;
    Jedis jedis = null;
    String ip = (String)map.get("ip");
    String port = (String)map.get("port");
    String password = (String)map.get("password");
    if (!"".equals(password) && password != null) {
      jedis = new Jedis(ip, Integer.parseInt(port), 10000);
      jedis.auth(password);
    } else {
      jedis = new Jedis(ip, Integer.parseInt(port), 10000);
    }

    try {
      value = jedis.info();
    } catch (Exception var10) {
      System.out.println("取redis的状态出错," + var10.getMessage());
      var10.printStackTrace();
    } finally {
      jedis.disconnect();
    }

    return value;
  }

  public static String getInfo2(Map<String, Object> map) {
    String value = null;
    Jedis jedis = null;
    String ip = (String)map.get("ip");
    String port = (String)map.get("port");
    String password = (String)map.get("password");

    try {
      pool = getPool(ip, port, password);
      jedis = pool.getResource();
      value = jedis.info();
    } catch (Exception var10) {
      System.out.println("取redis的状态出错," + var10.getMessage());
      pool.returnBrokenResource(jedis);
      var10.printStackTrace();
    } finally {
      returnResource(pool, jedis);
    }

    return value;
  }

  public static String getConfig2(Map<String, Object> map3, String configKey) {
    String value = "";
    Jedis jedis = null;
    String ip = (String)map3.get("ip");
    String port = (String)map3.get("port");
    String password = (String)map3.get("password");
    jedis = new Jedis(ip, Integer.parseInt(port), 10000);
    if (!password.equals("")) {
      jedis.auth(password);
    }

    try {
      List<String> list = jedis.configGet(configKey);

      for(int i = 0; i < list.size(); ++i) {
        value = (String)list.get(i);
      }
    } catch (Exception var9) {
      var9.printStackTrace();
    }

    return value;
  }

  public static Map<String, Object> get2(String key, String NoSQLDbName, Map<String, Object> map3) {
    Jedis jedis = null;
    String ip = (String)map3.get("ip");
    String port = (String)map3.get("port");
    String password = (String)map3.get("password");
    jedis = new Jedis(ip, Integer.parseInt(port), 10000);
    if (!password.equals("")) {
      jedis.auth(password);
    }

    String currentDBindex = NoSQLDbName.substring(2, NoSQLDbName.length());
    String value = null;
    HashMap map = new HashMap();

    try {
      jedis.select(Integer.parseInt(currentDBindex));
      String type = jedis.type(key);
      String exTime = "" + jedis.ttl(key);
      map.put("key", key);
      map.put("type", type);
      map.put("exTime", exTime);
      if (type.equals("string")) {
        map.put("value", jedis.get(key));
      }

      if (type.equals("list")) {
        Long lon = jedis.llen(key);
        map.put("value", jedis.lrange(key, 0L, lon));
      }

      if (type.equals("set")) {
        map.put("value", jedis.smembers(key).toString());
      }

      if (type.equals("zset")) {
        Set<Tuple> set = jedis.zrangeWithScores(key, 0L, -1L);
        map.put("value", set);
      }

      if (type.equals("hash")) {
        map.put("value", jedis.hgetAll(key));
      }
    } catch (Exception var16) {
      var16.printStackTrace();
    } finally {
      jedis.disconnect();
    }

    return map;
  }

  public static boolean bgsave(Map<String, Object> map3) {
    Jedis jedis = null;

    try {
      String ip = (String)map3.get("ip");
      String port = (String)map3.get("port");
      String password = (String)map3.get("password");
      jedis = new Jedis(ip, Integer.parseInt(port), 10000);
      if (!password.equals("")) {
        jedis.auth(password);
      }

      jedis.bgsave();
      return true;
    } catch (Exception var5) {
      var5.printStackTrace();
      return false;
    }
  }

  public static boolean set(NotSqlEntity notSqlEntity, Map<String, Object> map, String NoSQLDbName) {
    Jedis jedis6 = null;

    try {
      String ip = (String)map.get("ip");
      String port = (String)map.get("port");
      String password = (String)map.get("password");
      jedis6 = new Jedis(ip, Integer.parseInt(port), 20000);
      if (!password.equals("")) {
        jedis6.auth(password);
      }

      String currentDBindex = NoSQLDbName.substring(2, NoSQLDbName.length());
      jedis6.select(Integer.parseInt(currentDBindex));
      String key = notSqlEntity.getKey();
      String value = notSqlEntity.getValue();
      value = StringEscapeUtils.unescapeHtml4(value);
      String type = notSqlEntity.getType();
      int o1;
      if (!"".equals(notSqlEntity.getExTime()) && !"0".equals(notSqlEntity.getExTime())) {
        o1 = Integer.parseInt(notSqlEntity.getExTime());
      } else {
        o1 = -1;
      }

      if (type != null && !type.equals("none")) {
        if (type.equals("string")) {
          jedis6.set(key, value);
        }

        String[] valuek;
        if (type.equals("list")) {
          valuek = notSqlEntity.getValuek();
          jedis6.del(key);

          for(int i = valuek.length; i > 0; --i) {
            if (i == valuek.length) {
              jedis6.lpush(key, new String[]{valuek[i - 1]});
            } else {
              jedis6.lpushx(key, new String[]{valuek[i - 1]});
            }
          }
        }

        if (type.equals("set")) {
          valuek = notSqlEntity.getValuek();
          jedis6.del(key);
          String members = "";

          for(int i = 0; i < valuek.length; ++i) {
            if (i == 0) {
              members = members + valuek[i];
            } else {
              members = members + "," + valuek[i];
            }
          }

          jedis6.sadd(key, new String[]{members});
        }

        int i;
        String valuevvv;
        String[] valueV;
        HashMap hashmm;
        if (type.equals("zset")) {
          valuek = notSqlEntity.getValuek();
          valueV = notSqlEntity.getValuev();
          jedis6.del(key);
          hashmm = new HashMap();

          for(i = valuek.length; i > 0; --i) {
            Double valuekkk = Double.parseDouble(valuek[i - 1].trim());
            valuevvv = valueV[i - 1].trim();
            if (valuevvv == null) {
              valuevvv = "";
            }

            hashmm.put(valuevvv, valuekkk);
          }

          jedis6.zadd(key, hashmm);
        }

        if (type.equals("hash")) {
          valuek = notSqlEntity.getValuek();
          valueV = notSqlEntity.getValuev();
          jedis6.del(key);
          hashmm = new HashMap();
          i = valuek.length;

          while(true) {
            if (i <= 0) {
              jedis6.hmset(key, hashmm);
              break;
            }

            String valuekkk = valuek[i - 1].trim();
            valuevvv = valueV[i - 1].trim();
            if (valuevvv == null) {
              valuevvv = "";
            }

            hashmm.put(valuekkk, valuevvv);
            --i;
          }
        }

        if (type.equals("HashSet")) {
          System.out.println("HashSet 类型暂时不支持！");
        }

        if (type.equals("ArryList")) {
          System.out.println("ArryList 类型暂时不支持！");
        }

        if (o1 != -1) {
          jedis6.expire(key, o1);
        }

        return true;
      }

      return false;
    } catch (Exception var21) {
      System.out.println(var21.getMessage());
      var21.printStackTrace();
      jedis6.disconnect();
    } finally {
      jedis6.disconnect();
    }

    return false;
  }

  public static boolean deleteKeys(Map<String, Object> map, String NoSQLDbName, String[] ids) {
    Jedis jedis7 = null;
    String ip = (String)map.get("ip");
    String port = (String)map.get("port");
    String password = (String)map.get("password");
    jedis7 = new Jedis(ip, Integer.parseInt(port), 20000);
    if (!password.equals("")) {
      jedis7.auth(password);
    }

    String currentDBindex = NoSQLDbName.substring(2, NoSQLDbName.length());

    try {
      jedis7.select(Integer.parseInt(currentDBindex));

      for(int i = 0; i < ids.length; ++i) {
        jedis7.del(ids[i]);
      }

      return true;
    } catch (Exception var12) {
      var12.printStackTrace();
      jedis7.disconnect();
    } finally {
      jedis7.disconnect();
    }

    return false;
  }

  public static Map<String, Object> getNoSQLDBForRedis(int pageSize, int limitFrom, Map<String, Object> map1, String NoSQLDbName, String selectKey, String selectValue) {
    String currentDBindex = NoSQLDbName.substring(2, NoSQLDbName.length());
    Map<String, Object> tempMap = new HashMap();
    Jedis jedis2 = null;
    ArrayList list = new ArrayList();

    try {
      String ip = (String)map1.get("ip");
      String port = (String)map1.get("port");
      String password = (String)map1.get("password");
      jedis2 = new Jedis(ip, Integer.parseInt(port), 60000);
      if (!password.equals("")) {
        jedis2.auth(password);
      }

      jedis2.select(Integer.parseInt(currentDBindex));
      Long dbSize = jedis2.dbSize();
      Set nodekeys = new HashSet();
      if (selectKey.equals("nokey")) {
        if (dbSize > 1000L) {
          limitFrom = 0;

          for(int z = 0; z < pageSize; ++z) {
            ((Set)nodekeys).add(jedis2.randomKey());
          }
        } else {
          nodekeys = jedis2.keys("*");
        }
      } else {
        nodekeys = jedis2.keys("*" + selectKey + "*");
      }

      Iterator it = ((Set)nodekeys).iterator();
      int i = 1;

      for(String value = ""; it.hasNext(); ++i) {
        if (i >= limitFrom && i <= limitFrom + pageSize) {
          Map<String, Object> map = new HashMap();
          String key = (String)it.next();
          String type = jedis2.type(key);
          map.put("key", key);
          map.put("type", type);
          if (type.equals("string")) {
            value = jedis2.get(key);
            if (value.length() > 80) {
              map.put("value", value.substring(0, 79) + "......");
            } else {
              map.put("value", value);
            }
          }

          Long lon;
          if (type.equals("list")) {
            lon = jedis2.llen(key);
            if (lon > 20L) {
              lon = 20L;
            }

            map.put("value", jedis2.lrange(key, 0L, lon));
          }

          if (type.equals("set")) {
            map.put("value", jedis2.smembers(key).toString());
          }

          if (type.equals("zset")) {
            lon = jedis2.zcard(key);
            if (lon > 20L) {
              lon = 20L;
            }

            Set<Tuple> set = jedis2.zrangeWithScores(key, 0L, lon);
            Iterator<Tuple> itt = set.iterator();

            String ss;
            Tuple str;
            for(ss = ""; itt.hasNext(); ss = ss + "[" + str.getScore() + "," + str.getElement() + "],") {
              str = (Tuple)itt.next();
            }

            ss = ss.substring(0, ss.length() - 1);
            map.put("value", "[" + ss + "]");
          }

          if (type.equals("hash")) {
            map.put("value", jedis2.hgetAll(key).toString());
          }

          list.add(map);
        } else {
          it.next();
        }
      }

      if (selectKey.equals("nokey")) {
        tempMap.put("rowCount", Integer.parseInt(dbSize.toString()));
      } else {
        tempMap.put("rowCount", i);
      }

      tempMap.put("dataList", list);
    } catch (Exception var29) {
      var29.printStackTrace();
      jedis2.disconnect();
      System.out.println("取得 NoSQL数据出错：" + var29.getMessage());
    } finally {
      jedis2.disconnect();
    }

    return tempMap;
  }

  public static boolean testConnForRedis(String databaseType, String databaseName, String ip, String port, String user, String pass) {
    try {
      Jedis jedis7 = null;
      jedis7 = new Jedis(ip, Integer.parseInt(port), 5000);
      if (!pass.equals("")) {
        jedis7.auth(pass);
      }

      System.out.println("dbSize()=" + jedis7.dbSize());
      return jedis7.isConnected();
    } catch (Exception var7) {
      System.out.println(var7.getMessage());
      return false;
    }
  }

  public static boolean flushAllForRedis(Map<String, Object> map) throws Exception {
    Jedis jedis7 = null;
    String ip = (String)map.get("ip");
    String port = (String)map.get("port");
    String password = (String)map.get("password");
    jedis7 = new Jedis(ip, Integer.parseInt(port));
    password.equals("");
    jedis7.flushAll();
    return true;
  }
}
