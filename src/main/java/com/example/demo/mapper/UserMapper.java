package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users(username, password) VALUES(#{username}, #{password})")
    void register(User user);

    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password}")
    User login(@Param("username") String username, @Param("password") String password);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    @Update("UPDATE users SET balance = balance + #{amount} WHERE id = #{userId}")
    int updateBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);
}
