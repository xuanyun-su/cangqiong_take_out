package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sky.entity.SetmealDish;

@Mapper
public interface SetmealDishMapper {
// 根据id查询套餐id
    List<Long> getSetmealDishIds(List<Long> dishids);

    void insertBatch(List<SetmealDish> setmealDishes);

}
