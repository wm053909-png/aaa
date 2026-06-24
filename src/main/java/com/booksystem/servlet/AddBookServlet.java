package com.booksystem.servlet;

import com.booksystem.dao.BookDao;
import com.booksystem.model.Book;
import com.booksystem.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * 添加图书接口
 * POST /api/book/add
 *
 * 请求体 JSON: {"title":"...", "author":"...", "isbn":"...", "category_id":1, "stock":5, "cover_path":"..."}
 */
public class AddBookServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();

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

            // 解析 JSON
            Map<String, Object> paramMap = com.booksystem.util.JsonUtil.fromJson(
                sb.toString(), Map.class
            );

            String title = (String) paramMap.get("title");
            if (title == null || title.trim().isEmpty()) {
                JsonUtil.sendError(resp, 400, "书名不能为空");
                return;
            }

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(paramMap.get("author") != null ? (String) paramMap.get("author") : "");
            book.setIsbn(paramMap.get("isbn") != null ? (String) paramMap.get("isbn") : "");
            book.setCategoryId(paramMap.get("category_id") != null ? ((Number) paramMap.get("category_id")).intValue() : 1);
            book.setStock(paramMap.get("stock") != null ? ((Number) paramMap.get("stock")).intValue() : 1);
            book.setCoverPath(paramMap.get("cover_path") != null ? (String) paramMap.get("cover_path") : "");

            boolean success = bookDao.addBook(book);
            if (success) {
                JsonUtil.sendSuccess(resp, "添加图书成功");
            } else {
                JsonUtil.sendError(resp, 500, "添加图书失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, 500, "添加异常：" + e.getMessage());
        }
    }
}
