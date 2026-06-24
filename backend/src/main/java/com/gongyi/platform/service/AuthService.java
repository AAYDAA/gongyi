package com.gongyi.platform.service;

import com.gongyi.platform.dto.auth.AuthResponse;
import com.gongyi.platform.dto.auth.LoginRequest;
import com.gongyi.platform.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
