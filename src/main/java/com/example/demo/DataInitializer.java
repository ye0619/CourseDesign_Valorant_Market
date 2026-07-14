package com.example.demo;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DataInitializer {

    // 🔴 记得确认这里的密码是对的
    private static final String DB_PASSWORD = ">!lOz_#vf3N7";

    private static final String DB_URL = "jdbc:mysql://localhost:3306/valorant_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false";
    private static final String DB_USER = "root";

    public static void main(String[] args) {
        System.out.println(">>> 1. 正在连接官方 API (已添加浏览器伪装)...");

        String apiUrl = "https://valorant-api.com/v1/weapons/skins?language=zh-CN";

        // 【核心修改】使用 HttpRequest 并添加 User-Agent 头，假装是浏览器
        String jsonResult = "";
        try {
            jsonResult = HttpRequest.get(apiUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(20000) // 设置超时时间为20秒
                    .execute()
                    .body();
        } catch (Exception e) {
            System.err.println("网络请求彻底失败，请检查网络连接！");
            e.printStackTrace();
            return;
        }

        // 🔍 调试打印：看看对方到底返回了什么？
        if (jsonResult == null || jsonResult.isEmpty()) {
            System.err.println("API 返回了空内容！");
            return;
        }
        // 只打印前100个字符，防止刷屏。如果是HTML，这里一眼就能看出来。
        System.out.println(">>> API响应预览: " + jsonResult.substring(0, Math.min(jsonResult.length(), 100)));

        // 2. 解析 JSON
        JSONObject root = null;
        try {
            root = JSONUtil.parseObj(jsonResult);
        } catch (Exception e) {
            System.err.println("解析失败！返回的不是标准 JSON，可能是 HTML 错误页面。");
            return;
        }

        if (root.getInt("status") != 200) {
            System.err.println("API 返回状态码不是 200，而是: " + root.getInt("status"));
            return;
        }

        JSONArray dataList = root.getJSONArray("data");
        System.out.println(">>> 2. 获取成功！找到 " + dataList.size() + " 个皮肤。正在写入数据库...");

        // 3. 写入数据库
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "REPLACE INTO skin_data (uuid, display_name, display_icon, tier_name) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            int count = 0;
            for (Object item : dataList) {
                JSONObject skin = (JSONObject) item;

                String uuid = skin.getStr("uuid");
                String name = skin.getStr("displayName");
                String icon = skin.getStr("displayIcon");

                if (icon == null || icon.isEmpty()) continue;

                String tier = "标准";
                ps.setString(1, uuid);
                ps.setString(2, name);
                ps.setString(3, icon);
                ps.setString(4, tier);

                ps.addBatch();
                count++;
            }

            ps.executeBatch();
            System.out.println("------------------------------------------------");
            System.out.println(">>> 3. 大功告成！成功插入 " + count + " 条数据。");
            System.out.println("------------------------------------------------");

        } catch (Exception e) {
            System.err.println("数据库写入失败，请检查密码！");
            e.printStackTrace();
        }
    }
}