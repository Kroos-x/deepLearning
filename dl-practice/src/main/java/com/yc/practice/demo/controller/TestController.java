package com.yc.practice.demo.controller;

import com.yc.core.test.TestParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * 功能描述:导入导出 控制层
 *
 * @Author: xieyc
 * @Date: 2020-02-16
 * @Version: 1.0.0
 */
@RestController
@RequestMapping("/demo/test")
@RequiredArgsConstructor
@Validated
public class TestController {

    @GetMapping
    public void test(@NotBlank(message = "用户名不能为空") String param) {
        System.out.println(param);
    }

}
