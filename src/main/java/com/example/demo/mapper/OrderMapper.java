package com.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO orders(buyer_id, seller_id, listing_id, price) VALUES(#{buyerId}, #{sellerId}, #{listingId}, #{price})")
    void createOrder(@Param("buyerId") Long buyerId,
                     @Param("sellerId") Long sellerId,
                     @Param("listingId") Long listingId,
                     @Param("price") BigDecimal price);

    // 查询某人的购买记录 (带皮肤信息)
    @Select("SELECT o.*, s.display_name, s.display_icon, u.username as seller_name " +
            "FROM orders o " +
            "LEFT JOIN market_listing m ON o.listing_id = m.id " +
            "LEFT JOIN skin_data s ON m.skin_uuid = s.uuid " +
            "LEFT JOIN users u ON o.seller_id = u.id " +
            "WHERE o.buyer_id = #{userId} " +
            "ORDER BY o.create_time DESC")
    List<Map<String, Object>> findMyOrders(Long userId);
}