package com.sky.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopingCartService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "用户端购物车接口")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShopingCartService shopingCartService;

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车，商品信息维：{}",shoppingCartDTO);
        shopingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }
    @ApiOperation("减少购物车")
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("减少购物车，商品信息维：{}",shoppingCartDTO);
        shopingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("查询购物车数据")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shopingCartService.showShppingCart();
        return Result.success(list);
    }
    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result clean(){
        shopingCartService.cleanShoppingCart();
        return Result.success();
    }
}
