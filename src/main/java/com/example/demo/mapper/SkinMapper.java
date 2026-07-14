package com.example.demo.mapper;

import com.example.demo.entity.Skin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SkinMapper {

    // ✅ 重点检查：最后必须有 LIMIT #{offset}, #{size}
    @Select("<script>" +
            "SELECT * FROM skin_data WHERE 1=1 " +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='weapon != null and weapon != \"\"'> AND weapon = #{weapon} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND display_name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "ORDER BY uuid " + // 排序保证分页稳定
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<Skin> findByPage(@Param("category") String category,
                          @Param("weapon") String weapon,
                          @Param("keyword") String keyword,
                          @Param("offset") int offset,
                          @Param("size") int size);

    // 统计总数 (保持不变)
    @Select("<script>" +
            "SELECT count(*) FROM skin_data WHERE 1=1 " +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='weapon != null and weapon != \"\"'> AND weapon = #{weapon} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND display_name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "</script>")
    int countTotal(@Param("category") String category,
                   @Param("weapon") String weapon,
                   @Param("keyword") String keyword);
}