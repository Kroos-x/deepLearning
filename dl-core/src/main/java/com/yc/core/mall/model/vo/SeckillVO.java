package com.yc.core.mall.model.vo;

import com.yc.core.mall.entity.MallSeckill;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 功能描述：
 * <p>版权所有：</p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 *
 * @Company: 紫色年华
 * @Author: xieyc
 * @Datetime: 2020-06-05
 * @Version: 1.0.0
 */
@Data
public class SeckillVO extends MallSeckill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 凭证
     */
    private String md5;

    /**
     * 系统当前时间
     */
    private LocalDateTime localDateTime;

    /**
     * 状态(0:未开始 1:开始秒杀 2:已结束)
     */
    private String state;

}