package com.example.demo.dto; // 注意包名

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SellRequest {
    private Long userId;
    private Long invId;
    private String uuid;
    private BigDecimal price;
}