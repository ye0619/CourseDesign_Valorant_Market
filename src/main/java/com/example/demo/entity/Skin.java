package com.example.demo.entity;

import lombok.Data;

@Data
public class Skin {
    private String uuid;
    private String displayName;
    private String displayIcon;
    private String tierName;
    private String category; // 大类 (rifle, pistol...)
    private String weapon;   // 小类 (vandal, phantom...)
}