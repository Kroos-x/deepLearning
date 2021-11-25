package com.yc.practice.mall.controller;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yc.core.mall.entity.MallProductCategory;
import com.yc.practice.mall.service.MallProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述:商品类目控制层
 *
 * @Author: xieyc
 * @Date 2020-05-08
 * @Version: 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/mallProductCategory")
public class MallProductCategoryController {

    private final MallProductCategoryService iMallGoodClassService;

    @Autowired
    public MallProductCategoryController(MallProductCategoryService iMallGoodClassService) {
        this.iMallGoodClassService = iMallGoodClassService;
    }

    /**
     * 类目树
     *
     * @return 树
     */
    @GetMapping(value = "/tree")
    public List<Tree<String>> classTree() {
        return iMallGoodClassService.mallProductTree();
    }

    /**
     * 添加/更新类目
     *
     * @param mallProductCategory 类目信息
     */
    @PostMapping
    public void save(@RequestBody MallProductCategory mallProductCategory) {
        iMallGoodClassService.saveProductCategory(mallProductCategory);
    }

    /**
     * 查询子级类目
     *
     * @param page     分页信息
     * @param parentId 父级类别ID
     * @return page
     */
    @GetMapping("/children")
    public Page<MallProductCategory> children(Page<MallProductCategory> page, String parentId) {
        return iMallGoodClassService.children(page, parentId);
    }

    /**
     * 删除类目
     *
     * @param mallProductCategoryId 类目ID
     */
    @DeleteMapping
    public void delete(String mallProductCategoryId) {
        iMallGoodClassService.deleteAlone(mallProductCategoryId);
    }

    /**
     * 类目级联信息
     *
     * @return list
     */
    @GetMapping("/list")
    public List<Tree<String>> list() {
        return iMallGoodClassService.listProductCategory();
    }

}
