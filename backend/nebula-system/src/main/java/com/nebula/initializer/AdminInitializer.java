package com.nebula.initializer;

import com.nebula.config.AdminInitConfig;
import com.nebula.service.UserService;
import com.nebula.system.vo.req.UserReq;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 管理员初始化器
 * 系统启动时自动创建管理员用户
 */
@AllArgsConstructor
@Component
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final AdminInitConfig adminInitConfig;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if (!adminInitConfig.isEnable()) {
            log.info("管理员初始化功能已禁用，跳过初始化");
            return;
        }

        log.info("开始初始化管理员用户...");

        try {
            // 检查管理员是否已存在
            boolean adminExists = userService.existsByUsername(adminInitConfig.getUsername());
            
            if (adminExists) {
                log.info("管理员用户已存在，跳过初始化");
                return;
            }

            // 创建管理员用户请求对象
            UserReq adminUserReq = new UserReq();
            adminUserReq.setUsername(adminInitConfig.getUsername());
            adminUserReq.setPassword(adminInitConfig.getPassword()); // 密码会在服务层加密
            adminUserReq.setPhone(adminInitConfig.getPhone());
            adminUserReq.setIsAdmin(true); // 设置为管理员

            // 保存管理员用户
            userService.saveUser(adminUserReq);
            
            log.info("管理员用户初始化成功");
            log.info("用户名: {}", adminInitConfig.getUsername());
            log.info("密码: {}", adminInitConfig.getPassword());
            log.info("手机号: {}", adminInitConfig.getPhone());

        } catch (Exception e) {
            log.error("初始化管理员用户失败: {}", e.getMessage(), e);
        }
    }
}