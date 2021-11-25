package com.yc.core.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 功能描述: 用户信息
 *
 * @Author: xieyc && 紫色年华
 * @Date 2019-09-19
 * @Version: 1.0.0
 * <p>
 * mybatisPlus会默认使用实体类的类名到数据库中找对应的表
 * 也可以通过@TableName(value = "sys_user")来指定在数据库中的名字
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @TableId: value:指定数据库表中主键的列名，如果实体类属性和表中字段一致，可省略
     * type:指定主键策略
     * IdType.AUTO：数据库ID自增
     * IdType.INPUT: 用户输入ID
     * IdType.ID_WORKER:  全局唯一ID，内容为空自动填充（默认配置）
     * IdType.ASSIGN_UUID: 全局唯一ID，内容为空自动填充
     */
    @TableId(value = "sys_user_id", type = IdType.ASSIGN_UUID)
    private String sysUserId;
    /**
     * 真实姓名
     */
    private String userName;
    /**
     * 登录名称
     */
    private String loginName;
    /**
     * 密码
     */
    private String passWord;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 职务
     */
    private String jobs;
    /**
     * 性别(0女 1男 2保密 3未知)
     */
    private Integer sex;
    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate birthday;
    /**
     * 头像
     */
    private String headImg;
    /**
     * 身份证
     */
    private String idCard;
    /**
     * 手机
     */
    private String phone;
    /**
     * 住址
     */
    private String address;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 积分
     */
    private Integer score;
    /**
     * 余额
     */
    private BigDecimal balance;
    /**
     * 序号
     */
    private Integer sort;
    /**
     * 状态(0正常 1 冻结)
     */
    private Integer state;
    /**
     * 删除状态(0:未删除 1:已删除)
     */
    private Integer delFlag;
    /**
     * 备注
     */
    private String remark;
    /**
     * 所属部门
     */
    private String departId;
    /**
     * 创建人
     */
    private String createUserId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改人
     */
    private String updateUserId;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 通过此注解表明不对应数据库中的字段
     */
    @TableField(exist = false)
    private String tempArgs;

    /**
     * 旧密码
     */
    @TableField(exist = false)
    private String oldPassword;

}
