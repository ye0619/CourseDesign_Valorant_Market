package com.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 👈 别忘了导入
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ListingMapper {

    // 🔴 确保这里也加了 @Param
    @Insert("INSERT INTO market_listing(seller_id, inventory_id, skin_uuid, price, status) " +
            "VALUES(#{userId}, #{invId}, #{uuid}, #{price}, 0)")
    void addListing(@Param("userId") Long userId,
                    @Param("invId") Long invId,
                    @Param("uuid") String uuid,
                    @Param("price") BigDecimal price);

    @Select("SELECT * FROM market_listing WHERE id = #{id} AND status = 0")
    Map<String, Object> findListingById(@Param("id") Long id);

    @Update("UPDATE market_listing SET status = 1 WHERE id = #{id} AND status = 0")
    int markAsSold(@Param("id") Long id);

    @Select("SELECT m.id, m.price, u.username as seller_name, m.create_time " +
            "FROM market_listing m " +
            "LEFT JOIN users u ON m.seller_id = u.id " +
            "WHERE m.skin_uuid = #{uuid} AND m.status = 0 " +
            "ORDER BY m.price ASC") // 按价格从低到高排序
    List<Map<String, Object>> findBySkinUuid(@Param("uuid") String uuid);
}