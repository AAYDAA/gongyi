package com.gongyi.platform.service.impl;

import com.gongyi.platform.common.PageResult;
import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.domain.enums.UserRole;
import com.gongyi.platform.domain.enums.UserStatus;
import com.gongyi.platform.dto.auth.AuthResponse;
import com.gongyi.platform.dto.auth.LoginRequest;
import com.gongyi.platform.dto.auth.RegisterRequest;
import com.gongyi.platform.mapper.UserMapper;
import com.gongyi.platform.security.JwtUtil;
import com.gongyi.platform.service.AuthService;
import com.github.pagehelper.PageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserServiceImpl userService;

    public AuthServiceImpl(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserServiceImpl userService
    ) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (!StringUtils.hasText(request.username()) || !StringUtils.hasText(request.password())) {
            throw new IllegalStateException("用户名或密码不能为空");
        }

        userMapper.findByUsername(request.username()).ifPresent(u -> {
            throw new IllegalStateException("用户名已存在");
        });
        if (StringUtils.hasText(request.phone())) {
            userMapper.findByPhone(request.phone()).ifPresent(u -> {
                throw new IllegalStateException("手机号已存在");
            });
        }
        if (StringUtils.hasText(request.email())) {
            userMapper.findByEmail(request.email()).ifPresent(u -> {
                throw new IllegalStateException("邮箱已存在");
            });
        }

        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setRegisterTime(now);
        user.setCreateTime(now);
        user.setUpdateTime(now);

        userMapper.insert(user);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, userService.toResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        if (!StringUtils.hasText(request.account()) || !StringUtils.hasText(request.password())) {
            throw new IllegalStateException("账号或密码不能为空");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.account(), request.password())
        );

        User user = userMapper.findByUsername(request.account())
                .or(() -> userMapper.findByEmail(request.account()))
                .or(() -> userMapper.findByPhone(request.account()))
                .orElseThrow(() -> new IllegalStateException("用户不存在"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, userService.toResponse(user));
    }
}
