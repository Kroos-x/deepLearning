package com.yc.core.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 功能描述:商品类别
 *
 * @Author: xieyc
 * @Date: 2020-05-08
 * @Version: 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MallProductCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 类别Id
     */
    @TableId(value = "mall_product_category_id", type = IdType.ASSIGN_UUID)
    private String mallProductCategoryId;
    /**
     * 父类别id
     */
    private String parentId;
    /**
     * 类别名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;

    /**
     * 图标
     */
    private String icon;

    /**
     * 等级
     */
    private Integer level;

    // ============= 非表字段 ==============
    /**
     * 子级类目
     */
    @TableField(exist = false)
    private List<MallProductCategory> children;
}
