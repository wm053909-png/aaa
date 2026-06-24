package com.booksystem.dao;

import com.booksystem.model.Book;
import com.booksystem.model.BorrowRecord;
import com.booksystem.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 借阅记录数据访问对象
 * 核心：事务控制的借阅/归还操作
 */
public class BorrowDao {

    private final BookDao bookDao = new BookDao();

    /**
     * 借阅图书（事务操作）
     * 步骤：1.检查库存  2.扣减库存  3.插入借阅记录
     * 三个步骤必须同时成功或同时回滚
     *
     * @return 借阅记录对象（成功时返回），null 表示失败
     * @throws Exception 库存不足或其他异常
     */
    public BorrowRecord borrowBook(int bookId, int readerId) throws Exception {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            // 1. 开启事务
            DBUtil.beginTransaction();

            // 2. 检查库存是否充足（直接在事务连接上查询，避免连接被提前关闭）
            String checkSql = "SELECT id, title, stock FROM books WHERE id = ?";
            int stock = -1;
            String bookTitle = "";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, bookId);
                ResultSet rs = checkPs.executeQuery();
                if (!rs.next()) {
                    throw new Exception("图书不存在");
                }
                stock = rs.getInt("stock");
                bookTitle = rs.getString("title");
            }
            if (stock <= 0) {
                throw new Exception("库存不足，无法借阅");
            }

            // 3. 查询读者名（在事务连接上，避免额外连接关闭问题）
            String readerName = "";
            try (PreparedStatement namePs = conn.prepareStatement("SELECT name FROM readers WHERE id = ?")) {
                namePs.setInt(1, readerId);
                ResultSet nameRs = namePs.executeQuery();
                if (nameRs.next()) {
                    readerName = nameRs.getString("name");
                }
            }

            // 4. 扣减库存（在事务连接上操作）
            boolean decreased = bookDao.decreaseStock(conn, bookId);
            if (!decreased) {
                throw new Exception("库存扣减失败，可能已被其他读者借走");
            }

            // 5. 插入借阅记录
            String insertSql = "INSERT INTO borrow_records (book_id, reader_id, borrow_time, status) VALUES (?, ?, ?, '已借出')";
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, bookId);
                ps.setInt(2, readerId);
                ps.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                ps.executeUpdate();

                // 获取生成的记录ID
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int recordId = keys.getInt(1);

                    // 6. 提交事务
                    DBUtil.commit();

                    // 7. 构造返回对象
                    BorrowRecord record = new BorrowRecord();
                    record.setId(recordId);
                    record.setBookId(bookId);
                    record.setReaderId(readerId);
                    record.setBorrowTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    record.setStatus("已借出");
                    record.setBookTitle(bookTitle);
                    record.setReaderName(readerName);
                    return record;
                }
            }

            throw new Exception("借阅记录插入失败");
        } catch (Exception e) {
            // 事务回滚
            DBUtil.rollback();
            throw e;
        }
    }

    /**
     * 归还图书（事务操作）
     */
    public boolean returnBook(int recordId) throws Exception {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            DBUtil.beginTransaction();

            // 1. 查询借阅记录
            String querySql = "SELECT * FROM borrow_records WHERE id = ? AND status = '已借出'";
            try (PreparedStatement ps = conn.prepareStatement(querySql)) {
                ps.setInt(1, recordId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new Exception("借阅记录不存在或已归还");
                }
                int bookId = rs.getInt("book_id");

                // 2. 更新借阅状态为"已归还"
                String updateSql = "UPDATE borrow_records SET status = '已归还' WHERE id = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setInt(1, recordId);
                    updatePs.executeUpdate();
                }

                // 3. 增加库存
                bookDao.increaseStock(conn, bookId);

                // 4. 提交事务
                DBUtil.commit();
                return true;
            }
        } catch (Exception e) {
            DBUtil.rollback();
            throw e;
        }
    }

    /**
     * 查询某读者的借阅记录
     */
    public List<BorrowRecord> getBorrowRecordsByReader(int readerId) {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.id, br.book_id, br.reader_id, br.borrow_time, br.status, " +
                     "b.title AS book_title, r.name AS reader_name " +
                     "FROM borrow_records br " +
                     "LEFT JOIN books b ON br.book_id = b.id " +
                     "LEFT JOIN readers r ON br.reader_id = r.id " +
                     "WHERE br.reader_id = ? ORDER BY br.id DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setId(rs.getInt("id"));
                record.setBookId(rs.getInt("book_id"));
                record.setReaderId(rs.getInt("reader_id"));
                record.setBorrowTime(rs.getString("borrow_time"));
                record.setStatus(rs.getString("status"));
                record.setBookTitle(rs.getString("book_title"));
                record.setReaderName(rs.getString("reader_name"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * 查询所有借阅记录
     */
    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.id, br.book_id, br.reader_id, br.borrow_time, br.status, " +
                     "b.title AS book_title, r.name AS reader_name " +
                     "FROM borrow_records br " +
                     "LEFT JOIN books b ON br.book_id = b.id " +
                     "LEFT JOIN readers r ON br.reader_id = r.id " +
                     "ORDER BY br.id DESC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setId(rs.getInt("id"));
                record.setBookId(rs.getInt("book_id"));
                record.setReaderId(rs.getInt("reader_id"));
                record.setBorrowTime(rs.getString("borrow_time"));
                record.setStatus(rs.getString("status"));
                record.setBookTitle(rs.getString("book_title"));
                record.setReaderName(rs.getString("reader_name"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    private String getReaderName(int readerId) {
        String sql = "SELECT name FROM readers WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
