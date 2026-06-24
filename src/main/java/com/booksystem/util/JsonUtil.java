package com.booksystem.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON 工具类
 * 封装 Gson 实例，提供统一的 JSON 响应方法
 */
public class JsonUtil {

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * 将对象转为 JSON 字符串
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 将 JSON 字符串转为对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 发送成功响应
     */
    public static void sendSuccess(HttpServletResponse resp, String msg, Object data) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", msg);
        result.put("data", data);
        PrintWriter out = resp.getWriter();
        out.print(toJson(result));
        out.flush();
    }

    /**
     * 发送成功响应（无 data）
     */
    public static void sendSuccess(HttpServletResponse resp, String msg) throws IOException {
        sendSuccess(resp, msg, null);
    }

    /**
     * 发送错误响应
     */
    public static void sendError(HttpServletResponse resp, int code, String msg) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", null);
        PrintWriter out = resp.getWriter();
        out.print(toJson(result));
        out.flush();
    }
}
