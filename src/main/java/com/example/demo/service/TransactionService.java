package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired private UserMapper userMapper;
    @Autowired private ListingMapper listingMapper;
    @Autowired private InventoryMapper inventoryMapper;
    @Autowired private OrderMapper orderMapper;

    /**
     * 购买核心逻辑
     * @Transactional 保证：如果扣了钱但发货失败，钱会自动退回（回滚）
     */
    @Transactional(rollbackFor = Exception.class)
    public void buySkin(Long buyerId, Long listingId) throws Exception {
        // 1. 检查商品是否存在且在售
        Map<String, Object> listing = listingMapper.findListingById(listingId);
        if (listing == null) {
            throw new Exception("商品不存在或已被抢走！");
        }

        Long sellerId = (Long) listing.get("seller_id");
        Long invId = (Long) listing.get("inventory_id");
        BigDecimal price = (BigDecimal) listing.get("price");

        // 2. 不能买自己的
        if (buyerId.equals(sellerId)) {
            throw new Exception("不能购买自己上架的商品！");
        }

        // 3. 检查买家余额
        User buyer = userMapper.findById(buyerId);
        if (buyer.getBalance().compareTo(price) < 0) {
            throw new Exception("余额不足，请充值！");
        }

        // === 开始执行交易 ===

        // 4. 锁定商品状态 (乐观锁防止超卖)
        int rows = listingMapper.markAsSold(listingId);
        if (rows == 0) {
            throw new Exception("手慢了，商品刚被别人买走！");
        }

        // 5. 资金转移
        userMapper.updateBalance(buyerId, price.negate()); // 买家扣钱
        userMapper.updateBalance(sellerId, price);         // 卖家加钱

        // 6. 物品转移
        inventoryMapper.transferItem(invId, buyerId);

        // 7. 生成订单记录
        orderMapper.createOrder(buyerId, sellerId, listingId, price);
    }
}