package com.example.demo.controller;

import com.example.demo.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
@CrossOrigin(origins = "*")
public class MarketController {

    @Autowired private InventoryMapper inventoryMapper;
    @Autowired private ListingMapper listingMapper;
    @Autowired private OrderMapper orderMapper; // 注入这个

    // 查询库存
    @GetMapping("/inventory/{userId}")
    public List<Map<String, Object>> getMyInventory(@PathVariable Long userId) {
        return inventoryMapper.findByUserId(userId);
    }

    // ✨ 修复：从数据库随机抽取皮肤，保证100%成功
    @PostMapping("/open-case/{userId}")
    public String openCase(@PathVariable Long userId) {
        try {
            String randomUuid = inventoryMapper.getRandomSkinUuid();
            if (randomUuid == null) return "数据库里没有皮肤数据，无法开箱";

            inventoryMapper.addItem(userId, randomUuid);
            return "恭喜！开箱成功，获得了一件新饰品！";
        } catch (Exception e) {
            e.printStackTrace();
            return "开箱失败：" + e.getMessage();
        }
    }

    // 上架
    // 3. 上架商品 (修复版)
    @PostMapping("/sell")
    public Map<String, Object> sellItem(@RequestBody com.example.demo.dto.SellRequest req) {
        Map<String, Object> res = new HashMap<>();
        try {
            // 使用 DTO 对象，类型安全
            listingMapper.addListing(req.getUserId(), req.getInvId(), req.getUuid(), req.getPrice());
            // 修改库存状态为 1 (已上架)
            inventoryMapper.updateStatus(req.getInvId(), 1);

            res.put("code", 200);
            res.put("msg", "上架成功！");
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "上架失败: " + e.getMessage());
        }
        return res;
    }

    // 挂单列表
    @GetMapping("/listings")
    public List<Map<String, Object>> getListings(@RequestParam String uuid) {
        return listingMapper.findBySkinUuid(uuid);
    }

    // ✨ 新增：购买记录接口
    @GetMapping("/orders/{userId}")
    public List<Map<String, Object>> getMyOrders(@PathVariable Long userId) {
        return orderMapper.findMyOrders(userId);
    }
}