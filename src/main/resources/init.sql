-- ============================================
-- 图书管理系统 2.0 数据库初始化脚本
-- ============================================

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

-- 图书表
CREATE TABLE IF NOT EXISTS books (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT DEFAULT '',
    isbn TEXT DEFAULT '',
    category_id INTEGER DEFAULT 1,
    stock INTEGER DEFAULT 1,
    cover_path TEXT DEFAULT '',
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 读者表
CREATE TABLE IF NOT EXISTS readers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    card_no TEXT UNIQUE NOT NULL
);

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id INTEGER NOT NULL,
    reader_id INTEGER NOT NULL,
    borrow_time TEXT NOT NULL,
    status TEXT DEFAULT '已借出',
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (reader_id) REFERENCES readers(id)
);

-- ============================================
-- 初始数据
-- ============================================

-- 分类
INSERT INTO categories (name) VALUES ('编程'), ('文学'), ('历史'), ('科学'), ('艺术');

-- 图书
INSERT INTO books (title, author, isbn, category_id, stock) VALUES
('Java编程思想', 'Bruce Eckel', '978-0131872486', 1, 5),
('深入理解JVM', '周志明', '978-7111541134', 1, 3),
('数据结构与算法', 'Robert Sedgewick', '978-0321573513', 1, 4),
('百年孤独', '加西亚·马尔克斯', '978-7544253994', 2, 6),
('活着', '余华', '978-7506365437', 2, 8),
('三体', '刘慈欣', '978-7536692930', 2, 5),
('明朝那些事儿', '当年明月', '978-7506344791', 3, 4),
('时间简史', '史蒂芬·霍金', '978-7535732255', 4, 3),
('艺术的故事', '贡布里希', '978-7549587230', 5, 2),
('Python编程导论', 'John Guttag', '978-7111585886', 1, 7);

-- 读者
INSERT INTO readers (name, card_no) VALUES
('张三', 'R2024001'),
('李四', 'R2024002'),
('王五', 'R2024003'),
('赵六', 'R2024004'),
('钱七', 'R2024005');
