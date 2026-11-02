package com.example.demo3.controller;

import com.example.demo3.common.Result;
import com.example.demo3.common.enums.ResultCode;
import com.example.demo3.dto.LoginDTO;
import com.example.demo3.entity.User;
import com.example.demo3.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.demo3.common.page.PageInfo;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    private Long getCurrentUserId(HttpServletRequest request){
        return (Long) request.getAttribute("userId");
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        log.info("查询全部用户列表");
        return userService.selectAllUser();
    }

    @GetMapping("/admin/getAll")
    public Result<List<User>> adminGetAll(HttpServletRequest request){
        Integer role = (Integer) request.getAttribute("role");
        log.info("管理员查询全部用户，当前访问者角色：{}", role);
        if(!Integer.valueOf(1).equals(role)){
            log.warn("权限不足，用户角色{}尝试访问管理员接口", role);
            return Result.fail(ResultCode.FORBIDDEN);
        }
        return userService.selectAllUser();
    }

    @GetMapping("/withOrder")
    public Result<List<User>> getUserWithOrder() {
        log.info("查询用户关联订单数据");
        return userService.selectUserWithOrder();
    }

    @GetMapping("/page")
    public Result<PageInfo<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    ){
        log.info("用户分页查询，pageNum:{},pageSize:{}", pageNum, pageSize);
        return userService.selectUserByPage(pageNum, pageSize);
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO loginDTO){
        String username = loginDTO.getUsername();
        log.info("用户发起登录请求，用户名：{}", username);
        try {
            String password = loginDTO.getPassword();
            Result<?> result = userService.login(username, password);
            log.info("用户【{}】登录请求处理完成", username);
            return result;
        } catch (Exception e){
            log.error("用户【{}】登录发生异常", username, e);
            return Result.error("登录失败");
        }
    }

    @PostMapping("/testAi")
    public Result<String> test(){
        log.info("测试AI接口调用");
        return Result.success("接口通了");
    }

    @PostMapping("/add")
    public Result add(@Valid @RequestBody User user, HttpServletRequest request){
        Long operateUserId = getCurrentUserId(request);
        log.info("新增用户，操作人userId={}, 新增用户名={}", operateUserId, user.getUsername());
        try {
            userService.save(user);
            log.info("新增用户成功，新增用户id={}", user.getId());
            return Result.success();
        } catch (Exception e){
            log.error("新增用户异常，操作人userId={}", operateUserId, e);
            return Result.error("新增用户失败");
        }
    }

    @PutMapping("/update")
    public Result update(@Valid @RequestBody User user, HttpServletRequest request){
        Long operateUserId = getCurrentUserId(request);
        log.info("修改用户，操作人userId={}, 修改用户id={}", operateUserId, user.getId());
        try {
            userService.updateById(user);
            log.info("修改用户成功");
            return Result.success();
        } catch (Exception e){
            log.error("修改用户异常，操作人userId={}, 用户id={}", operateUserId, user.getId(), e);
            return Result.error("修改用户失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request){
        Long operateUserId = getCurrentUserId(request);
        log.info("删除用户，操作人userId={}, 待删除用户id={}", operateUserId, id);
        try {
            userService.removeById(id);
            log.info("删除用户成功");
            return Result.success();
        } catch (Exception e){
            log.error("删除用户异常，操作人userId={}, 用户id={}", operateUserId, id, e);
            return Result.error("删除用户失败");
        }
    }
}