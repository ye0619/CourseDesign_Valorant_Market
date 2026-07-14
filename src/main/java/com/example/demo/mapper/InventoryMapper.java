package com.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 👈 必须导入这个包
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryMapper {

    // 查询某用户的库存
    @Select("SELECT i.id, i.skin_uuid, s.display_name, s.display_icon, i.status " +
            "FROM inventory i " +
            "LEFT JOIN skin_data s ON i.skin_uuid = s.uuid " +
            "WHERE i.user_id = #{userId}")
    List<Map<String, Object>> findByUserId(@Param("userId") Long userId);

    // 随机获取一个皮肤UUID
    @Select("SELECT uuid FROM skin_data ORDER BY RAND() LIMIT 1")
    String getRandomSkinUuid();

    // 发放库存
    @Insert("INSERT INTO inventory(user_id, skin_uuid, status) VALUES(#{userId}, #{uuid}, 0)")
    void addItem(@Param("userId") Long userId, @Param("uuid") String uuid);

    // 🔴 修复点在这里：加上 @Param("invId") 和 @Param("status")
    @Update("UPDATE inventory SET status = #{status} WHERE id = #{invId}")
    void updateStatus(@Param("invId") Long invId, @Param("status") Integer status);

    // 转移库存 (购买时用)
    @Update("UPDATE inventory SET user_id = #{newOwnerId}, status = 0 WHERE id = #{invId}")
    void transferItem(@Param("invId") Long invId, @Param("newOwnerId") Long newOwnerId);
}