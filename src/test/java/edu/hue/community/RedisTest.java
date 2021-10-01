package edu.hue.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 47552
 * @date 2021/09/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test01() {
        redisTemplate.opsForValue().set("test:count",1);

        System.out.println(redisTemplate.opsForValue().get("test:count"));
        System.out.println(redisTemplate.opsForValue().increment("test:count"));
    }

    @Test
    public void testHash(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");
        redisTemplate.opsForHash().put(redisKey,"age",18);

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "age"));
    }

    // 测试 redis 的编程式事务
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                // 开启事务
                redisOperations.multi();

                redisOperations.opsForSet().add(redisKey,"刘备");
                redisOperations.opsForSet().add(redisKey,"关羽");
                redisOperations.opsForSet().add(redisKey,"张飞");

                System.out.println(redisOperations.opsForSet().members(redisKey));

                // 提交事务
                return redisOperations.exec();
            }
        });
        System.out.println(obj);
    }

    /**
     * 测试 redis hyperloglog 数据类型
     */
    @Test
    public void testHyperLogLog() {
        String redisKey = "test:hll:01";

        for (int i = 1; i < 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }

        for (int i = 1; i < 100000; i++) {
            int r = (int) (Math.random()*100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }

        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }

    /**
     * 测试 redis hyperloglog 数据类型
     */
    @Test
    public void testHyperLogLogUnions() {
        String redisKey02 = "test:hll:02";
        for (int i = 1; i < 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey02, i);
        }

        String redisKey03 = "test:hll:04";
        for (int i = 5001; i <15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey03,i);
        }

        String redisKey04 = "test:hll:04";
        for (int i = 10001; i <20000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey04, i);
        }

        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey02, redisKey03, redisKey04);
        Long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }

    /**
     * 统计 bit 数组中有多少个为true
     */
    @Test
    public void testBitMap01() {
        String redisKey = "test:bm:01";
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,2,false);
        redisTemplate.opsForValue().setBit(redisKey,3,true);

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

    }

    @Test
    public void testBitMap02() {
        String redisKey02 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey02, 1, true);
        redisTemplate.opsForValue().setBit(redisKey02, 2, false);
        redisTemplate.opsForValue().setBit(redisKey02, 3, true);

        String redisKey03 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey03, 3, true);
        redisTemplate.opsForValue().setBit(redisKey03, 4, false);
        redisTemplate.opsForValue().setBit(redisKey03, 5, true);

        String redisKey04 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey04, 5, true);
        redisTemplate.opsForValue().setBit(redisKey04, 6, true);
        redisTemplate.opsForValue().setBit(redisKey04, 7, true);

        String redisKey = "test:bm:op";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR, redisKey.getBytes(),
                        redisKey02.getBytes(), redisKey03.getBytes(), redisKey04.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey02, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey02, 2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey02, 3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey03, 3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey03, 4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey03, 5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey04, 5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey04, 6));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey04, 7));
    }

}
