package com.example.demo.controller;

import com.example.demo.entity.Skin;
import com.example.demo.mapper.SkinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skins")
@CrossOrigin(origins = "*")
public class SkinController {

    @Autowired
    private SkinMapper skinMapper;

    @GetMapping("/page")
    public Map<String, Object> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, // 默认每页10条
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String weapon,
            @RequestParam(defaultValue = "") String search
    ) {
        // ✅ 核心算术题：
        // 第1页：(1-1)*10 = 0 (从第0条开始取10个)
        // 第2页：(2-1)*10 = 10 (从第10条开始取10个)
        int offset = (page - 1) * size;

        // 打印一下日志，看看前端传没传对 (在控制台看)
        System.out.println("查询分页：第" + page + "页, 取" + size + "条, Offset=" + offset);

        List<Skin> list = skinMapper.findByPage(category, weapon, search, offset, size);
        int total = skinMapper.countTotal(category, weapon, search);

        Map<String, Object> result = new HashMap<>();
        result.put("records", list);
        result.put("total", total);
        return result;
    }
}