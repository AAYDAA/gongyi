package com.gongyi.platform.security;

import com.gongyi.platform.domain.entity.User;
import com.gongyi.platform.domain.enums.UserStatus;
import com.gongyi.platform.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username)
                .or(() -> userMapper.findByEmail(username))
                .or(() -> userMapper.findByPhone(username))
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        return new SecurityUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                user.getStatus() == UserStatus.ACTIVE
        );
    }
}
