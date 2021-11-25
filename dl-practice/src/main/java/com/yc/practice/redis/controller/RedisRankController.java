package com.yc.practice.redis.controller;

import com.yc.practice.redis.service.RedisRankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * 功能描述:排行榜 暴露接口 API
 *
 * @Author: xieyc
 * @Date: 2020-02-01
 * @Version: 1.0.0
 */
@RestController
@RequestMapping("/redisPractice/redisRank")
@Slf4j
public class RedisRankController {

    private final RedisRankService iRedisRankService;

    @Autowired
    public RedisRankController(RedisRankService iRedisRankService) {
        this.iRedisRankService = iRedisRankService;
    }

    /**
     * 初始化数据
     */
    @PostMapping("/initRankData")
    public void initRankData() {
        iRedisRankService.initRankData();
    }

    /**
     * 获取数据
     *
     * @return set
     */
    @GetMapping("/getData")
    public Set getData() {
        return iRedisRankService.getData();
    }

    /**
     * 清除数据
     */
    @PostMapping("/clearData")
    public void clearData() {
        iRedisRankService.clearData();
    }

    /**
     * 获取总成绩排行榜top10
     *
     * @return set
     */
    @GetMapping("/scoreTop10")
    public Set top10(String type) {
        return iRedisRankService.top10(type);
    }

    /**
     * 新增一名学生的成绩到排行榜中
     */
    @PostMapping("/add")
    public void add() {
        iRedisRankService.add();
    }

    /**
     * 查询指定人的排名和分数
     *
     * @return
     */
    @GetMapping("/userInfo")
    public Map userInfo() {
        return iRedisRankService.userInfo();
    }

    /**
     * .统计分数区间人数
     *
     * @return
     */
    @GetMapping("/scopeCount")
    public Long scopeCount() {
        return iRedisRankService.scopeCount();
    }

    /**
     * 使用加法操作分数
     * 直接在原有的score上使用加法;
     * 如果没有这个元素，则会创建，并且score初始为0.再使用加法
     *
     * @return
     */
    @PostMapping("/addScore")
    public void addScore() {
        iRedisRankService.addScore();
    }
}
