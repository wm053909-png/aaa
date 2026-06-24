package com.booksystem.servlet;

import com.booksystem.dao.CategoryDao;
import com.booksystem.model.Category;
import com.booksystem.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 分类查询接口
 * GET /api/categories
 */
public class CategoryServlet extends HttpServlet {

    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Category> categories = categoryDao.getAllCategories();
            JsonUtil.sendSuccess(resp, "查询成功", categories);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, 500, "查询异常：" + e.getMessage());
        }
    }
}
