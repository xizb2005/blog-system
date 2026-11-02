package com.example.demo3.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo3.common.enums.ResultCode;
import com.example.demo3.common.page.PageInfo;
import com.example.demo3.common.Result;
import com.example.demo3.entity.User;
import com.example.demo3.mapper.UserMapper;
import com.example.demo3.service.UserService;
import com.example.demo3.util.JwtUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Override
    public Result<List<User>> selectAllUser() {
        log.info("Service：查询全部用户列表");
        List<User> userList = userMapper.selectAllUser();
        return Result.success(userList);
    }

    @Override
    public Result<List<User>> selectUserWithOrder() {
        log.info("Service：查询用户关联订单数据");
        List<User> userList = userMapper.selectUserWithOrder();
        return Result.success(userList);
    }

    @Override
    public Result<PageInfo<User>> selectUserByPage(Integer pageNum, Integer pageSize) {
        log.info("Service：用户分页查询 pageNum={},pageSize={}", pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectAllUser();
        Page<User> page = (Page<User>) userList;
        PageInfo<User> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        pageInfo.setList(page.getResult());
        return Result.success(pageInfo);
    }

    @Override
    public Result<?> login(String username, String password) {
        log.info("Service：用户登录，用户名={}", username);
        User user = userMapper.selectByUsername(username);
        if(user == null){
            log.warn("Service：登录失败，用户名{}不存在", username);
            return Result.fail(ResultCode.FAIL.getCode(), "用户名不存在");
        }
        if(!user.getPassword().equals(password)){
            log.warn("Service：登录失败，用户名{}密码错误", username);
            return Result.fail(ResultCode.FAIL.getCode(), "密码错误");
        }
        String token = JwtUtil.generateToken(Long.valueOf(user.getId()), user.getRole());
        log.info("Service：用户{}登录成功，生成token", username);
        return Result.success(token);
    }

    @Override
    public boolean save(User user) {
        log.info("Service层新增用户，用户名={}", user.getUsername());
        try {
            boolean result = super.save(user);
            log.info("Service层新增用户成功，用户id={}", user.getId());
            return result;
        } catch (Exception e) {
            log.error("Service层新增用户失败，用户名={}", user.getUsername(), e);
            throw e;
        }
    }

    @Override
    public boolean updateById(User user) {
        log.info("Service层修改用户，用户id={}", user.getId());
        try {
            boolean result = super.updateById(user);
            if(!result){
                log.warn("Service层修改用户，未找到用户记录，用户id={}", user.getId());
            }else{
                log.info("Service层修改用户成功，用户id={}", user.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("Service层修改用户失败，用户id={}", user.getId(), e);
            throw e;
        }
    }

    public boolean removeById(Long id) {
        log.info("Service层删除用户，用户id={}", id);
        try {
            boolean result = super.removeById(id);
            if(!result){
                log.warn("Service层删除用户，未找到用户记录，用户id={}", id);
            }else{
                log.info("Service层删除用户成功，用户id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("Service层删除用户失败，用户id={}", id, e);
            throw e;
        }
    }
}