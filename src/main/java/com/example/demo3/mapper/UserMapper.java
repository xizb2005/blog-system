package com.example.demo3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo3.entity.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> selectAllUser();
    List<User> selectUserWithOrder();
    User selectByUsername(String username);
}