package com.xyz.rbac.cache.redis;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyz.rbac.cache.keys.CacheKeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    public <T> T get(CacheKeyPrefix prefix, String key, Class<T> classz) {
        Jedis jedis =null;
        try {
            jedis= jedisPool.getResource();
            String value= jedis.get(prefix.getKey(key));
            T t=stringToBean(value,classz);
            return t;
        } finally {
            if (jedis != null) {//关闭连接池
                jedis.close();
            }
        }
    }

    public <T> List<T> getList(CacheKeyPrefix prefix, String key, Class<T> classz) {
        Jedis jedis =null;
        try {
            jedis= jedisPool.getResource();
            String value= jedis.get(prefix.getKey(key));
            return stringToListBean(value,classz);
        } finally {
            if (jedis != null) {//关闭连接池
                jedis.close();
            }
        }
    }



    public <T> Boolean set(CacheKeyPrefix prefix,String key,T value) {
        Jedis jedis =null;
        try {
            jedis= jedisPool.getResource();
            String s=beanToString(value);
            if(s!=null) {
                String realKey  = prefix.getKey(key);
                int seconds =  prefix.getSeconds();
                if(seconds<=0) {
                    jedis.set(realKey, s);
                }
                else {
                    jedis.setex(realKey,seconds,s);
                }
                return true;
            }
            return  false;
        } finally {
            if (jedis != null) {//关闭连接池
                jedis.close();
            }
        }
    }





    public <T> boolean exists(CacheKeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            return  jedis.exists(prefix.getKey(key));
        }finally {
            if (jedis != null) {//关闭连接池
                jedis.close();
            }
        }
    }

    /**
     * 增加值
     * */
    public <T> Long incr(CacheKeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            return  jedis.incr(prefix.getKey(key));
        }finally {
            if (jedis != null) {//关闭连接池
                jedis.close();
            }
        }
    }

    /**
     * 减少值
     * */
    public <T> Long decr(CacheKeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            return  jedis.decr(prefix.getKey(key));
        }finally {
            if (jedis != null) {//关闭连接池
                jedis.close();
            }
        }
    }
    public <T> Long delete(CacheKeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            return  jedis.del(prefix.getKey(key));
        }finally {
            if (jedis != null) {//关闭连接池
                jedis.close();
            }
        }
    }

    private <T> List<T> stringToListBean(String value, Class<T> classz) {
        //JSONArray jsonArray= JSONArray.parseArray(value);
        //List<T> list=new ArrayList<T>();
        //for (int i=0,len=jsonArray.size();i<len;i++){
        //    JSON json=(JSON) jsonArray.get(i);
        //    if(json!=null){
        //        if (classz == int.class || classz == Integer.class) {
        //            list.add( (T) Integer.valueOf(value));
        //        } else if (classz == String.class) {
        //            list.add( (T)value);
        //        } else if (classz == long.class || classz == Long.class) {
        //            list.add((T) Long.valueOf(value));
        //        }else {
        //            list.add(JSON.toJavaObject(json, classz));
        //        }
        //    }
        //}
        //return list;
        //
        return JSONArray.parseArray(value, classz);
    }

    private <T>  T stringToBean(String value,Class<T> classz) {
        if (value == null || value.length() == 0 || classz == null) {
            return null;
        }
        if (classz == int.class || classz == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (classz == String.class) {
            return (T)value;
        } else if (classz == long.class || classz == Long.class) {
            return (T) Long.valueOf(value);
        }

        return JSON.toJavaObject(JSON.parseObject(value), classz);
    }

    private <T>  String beanToString(T value){
        if(value==null){
            return  null;
        }
        Class<?> classz=value.getClass();
        if(classz==int.class||classz==Integer.class){
            return value.toString();
        }else if(classz==String.class){
            return (String) value;
        }
        else if(classz==long.class||classz==Long.class) {
            return value.toString();
        }
        return  JSON.toJSONString(value);
    }



}