package com.sky.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController("adminController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {
    @Autowired
    RedisTemplate redisTemplate;
    private static final String KEY= "SHOPStatus";
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态{}", status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }
    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer)redisTemplate.opsForValue().get(KEY);
        log.info("获取营业店铺状态{}",status==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
