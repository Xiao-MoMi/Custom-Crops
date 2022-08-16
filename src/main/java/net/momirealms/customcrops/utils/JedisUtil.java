package net.momirealms.customcrops.utils;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.helper.Log;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JedisUtil {

    private static JedisPool jedisPool;
    public static boolean useRedis;

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    public static void initializeRedis(YamlConfiguration configuration){

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        jedisPoolConfig.setNumTestsPerEvictionRun(-1);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(configuration.getInt("redis.MinEvictableIdleTimeMillis",1800000));
        jedisPoolConfig.setMaxTotal(configuration.getInt("redis.MaxTotal",8));
        jedisPoolConfig.setMaxIdle(configuration.getInt("redis.MaxIdle",8));
        jedisPoolConfig.setMinIdle(configuration.getInt("redis.MinIdle",1));
        jedisPoolConfig.setMaxWaitMillis(configuration.getInt("redis.MaxWaitMillis",30000));

        jedisPool = new JedisPool(jedisPoolConfig, configuration.getString("redis.host","localhost"), configuration.getInt("redis.port",6379));

        AdventureManager.consoleMessage("<gradient:#ff206c:#fdee55>[CustomCrops] <color:#FFEBCD>Redis Enabled!");

        List<Jedis> minIdleJedisList = new ArrayList<>(jedisPoolConfig.getMinIdle());
        for (int i = 0; i < jedisPoolConfig.getMinIdle(); i++) {
            Jedis jedis;
            try {
                jedis = jedisPool.getResource();
                minIdleJedisList.add(jedis);
                jedis.ping();
            } catch (Exception e) {
                Log.warn(e.getMessage());
            }
        }

        for (int i = 0; i < jedisPoolConfig.getMinIdle(); i++) {
            Jedis jedis;
            try {
                jedis = minIdleJedisList.get(i);
                jedis.close();
            } catch (Exception e) {
                Log.warn(e.getMessage());
            }
        }
    }

    public static void addPlayer(String player){
        Bukkit.getScheduler().runTaskLaterAsynchronously(CustomCrops.plugin, ()->{
            Jedis jedis = getJedis();
            jedis.sadd("cc_players", player);
            jedis.close();
        }, 20);
    }

    public static void remPlayer(String player){
        Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, ()->{
            Jedis jedis = getJedis();
            jedis.srem("cc_players", player);
            jedis.close();
        });
    }

    public static HashSet<String> getPlayers(){
        Jedis jedis = getJedis();
        HashSet<String> players = (HashSet<String>) jedis.smembers("cc_players");
        jedis.close();
        return players;
    }
}
