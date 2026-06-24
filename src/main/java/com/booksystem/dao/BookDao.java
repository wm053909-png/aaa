package com.booksystem.dao;

import com.booksystem.model.Book;
import com.booksystem.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书数据访问对象
 * 支持多条件动态检索
 */
public class BookDao {

    /**
     * 多条件动态查询图书
     * @param keyword    关键词（模糊匹配书名、作者、ISBN）
     * @param categoryId 分类ID（0或null表示不限分类）
     * @return 匹配的图书列表
     */
    public List<Book> searchBooks(String keyword, int categoryId) {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT b.id, b.title, b.author, b.isbn, b.category_id, c.name AS category_name, " +
            "b.stock, b.cover_path FROM books b LEFT JOIN categories c ON b.category_id = c.id WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        // 动态拼接条件：关键词模糊匹配
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ?)");
            String likeKeyword = "%" + keyword.trim() + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        // 动态拼接条件：分类过滤
        if (categoryId > 0) {
            sql.append(" AND b.category_id = ?");
            params.add(categoryId);
        }

        sql.append(" ORDER BY b.id DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setCategoryName(rs.getString("category_name"));
                book.setStock(rs.getInt("stock"));
                book.setCoverPath(rs.getString("cover_path"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 根据ID查询单本图书
     */
    public Book getBookById(int id) {
        String sql = "SELECT b.id, b.title, b.author, b.isbn, b.category_id, c.name AS category_name, " +
                     "b.stock, b.cover_path FROM books b LEFT JOIN categories c ON b.category_id = c.id WHERE b.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setCategoryName(rs.getString("category_name"));
                book.setStock(rs.getInt("stock"));
                book.setCoverPath(rs.getString("cover_path"));
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 扣减库存（事务中使用，需要外部传入连接）
     */
    public boolean decreaseStock(Connection conn, int bookId) throws SQLException {
        String sql = "UPDATE books SET stock = stock - 1 WHERE id = ? AND stock > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 增加库存（归还时使用）
     */
    public boolean increaseStock(Connection conn, int bookId) throws SQLException {
        String sql = "UPDATE books SET stock = stock + 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 添加新图书
     */
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, category_id, stock, cover_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getCategoryId());
            ps.setInt(5, book.getStock());
            ps.setString(6, book.getCoverPath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
