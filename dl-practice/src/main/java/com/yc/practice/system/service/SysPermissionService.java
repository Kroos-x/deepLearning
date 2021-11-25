package com.yc.practice.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yc.core.system.entity.SysPermission;
import com.yc.core.system.model.query.PermissionQuery;
import com.yc.core.system.model.vo.SysPermissionTree;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 功能描述:
 *
 * @Author: xieyc
 * @Date 2019-09-20
 * @Version: 1.0.0
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 获取用户权限码 例如：admin,guest,xxx
     *
     * @param loginName 登录名称
     * @return 权限码
     */
    List<String> getUserPerm(String loginName);

    /**
     * 根据Token获取用户拥有的权限
     *
     * @param token
     * @param response
     * @return
     */
    JSONObject getUserPermissionByToken(String token, HttpServletResponse response);

    /**
     * 加载全部权限
     *
     * @return
     */
    List<SysPermissionTree> permissionlist();

    /**
     * 获取全部的权限树
     *
     * @return
     */
    Map<String, Object> queryTreeList(PermissionQuery query);

    /**
     * 添加
     *
     * @param sysPermission
     * @return
     */
    void savePermission(SysPermission sysPermission);

    /**
     * 删除
     *
     * @param id
     */
    void deletePermission(String id);

    /**
     * 批量删除
     *
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 菜单权限树
     *
     * @return
     */
    Map<String, Object> permissionMapTree();

}
