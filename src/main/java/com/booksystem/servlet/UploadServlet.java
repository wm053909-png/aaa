package com.booksystem.servlet;

import com.booksystem.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 图片上传接口
 * POST /api/upload
 *
 * 支持 multipart/form-data 格式的图片上传
 * 保存到 webapp/uploads/ 目录
 */
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,       // 单个文件最大 5MB
    maxRequestSize = 10 * 1024 * 1024     // 总请求最大 10MB
)
public class UploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 获取上传的文件
            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                JsonUtil.sendError(resp, 400, "请选择要上传的文件");
                return;
            }

            // 获取原始文件名
            String originalName = filePart.getSubmittedFileName();
            if (originalName == null || originalName.isEmpty()) {
                JsonUtil.sendError(resp, 400, "文件名无效");
                return;
            }

            // 检查文件类型
            String ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
            if (!"jpg".equals(ext) && !"jpeg".equals(ext) && !"png".equals(ext) && !"gif".equals(ext)) {
                JsonUtil.sendError(resp, 400, "仅支持 jpg/jpeg/png/gif 格式的图片");
                return;
            }

            // 生成唯一文件名
            String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

            // 获取上传目录的真实路径
            String uploadPath = req.getServletContext().getRealPath("/uploads");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            File file = new File(uploadPath, newFileName);
            filePart.write(file.getAbsolutePath());

            // 构建响应
            String filePath = "uploads/" + newFileName;
            Map<String, String> data = new HashMap<>();
            data.put("file_path", filePath);
            data.put("file_name", originalName);

            JsonUtil.sendSuccess(resp, "上传成功", data);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, 500, "上传异常：" + e.getMessage());
        }
    }
}
