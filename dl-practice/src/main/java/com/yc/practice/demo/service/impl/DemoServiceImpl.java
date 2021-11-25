package com.yc.practice.demo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yc.common.properties.EncodeProperties;
import com.yc.core.demo.entity.Demo;
import com.yc.core.demo.mapper.DemoMapper;
import com.yc.core.demo.model.DemoQuery;
import com.yc.practice.demo.service.DemoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 功能描述:
 *
 * @Author: xieyc && 紫色年华
 * @Date 2020-04-17
 * @Version: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {

    private final RedisTemplate<String, String> redisTemplate;
    private EncodeProperties encodeProperties;

    public DemoServiceImpl(RedisTemplate<String, String> redisTemplate, EncodeProperties encodeProperties) {
        this.redisTemplate = redisTemplate;
        this.encodeProperties = encodeProperties;
    }

    @Override
    public Page<Demo> demoPage(Page<Demo> page, DemoQuery query) {
        return this.baseMapper.selectPage(page, Wrappers.<Demo>lambdaQuery()
                .orderByAsc(Demo::getSort)
        );
    }

    @Override
    public void saveDemo(Demo demo) {
        if (StringUtils.isBlank(demo.getDemoId())) {
            baseMapper.insert(demo);
        } else {
            baseMapper.updateById(demo);
        }
    }

    @Override
    public void deleteAlone(String demoId) {
        this.baseMapper.deleteById(demoId);
    }

    @Override
    public void dd() {
        System.out.println(encodeProperties.getAesKey());
        encodeProperties.setAesKey("28923892379423892979789");
        System.out.println(encodeProperties.getAesKey());
    }


}
