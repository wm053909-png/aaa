package com.booksystem.servlet;

import com.booksystem.dao.BorrowDao;
import com.booksystem.util.DBUtil;
import com.booksystem.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * 归还图书接口
 * POST /api/return
 *
 * 请求体 JSON: {"record_id": 1}
 * 响应 JSON: {"code": 200, "msg": "归还成功"}
 */
public class ReturnServlet extends HttpServlet {

    private final BorrowDao borrowDao = new BorrowDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // 解析 JSON（Gson 默认将数字解析为 Double，需要安全转换）
            Map<String, Object> paramMap = com.booksystem.util.JsonUtil.fromJson(
                sb.toString(), Map.class
            );

            Integer recordId = paramMap.get("record_id") != null ? ((Number) paramMap.get("record_id")).intValue() : null;
            if (recordId == null) {
                JsonUtil.sendError(resp, 400, "参数不完整，需要 record_id");
                return;
            }

            // 执行归还事务
            boolean success = borrowDao.returnBook(recordId);
            if (success) {
                JsonUtil.sendSuccess(resp, "归还成功");
            } else {
                JsonUtil.sendError(resp, 400, "归还失败，记录不存在或已归还");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, 500, "归还异常：" + e.getMessage());
        } finally {
            DBUtil.close();
        }
    }
}
