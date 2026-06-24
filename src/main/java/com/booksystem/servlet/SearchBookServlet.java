package com.booksystem.servlet;

import com.booksystem.dao.BookDao;
import com.booksystem.model.Book;
import com.booksystem.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 多条件动态检索接口
 * GET /api/search?keyword=xxx&category_id=1
 *
 * 前端 AJAX 请求此接口，返回 JSON 格式的图书列表
 */
public class SearchBookServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 获取查询参数
            String keyword = req.getParameter("keyword");
            String categoryIdStr = req.getParameter("category_id");

            int categoryId = 0;
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Integer.parseInt(categoryIdStr);
            }

            // 执行多条件查询
            List<Book> books = bookDao.searchBooks(keyword, categoryId);

            // 返回 JSON 响应
            JsonUtil.sendSuccess(resp, "查询成功", books);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, 500, "查询异常：" + e.getMessage());
        }
    }
}
