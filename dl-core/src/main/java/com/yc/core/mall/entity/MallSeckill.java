package com.yc.core.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 功能描述:
 *
 * @Author: xieyc
 * @Date: 2020-06-01
 * @Version: 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MallSeckill implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "mall_seckill_id", type = IdType.ASSIGN_UUID)
    private String mallSeckillId;
    /**
     * 秒杀名称
     */
    private String title;
    /**
     * 商品ID
     */
    private String mallProductId;
    /**
     * 商品名称
     */
    private String mallProductName;
    /**
     * 库存
     */
    private Long stock;
    /**
     * 秒杀开启时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime seckillStartTime;
    /**
     * 秒杀结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime seckillEndTime;
    /**
     * 删除状态(0:未删除 1:已删除)
     */
    private Integer delFlag;
    /**
     * 创建人ID
     */
    private String createUserId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    /**
     * 修改人
     */
    private String updateUserId;

}
