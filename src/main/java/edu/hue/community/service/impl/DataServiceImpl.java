package edu.hue.community.service.impl;

import edu.hue.community.service.DataService;
import edu.hue.community.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 47552
 * @date 2021/10/01
 */
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 存放单日独立用户
     * @param ip
     */
    @Override
    public void saveUV(String ip) {
        String date = simpleDateFormat.format(new Date());
        // 得到key
        String uvKey = RedisUtils.getUVKey(date);
        // 存放用户ip
        redisTemplate.opsForHyperLogLog().add(uvKey,ip);
    }

    /**
     * 得到一段时间内的独立用户数量
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public Long getUVCount(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        List<String> uvList = new ArrayList<>();
        while (!calendar.getTime().after(endDate)) {
            String key = RedisUtils.getUVKey(simpleDateFormat.format(calendar.getTime()));
            uvList.add(key);
            calendar.add(Calendar.DATE,1);
        }
        String uvKey = RedisUtils.getUVKey(simpleDateFormat.format(startDate), simpleDateFormat.format(endDate));
        redisTemplate.opsForHyperLogLog().union(uvKey, uvList.toArray());
        return redisTemplate.opsForHyperLogLog().size(uvKey);
    }

    /**
     * 存放单日活跃用户的key
     * @param userId
     */
    @Override
    public void saveDAU(Integer userId) {
        String dauKey = RedisUtils.getDAUKey(simpleDateFormat.format(new Date()));
        redisTemplate.opsForValue().setBit(dauKey, userId, true);
    }

    /**
     * 获得一段时间内的活跃用户的数量
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public Long getDAUCount(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        List<byte[]> dauList = new ArrayList<>();
        while (!calendar.getTime().after(endDate)) {
            String key = RedisUtils.getDAUKey(simpleDateFormat.format(calendar.getTime()));
            dauList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }
        String dauKey = RedisUtils.getDAUKey(simpleDateFormat.format(startDate), simpleDateFormat.format(endDate));
        Long count = (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR, dauKey.getBytes(), dauList.toArray(new byte[0][0]));
                return connection.bitCount(dauKey.getBytes());
            }
        });
        return count;
    }
}
