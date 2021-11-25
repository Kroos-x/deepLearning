package com.yc.practice.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.yc.common.constant.CommonConstant;
import com.yc.core.system.entity.SysPermission;
import com.yc.core.system.model.query.PermissionQuery;
import com.yc.core.system.model.vo.SysPermissionTree;
import com.yc.practice.common.log.WriteLog;
import com.yc.practice.system.service.SysPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 功能描述:权限控制器 (菜单 按钮)
 *
 * @Author: xieyc
 * @Date: 2019-09-20
 * @Version: 1.0.0
 */
@RestController
@RequestMapping("/sysPermission")
@Slf4j
@Api(tags = "系统权限")
public class SysPermissionController {

    private final SysPermissionService service;

    @Autowired
    public SysPermissionController(SysPermissionService service) {
        this.service = service;
    }

    @GetMapping(value = "/getUserPermissionByToken")
    @ApiOperation(value = "获取用户权限", notes = "根据Token获取用户拥有的权限")
    @WriteLog(opPosition = "获取指定用户权限")
    public JSONObject getUserPermissionByToken(String token, HttpServletResponse response) {
        return service.getUserPermissionByToken(token, response);
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "查询全部权限", notes = "查询全部权限")
    @WriteLog(opPosition = "查询全部权限")
    public List<SysPermissionTree> list() {
        return service.permissionlist();
    }

    @GetMapping(value = "/queryTreeList")
    @ApiOperation(value = "获取权限树", notes = "获取权限树")
    @WriteLog(opPosition = "获取权限树")
    public Map<String, Object> queryTreeList(PermissionQuery query) {
        return service.queryTreeList(query);
    }

    @PostMapping
    @ApiOperation(value = "添加菜单/更新", notes = "添加菜单/更新")
    @WriteLog(opPosition = "添加菜单/更新", optype = CommonConstant.OPTYPE_CREATE)
    public void savePermission(@RequestBody SysPermission permission) {
        service.savePermission(permission);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除菜单", notes = "删除菜单")
    @WriteLog(opPosition = "删除菜单", optype = CommonConstant.OPTYPE_DELETE)
    public void delete(String sysPermissionId) {
        service.deletePermission(sysPermissionId);
    }

    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除菜单", notes = "批量删除菜单")
    @WriteLog(opPosition = "批量删除菜单", optype = CommonConstant.OPTYPE_DELETE)
    public void deleteBatch(String ids) {
        service.deleteBatch(ids);
    }

    @GetMapping(value = "/permissionMapTree")
    @ApiOperation(value = "菜单权限树", notes = "菜单权限树")
    @WriteLog(opPosition = "查看菜单权限树")
    public Map<String, Object> permissionMapTree() {
        return service.permissionMapTree();
    }

}
