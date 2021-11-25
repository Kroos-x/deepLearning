package com.yc.practice.message.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.yc.common.constant.CommonConstant;
import com.yc.common.utils.WebSocketUtil;
import com.yc.practice.common.UserUtil;
import com.yc.practice.message.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 功能描述:
 *
 * @Author: xieyc
 * @Date: 2020-05-12
 * @Version: 1.0.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatServiceImpl implements ChatService {

    private WebSocketUtil webSocket;
    private final RedisTemplate redisTemplate;

    @Autowired
    public ChatServiceImpl(RedisTemplate redisTemplate, WebSocketUtil webSocket) {
        this.redisTemplate = redisTemplate;
        this.webSocket = webSocket;
    }

    @Override
    public List<Object> message(JSONObject jsonObject) {
        /**
         * 0、产生历史第一次聊天信息(创建聊天对象)
         * 1、从聊天对象缓存集合中查询是否存在 ATOB 或者 BTOA
         * 2、有追加，没有新建聊天对象和聊天记录(时间,内容,头像,发送人)
         * 3、推送消息给接收人
         * 4、返回历史聊天记录给发送人
         */
        HashMap<String, Object> map = new HashMap<>();
        map.put("content", jsonObject.getString("content"));
        map.put("headImg", UserUtil.getUser().getHeadImg());
        map.put("userName", UserUtil.getUser().getUsername());
        map.put("time", DateUtil.formatDateTime(new Date()));
        map.put("sysUserId", UserUtil.getUserId());
        String key = UserUtil.getUserId() + "AND" + jsonObject.getString("receiveUserId");
        String key_sub = jsonObject.getString("receiveUserId") + "AND" + UserUtil.getUserId();
        if (redisTemplate.hasKey(CommonConstant.CHAT_OBJECT)) {
            Set<String> chatObject = redisTemplate.opsForSet().members(CommonConstant.CHAT_OBJECT);
            if (chatObject.contains(key_sub)) {
                key = key_sub;
            }
        } else {
            redisTemplate.opsForSet().add(CommonConstant.CHAT_OBJECT, key);
        }
        redisTemplate.opsForList().rightPush(key, map);
        // 推送消息给接收人
        JSONObject obj = new JSONObject();
        obj.put("content", jsonObject.getString("content"));
        webSocket.sendOneMessage(jsonObject.getString("receiveUserId"), obj.toJSONString());
        // 返回聊天记录给发送人
        Long size = redisTemplate.opsForList().size(key);
        return redisTemplate.opsForList().range(key, 0, size);
    }

    @Override
    public List<Object> message(String receiveUserId) {
        String key = UserUtil.getUserId() + "AND" + receiveUserId;
        String key_sub = receiveUserId + "AND" + UserUtil.getUserId();
        if (redisTemplate.hasKey(CommonConstant.CHAT_OBJECT)) {
            Set<String> chatObject = redisTemplate.opsForSet().members(CommonConstant.CHAT_OBJECT);
            if (chatObject.contains(key_sub)) {
                key = key_sub;
            }
            Long size = redisTemplate.opsForList().size(key);
            return redisTemplate.opsForList().range(key, 0, size);
        }
        return null;
    }

}
