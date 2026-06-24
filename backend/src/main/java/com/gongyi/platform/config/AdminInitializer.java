package com.gongyi.platform.config;

import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.domain.enums.UserStatus;
import com.gongyi.platform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Configuration
public class AdminInitializer {

    @Bean
    public ApplicationRunner initAdminUser(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            @Value("${app.init-admin.enabled:false}") boolean enabled,
            @Value("${app.init-admin.username:admin}") String username,
            @Value("${app.init-admin.password:admin123456}") String password,
            @Value("${app.init-admin.email:admin@example.com}") String email
    ) {
        return args -> {
            if (!enabled) return;
            if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) return;
            if (userMapper.findByUsername(username).isPresent()) return;

            LocalDateTime now = LocalDateTime.now();
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(StringUtils.hasText(email) ? email : null);
            user.setRole(UserRole.ADMIN);
            user.setStatus(UserStatus.ACTIVE);
            user.setRegisterTime(now);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            userMapper.insert(user);
        };
    }
}
