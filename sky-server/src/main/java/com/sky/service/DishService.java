package com.sky.service;

import java.util.List;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

public interface DishService {
    // 新增菜品和对应口味
    public void saveWithFlavor(DishDTO dishDTO);

    public PageResult pageQuery(DishPageQueryDTO pageQueryDTO);

    public void deleteBatch(List<Long> ids);

    public DishVO getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDTO dishDTO);

    public void updateStatus(Integer status,Long id);

    public List<Dish> list(Long categoryId);
      /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
