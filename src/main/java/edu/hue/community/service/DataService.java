package edu.hue.community.service;

import java.util.Date;

/**
 * @author 47552
 * @date 2021/10/01
 */
public interface DataService {

    /**
     * 存放单日独立用户
     * @param ip
     */
    void saveUV(String ip);

    /**
     * 得到一段时间内的独立用户数量
     * @param startDate
     * @param endDate
     * @return
     */
    Long getUVCount(Date startDate, Date endDate);

    /**
     * 存放单日活跃用户的key
     * @param userId
     */
    void saveDAU(Integer userId);

    /**
     * 获得一段时间内的活跃用户的数量
     * @param startDate
     * @param endDate
     * @return
     */
    Long getDAUCount(Date startDate, Date endDate);

}
