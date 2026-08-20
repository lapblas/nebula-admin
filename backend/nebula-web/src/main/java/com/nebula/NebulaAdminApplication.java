package com.nebula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nebula"})
public class NebulaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NebulaAdminApplication.class, args);
        System.out.println("""
    _   __     __          __         ___       __          _    \s
   / | / /__  / /_  __  __/ /___ _   /   | ____/ /___ ___  (_)___\s
  /  |/ / _ \\/ __ \\/ / / / / __ `/  / /| |/ __  / __ `__ \\/ / __ \\
 / /|  /  __/ /_/ / /_/ / / /_/ /  / ___ / /_/ / / / / / / / / / /
/_/ |_/\\___/_.___/\\__,_/_/\\__,_/  /_/  |_\\__,_/_/ /_/ /_/_/_/ /_/\s                
Nebula Admin 启动成功！""");
    }

}