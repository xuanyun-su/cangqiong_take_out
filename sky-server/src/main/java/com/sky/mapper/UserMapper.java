package com.sky.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.User;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openId);

    void insert(User user);

    // 根据动态条件 来查询用户数量
    Integer countByMap(Map map);
    //TODO 这句话可能不对 
    @Select("select * from user where user_id = #{userId}")
    User getById(Long userId);
    
}
