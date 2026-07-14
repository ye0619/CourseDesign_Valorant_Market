package com.example.demo.entity;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private BigDecimal balance;
}