package com.yc.core.mall.model.form;

import com.yc.core.mall.entity.MallProduct;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 功能描述:购物车FORM
 *
 * @Author: xieyc
 * @Date: 2020-05-17
 * @Version: 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CartForm extends MallProduct {

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 回显商品
     */
    private List<CartForm> goodList;

}
