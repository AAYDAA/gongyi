package com.gongyi.platform.mapper;

import com.gongyi.platform.domain.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    Optional<User> findById(Long id);

    @Select("SELECT * FROM users WHERE username = #{username}")
    Optional<User> findByUsername(String username);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    Optional<User> findByPhone(String phone);

    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);

    @Select("SELECT * FROM users")
    List<User> findAll();

    @Insert("INSERT INTO users (username, password, phone, email, avatar_url, role, register_time, status, create_time, update_time) " +
            "VALUES (#{username}, #{password}, #{phone}, #{email}, #{avatarUrl}, #{role}, #{registerTime}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET username = #{username}, phone = #{phone}, email = #{email}, avatar_url = #{avatarUrl}, " +
            "role = #{role}, status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int update(User user);
}
