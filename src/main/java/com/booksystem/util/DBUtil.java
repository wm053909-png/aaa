package com.booksystem.util;

import java.sql.*;

/**
 * SQLite 数据库连接工具类
 * 提供连接获取、事务管理功能
 */
public class DBUtil {

    private static final String DB_URL = "jdbc:sqlite:book.db";

    // 使用 ThreadLocal 保证每个线程独占一个连接（事务场景关键）
    private static final ThreadLocal<Connection> localConn = new ThreadLocal<>();

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite 驱动加载失败", e);
        }
    }

    /**
     * 获取当前线程的数据库连接
     * 如果 ThreadLocal 中没有连接，则创建新连接
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = localConn.get();
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL);
            localConn.set(conn);
        }
        return conn;
    }

    /**
     * 开启事务（关闭自动提交）
     */
    public static void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
    }

    /**
     * 提交事务
     */
    public static void commit() throws SQLException {
        Connection conn = localConn.get();
        if (conn != null && !conn.isClosed()) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback() {
        try {
            Connection conn = localConn.get();
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接并清理 ThreadLocal
     */
    public static void close() {
        try {
            Connection conn = localConn.get();
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            localConn.remove();
        }
    }

    /**
     * 初始化数据库（执行建表脚本）
     */
    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // 创建分类表
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE)"
            );

            // 创建图书表
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT DEFAULT ''," +
                "isbn TEXT DEFAULT ''," +
                "category_id INTEGER DEFAULT 1," +
                "stock INTEGER DEFAULT 1," +
                "cover_path TEXT DEFAULT ''," +
                "FOREIGN KEY (category_id) REFERENCES categories(id))"
            );

            // 创建读者表
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS readers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "card_no TEXT UNIQUE NOT NULL)"
            );

            // 创建借阅记录表
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS borrow_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "book_id INTEGER NOT NULL," +
                "reader_id INTEGER NOT NULL," +
                "borrow_time TEXT NOT NULL," +
                "status TEXT DEFAULT '已借出'," +
                "FOREIGN KEY (book_id) REFERENCES books(id)," +
                "FOREIGN KEY (reader_id) REFERENCES readers(id))"
            );

            // 检查是否已有数据，没有则插入初始数据
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories");
            if (rs.next() && rs.getInt(1) == 0) {
                insertInitData(stmt);
            }
            rs.close();

            System.out.println("[DBUtil] 数据库初始化完成");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    private static void insertInitData(Statement stmt) throws SQLException {
        // 分类
        stmt.executeUpdate("INSERT INTO categories (name) VALUES ('编程'),('文学'),('历史'),('科学'),('艺术')");

        // 图书
        stmt.executeUpdate(
            "INSERT INTO books (title, author, isbn, category_id, stock) VALUES " +
            "('Java编程思想','Bruce Eckel','978-0131872486',1,5)," +
            "('深入理解JVM','周志明','978-7111541134',1,3)," +
            "('数据结构与算法','Robert Sedgewick','978-0321573513',1,4)," +
            "('百年孤独','加西亚·马尔克斯','978-7544253994',2,6)," +
            "('活着','余华','978-7506365437',2,8)," +
            "('三体','刘慈欣','978-7536692930',2,5)," +
            "('明朝那些事儿','当年明月','978-7506344791',3,4)," +
            "('时间简史','史蒂芬·霍金','978-7535732255',4,3)," +
            "('艺术的故事','贡布里希','978-7549587230',5,2)," +
            "('Python编程导论','John Guttag','978-7111585886',1,7)"
        );

        // 读者
        stmt.executeUpdate(
            "INSERT INTO readers (name, card_no) VALUES " +
            "('张三','R2024001'),('李四','R2024002'),('王五','R2024003')," +
            "('赵六','R2024004'),('钱七','R2024005')"
        );

        System.out.println("[DBUtil] 初始数据插入完成");
    }
}
