package com.example.demo3.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo3.common.page.PageInfo;
import com.example.demo3.common.Result;
import com.example.demo3.entity.User;
import java.util.List;


public interface UserService extends IService<User> {
    Result<List<User>> selectAllUser();
    Result<List<User>> selectUserWithOrder();
    // pagehelper手写xml分页
    Result<PageInfo<User>> selectUserByPage(Integer pageNum, Integer pageSize);

    Result<?> login(String username, String password);
}