package com.example.demo.controller;

import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/trade")
@CrossOrigin(origins = "*")
public class TradeController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/buy")
    public Map<String, Object> buy(@RequestBody Map<String, Long> params) {
        Long buyerId = params.get("buyerId");
        Long listingId = params.get("listingId");

        Map<String, Object> res = new HashMap<>();
        try {
            transactionService.buySkin(buyerId, listingId);
            res.put("code", 200);
            res.put("msg", "购买成功！去库存看看吧");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", e.getMessage()); // 返回具体的错误信息
        }
        return res;
    }
}