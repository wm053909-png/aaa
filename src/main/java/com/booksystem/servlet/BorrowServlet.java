package com.booksystem.servlet;

import com.booksystem.dao.BorrowDao;
import com.booksystem.model.BorrowRecord;
import com.booksystem.util.DBUtil;
import com.booksystem.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 异步借阅接口
 * POST /api/borrow
 *
 * 请求体 JSON: {"book_id": 1, "reader_id": 1}
 * 响应 JSON: {"code": 200, "msg": "借阅成功", "data": {...}}
 *
 * 核心业务：事务控制（检查库存 → 扣减库存 → 插入借阅记录）
 */
public class BorrowServlet extends HttpServlet {

    private final BorrowDao borrowDao = new BorrowDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 读取请求体中的 JSON
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

            Integer bookId = paramMap.get("book_id") != null ? ((Number) paramMap.get("book_id")).intValue() : null;
            Integer readerId = paramMap.get("reader_id") != null ? ((Number) paramMap.get("reader_id")).intValue() : null;

            // 参数校验
            if (bookId == null || readerId == null) {
                JsonUtil.sendError(resp, 400, "参数不完整，需要 book_id 和 reader_id");
                return;
            }

            // 执行借阅事务
            BorrowRecord record = borrowDao.borrowBook(bookId, readerId);

            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("record_id", record.getId());
            data.put("book_title", record.getBookTitle());
            data.put("reader_name", record.getReaderName());
            data.put("borrow_time", record.getBorrowTime());
            // 返回最新库存（查询一次）
            com.booksystem.dao.BookDao bookDao = new com.booksystem.dao.BookDao();
            com.booksystem.model.Book book = bookDao.getBookById(bookId);
            data.put("stock", book != null ? book.getStock() : 0);

            JsonUtil.sendSuccess(resp, "借阅成功", data);

        } catch (Exception e) {
            e.printStackTrace();
            // 优雅异常拦截：返回错误码 JSON，而非 Tomcat 黄色错误页
            String msg = e.getMessage();
            if (msg != null && msg.contains("库存不足")) {
                JsonUtil.sendError(resp, 400, "库存不足，无法借阅");
            } else if (msg != null && msg.contains("已被其他读者借走")) {
                JsonUtil.sendError(resp, 400, "库存不足，无法借阅");
            } else {
                JsonUtil.sendError(resp, 500, "借阅异常：" + msg);
            }
        } finally {
            // 确保连接被释放
            DBUtil.close();
        }
    }
}
