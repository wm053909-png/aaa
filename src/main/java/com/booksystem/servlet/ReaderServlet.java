package com.booksystem.servlet;

import com.booksystem.dao.ReaderDao;
import com.booksystem.model.Reader;
import com.booksystem.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 读者查询接口
 * GET /api/readers
 */
public class ReaderServlet extends HttpServlet {

    private final ReaderDao readerDao = new ReaderDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Reader> readers = readerDao.getAllReaders();
            JsonUtil.sendSuccess(resp, "查询成功", readers);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, 500, "查询异常：" + e.getMessage());
        }
    }
}
