package com.yc.practice.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yc.common.constant.CommonEnum;
import com.yc.common.global.error.Error;
import com.yc.common.global.error.ErrorException;
import com.yc.core.cascade.CaseTopLevel;
import com.yc.core.system.entity.SysPermission;
import com.yc.core.system.mapper.SysPermissionMapper;
import com.yc.core.system.model.query.PermissionQuery;
import com.yc.core.system.model.vo.SysPermissionTree;
import com.yc.core.system.model.vo.TreeModel;
import com.yc.practice.common.UserUtil;
import com.yc.practice.system.service.SysPermissionService;
import com.yc.practice.system.utils.PermissionOPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述:
 *
 * @Author: xieyc
 * @Date: 2019-09-20
 * @Version: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Override
    public List<String> getUserPerm(String loginName) {
        List<String> list = new ArrayList<>();
        List<SysPermission> permissions = this.baseMapper.queryPermissionByUser(loginName);
        permissions.forEach(i -> list.add(i.getPermsCode()));
        return list;
    }

    @Override
    public JSONObject getUserPermissionByToken(String token, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        if (StringUtils.isEmpty(token)) {
            throw new ErrorException(Error.ParameterNotFound);
        }
        List<SysPermission> metaList = this.baseMapper.queryPermissionByUser(UserUtil.getUser().getLoginName());
        PermissionOPUtil.addIndexPage(metaList);
        JSONArray menujsonArray = new JSONArray();
        this.getMenuJsonArray(menujsonArray, metaList, null);
        JSONArray authjsonArray = new JSONArray();
        this.getAuthJsonArray(authjsonArray, metaList);
        // 查询所有的权限
        List<SysPermission> allAuthList = this.baseMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getDelFlag, 0)
                .eq(SysPermission::getMenuType, 2)
        );
        JSONArray allauthjsonArray = new JSONArray();
        this.getAllAuthJsonArray(allauthjsonArray, allAuthList);
        json.put("menu", menujsonArray);
        json.put("auth", authjsonArray);
        json.put("allAuth", allauthjsonArray);
        return json;
    }

    /**
     * @param jsonArray  组装的json
     * @param metaList   当前人拥有的菜单权限
     * @param parentJson 父级菜单json对象
     * @DESC：组装前端菜单JSON数组 1、遍历第一条菜单权限信息，此时parentJson为null
     * 2、如果是一级菜单，执行@1(封装成json对象),执行@2(添加到jsonArray,继续调用getMenuJsonArray方法,并将完整metaList及封装的json对象传入)
     * 然后遍历所有metaList,如果是一级菜单不执行任何操作,如果是二级菜单并且此parentJson的ID等于当前菜单的父ID,
     * 执行@3(将子菜单组装到children中，将按钮组装到meta->permissionList中,如果不是叶子节点,会继续调用getMenuJsonArray方法封装其下级菜单)
     * 3、如果是二级菜单或三级菜单，不执行任何操作(在一级菜单走完后会循环放入二级三级菜单)
     * <p>
     * menuType: 类型( 0：一级菜单 1：子菜单 2：按钮 )
     */
    private void getMenuJsonArray(JSONArray jsonArray, List<SysPermission> metaList, JSONObject parentJson) {
        for (SysPermission permission : metaList) {
            if (permission.getMenuType() == null) {
                continue;
            }
            String tempPid = permission.getParentId();
            // @1
            JSONObject json = getMenuJsonObject(permission);
            if (json == null) {
                continue;
            }
            // @2
            if (parentJson == null && StringUtils.isEmpty(tempPid)) {
                jsonArray.add(json);
                if (!permission.getIsLeaf()) {
                    getMenuJsonArray(jsonArray, metaList, json);
                }
                // @3
            } else if (parentJson != null && StringUtils.isNotEmpty(tempPid) && tempPid.equals(parentJson.getString("id"))) {
                if (permission.getMenuType() == 2) {
                    JSONObject metaJson = parentJson.getJSONObject("meta");
                    if (metaJson.containsKey("permissionList")) {
                        metaJson.getJSONArray("permissionList").add(json);
                    } else {
                        JSONArray permissionList = new JSONArray();
                        permissionList.add(json);
                        metaJson.put("permissionList", permissionList);
                    }
                } else if (permission.getMenuType() == 1 || permission.getMenuType() == 0) {
                    if (parentJson.containsKey("children")) {
                        parentJson.getJSONArray("children").add(json);
                    } else {
                        JSONArray children = new JSONArray();
                        children.add(json);
                        parentJson.put("children", children);
                    }

                    if (!permission.getIsLeaf()) {
                        getMenuJsonArray(jsonArray, metaList, json);
                    }
                }
            }
        }
    }

    /**
     * @param permission
     * @return
     * @DESC：单条权限信息拼装JSON对象 类型(0 ： 一级菜单 1 ： 子菜单 2 ： 按钮)
     */
    private JSONObject getMenuJsonObject(SysPermission permission) {
        JSONObject json = new JSONObject();
        if (permission.getMenuType() == 2) {
            return null;
        } else if (permission.getMenuType() == 0 || permission.getMenuType() == 1) {
            json.put("id", permission.getSysPermissionId());
            if (permission.getIsRoute()) {
                // 表示生成路由
                json.put("route", "1");
            } else {
                // 表示不生成路由
                json.put("route", "0");
            }
            json.put("path", permission.getUrl());
            // 重要规则：路由name (通过URL生成路由name,路由name供前端开发，页面跳转使用)
            json.put("name", urlToRouteName(permission.getUrl()));
            json.put("component", permission.getComponent());
            JSONObject meta = new JSONObject();
            // 默认所有的菜单都加路由缓存，提高系统性能
            meta.put("keepAlive", "true");
            meta.put("title", permission.getName());
            if (StringUtils.isEmpty(permission.getParentId())) {
                // 一级菜单跳转地址
                if (StringUtils.isNotEmpty(permission.getIcon())) {
                    meta.put("icon", permission.getIcon());
                }
            } else {
                if (StringUtils.isNotEmpty(permission.getIcon())) {
                    meta.put("icon", permission.getIcon());
                }
            }
            if (permission.getIsHidden()) {
                json.put("hidden", true);
            }
            json.put("meta", meta);
        }
        return json;
    }


    /**
     * 组装前端按钮权限JSON数组——当前人的权限按钮
     *
     * @param jsonArray
     * @param metaList
     */
    private void getAuthJsonArray(JSONArray jsonArray, List<SysPermission> metaList) {
        for (SysPermission permission : metaList) {
            if (permission.getMenuType() == null) {
                continue;
            }
            JSONObject json = null;
            if (permission.getMenuType() == 2) {
                json = new JSONObject();
                json.put("action", permission.getPermsCode());
                json.put("describe", permission.getName());
                jsonArray.add(json);
            }
        }
    }

    /**
     * 组装前端按钮权限JSON数组——所有按钮
     *
     * @param jsonArray
     * @param allList
     */
    private void getAllAuthJsonArray(JSONArray jsonArray, List<SysPermission> allList) {
        JSONObject json;
        for (SysPermission permission : allList) {
            json = new JSONObject();
            json.put("action", permission.getPermsCode());
            json.put("describe", permission.getName());
            jsonArray.add(json);
        }
    }

    /**
     * @param url
     * @return
     * @DESC：通过URL生成路由name 去掉URL前缀斜杠，替换内容中的斜杠‘/’为-）
     * 举例： URL = /isystem/role RouteName = isystem-role
     */
    private String urlToRouteName(String url) {
        if (StringUtils.isNotEmpty(url)) {
            if (url.startsWith("/")) {
                url = url.substring(1);
            }
            url = url.replace("/", "-");
            url = url.replace(":", "@");
            return url;
        } else {
            return null;
        }
    }

    @Override
    public List<SysPermissionTree> permissionlist() {
        List<SysPermission> list = this.baseMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getDelFlag, CommonEnum.DelFlag.NO_DEL.getCode())
                .orderByAsc(SysPermission::getSort)
        );
        List<SysPermissionTree> treeList = new ArrayList<>();
        this.getTreeList(treeList, list, null);
        return treeList;
    }

    @Override
    public Map<String, Object> queryTreeList(PermissionQuery query) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        // 全部权限id
        List<String> ids = new ArrayList<>();
        List<SysPermission> list = this.baseMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getDelFlag, CommonEnum.DelFlag.NO_DEL.getCode())
                .eq(SysPermission::getMenuType, query.getMenuType())
                .orderByAsc(SysPermission::getSort)
        );
        for (SysPermission curr : list) {
            ids.add(curr.getSysPermissionId());
        }
        //全部树节点
        List<TreeModel> treeList = new ArrayList<>();
        getTreeModelList(treeList, list, null);
        resMap.put("treeList", treeList);
        resMap.put("ids", ids);
        return resMap;
    }

    @Override
    public void savePermission(SysPermission sysPermission) {
        if (StringUtils.isNotBlank(sysPermission.getSysPermissionId())) {
            sysPermission = PermissionOPUtil.intelligentProcessData(sysPermission);
            SysPermission oldPer = this.getById(sysPermission.getSysPermissionId());
            String code = sysPermission.getUrl().substring(1).replace("/", ":").toUpperCase();
            int codeCount = this.baseMapper.selectCount(new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getMenuType, sysPermission.getMenuType())
                    .eq(SysPermission::getPermsCode, code)
                    .ne(SysPermission::getSysPermissionId, oldPer.getSysPermissionId())
            );
            if (codeCount > 0) {
                throw new ErrorException(Error.URLNotUnique);
            }
            sysPermission.setPermsCode(code);
            if (oldPer == null) {
                throw new ErrorException(Error.PermissionNotFound);
            } else {
                //----------------------------------------------------------------------
                // Step1.判断是否是一级菜单，是的话清空父菜单ID
                if (CommonEnum.MenuType.TOP_MENU_TYPE.getCode().equals(sysPermission.getMenuType())) {
                    sysPermission.setParentId(null);
                }
                // Step2.判断菜单下级是否有菜单，无则设置为叶子节点
                int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, sysPermission.getSysPermissionId()));
                if (count == 0) {
                    sysPermission.setIsLeaf(true);
                }
                //----------------------------------------------------------------------
                this.updateById(sysPermission);
                // 如果当前菜单的父菜单变了，则需要修改新父菜单和老父菜单的，叶子节点状态
                String newPid = sysPermission.getParentId();
                if ((StringUtils.isNotEmpty(newPid) && !newPid.equals(oldPer.getParentId())) || StringUtils.isEmpty(newPid) && StringUtils.isNotEmpty(oldPer.getParentId())) {
                    // a.设置新的父菜单不为叶子节点
                    this.baseMapper.setMenuLeaf(newPid, 0);
                    // b.判断老的菜单下是否还有其他子菜单，没有的话则设置为叶子节点
                    int cc = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, oldPer.getParentId()));
                    if (cc == 0) {
                        if (StringUtils.isNotEmpty(oldPer.getParentId())) {
                            this.baseMapper.setMenuLeaf(oldPer.getParentId(), 1);
                        }
                    }
                }
            }
        } else {
            sysPermission = PermissionOPUtil.intelligentProcessData(sysPermission);
            String code = sysPermission.getUrl().substring(1).replace("/", ":").toUpperCase();
            int count = this.baseMapper.selectCount(new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getMenuType, sysPermission.getMenuType())
                    .eq(SysPermission::getPermsCode, code)
            );
            if (count > 0) {
                throw new ErrorException(Error.URLNotUnique);
            }
            sysPermission.setPermsCode(code);
            // 菜单等级
            if (CommonEnum.MenuType.TOP_MENU_TYPE.getCode().equals(sysPermission.getMenuType())) {
                sysPermission.setParentId(null);
            }
            // 设置父节点不为叶子节点
            if (StringUtils.isNotEmpty(sysPermission.getParentId())) {
                this.baseMapper.setMenuLeaf(sysPermission.getParentId(), 0);
            }
            sysPermission.setIsLeaf(true);
            sysPermission.setCreateUserId(UserUtil.getUser().getSysUserId());
            this.save(sysPermission);
            if (CommonEnum.MenuType.SECOND_MENU_TYPE.getCode().equals(sysPermission.getMenuType())) {
                this.addDefaultPermission(sysPermission);
            }
        }
    }

    @Override
    public void deletePermission(String id) {
        SysPermission sysPermission = this.getById(id);
        if (sysPermission == null) {
            throw new ErrorException(Error.PermissionNotFound);
        }
        String pid = sysPermission.getParentId();
        if (StringUtils.isNotEmpty(pid)) {
            int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
            if (count == 1) {
                // 若父节点无其他子节点，则该父节点是叶子节点
                this.baseMapper.setMenuLeaf(pid, 1);
            }
        }
        // 删除子节点
        this.removeChildren(sysPermission.getSysPermissionId());
        // 删除节点
        sysPermission.setDelFlag(CommonEnum.DelFlag.DEL.getCode());
        this.baseMapper.updateById(sysPermission);
    }

    @Override
    public void deleteBatch(String ids) {
        String[] arr = ids.split(",");
        for (String id : arr) {
            if (StringUtils.isNotEmpty(id)) {
                this.deletePermission(id);
            }
        }
    }

    @Override
    public Map<String, Object> permissionMapTree() {
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<String> ids = new ArrayList<>();
        List<CaseTopLevel> list = this.baseMapper.caseList();
        // 全部树节点数据
        resMap.put("treeList", list);
        for (CaseTopLevel sysPer : list) {
            ids.add(sysPer.getId());
        }
        // 全部树ids
        resMap.put("ids", ids);
        return resMap;
    }

    /**
     * 组装菜单树 ====== permissionlist 子方法 ========
     *
     * @param treeList
     * @param metaList
     * @param temp
     */
    private void getTreeList(List<SysPermissionTree> treeList, List<SysPermission> metaList, SysPermissionTree temp) {
        for (SysPermission permission : metaList) {
            String tempPid = permission.getParentId();
            SysPermissionTree tree = new SysPermissionTree(permission);
            if (temp == null && StringUtils.isEmpty(tempPid)) {
                treeList.add(tree);
                if (!tree.isLeaf()) {
                    getTreeList(treeList, metaList, tree);
                }
            } else if (temp != null && tempPid != null && tempPid.equals(temp.getId())) {
                temp.getChildren().add(tree);
                if (!tree.isLeaf()) {
                    getTreeList(treeList, metaList, tree);
                }
            }
        }
    }

    /**
     * ======= permissionMapTree 子方法 =======
     *
     * @param treeList
     * @param metaList
     * @param temp
     */
    private void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp) {
        for (SysPermission permission : metaList) {
            String tempPid = permission.getParentId();
            TreeModel tree = new TreeModel(permission);
            if (temp == null && StringUtils.isEmpty(tempPid)) {
                treeList.add(tree);
                if (!tree.isLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            } else if (temp != null && tempPid != null && tempPid.equals(temp.getKey())) {
                temp.getChildren().add(tree);
                if (!tree.isLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            }
        }
    }

    /**
     * 根据父id删除其关联的子节点数据 ****** 子方法 *****
     */
    private void removeChildren(String parentId) {
        // 查出该主键下的所有子级
        List<SysPermission> permissionList = this.list(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getParentId, parentId)
        );
        if (CollectionUtils.isNotEmpty(permissionList)) {
            // 如果查出的集合不为空, 则先删除所有
            SysPermission sysPermission = new SysPermission();
            sysPermission.setDelFlag(CommonEnum.DelFlag.DEL.getCode());
            this.update(sysPermission, new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getParentId, parentId)
            );
            // 遍历, 根据每个对象,查找其是否仍有子级
            permissionList.forEach(item -> {
                String id = item.getSysPermissionId();
                int num = this.count(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id));
                // 有子级, 则递归
                if (num > 0) {
                    this.removeChildren(id);
                }
            });
        }
    }


    /**
     * ****** 子方法 ***** 初始化二级菜单按钮权限
     *
     * @param sysPermission 二级菜单信息
     */
    private void addDefaultPermission(SysPermission sysPermission) {
        String[] permissionArr = new String[]{"ADD", "UPDATE", "DEL", "QUERY"};
        for (int i = 0; i < permissionArr.length; i++) {
            SysPermission buttonPermission = new SysPermission();
            buttonPermission.setParentId(sysPermission.getSysPermissionId());
            buttonPermission.setName(CommonEnum.ButtonName.getEnumByCode(i));
            // isystem:user:add
            String code = (sysPermission.getUrl().substring(1) + ":" + permissionArr[i]).replace("/", ":");
            buttonPermission.setPermsCode(code.toUpperCase());
            buttonPermission.setMenuType(2);
            buttonPermission.setSort(i);
            buttonPermission.setIsRoute(false);
            buttonPermission.setIsLeaf(true);
            buttonPermission.setKeepAlive(false);
            buttonPermission.setIsHidden(false);
            buttonPermission.setDelFlag(0);
            buttonPermission.setCreateUserId(UserUtil.getUserId());
            this.baseMapper.insert(buttonPermission);
        }
        //设置父节点不为叶子节点
        this.baseMapper.setMenuLeaf(sysPermission.getSysPermissionId(), 0);
    }

}
