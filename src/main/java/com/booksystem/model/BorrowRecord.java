package com.booksystem.model;

/**
 * 借阅记录实体类
 */
public class BorrowRecord {
    private int id;
    private int bookId;
    private int readerId;
    private String borrowTime;
    private String status; // 已借出 / 已归还

    // 关联查询字段
    private String bookTitle;
    private String readerName;

    public BorrowRecord() {}

    public BorrowRecord(int id, int bookId, int readerId, String borrowTime, String status) {
        this.id = id;
        this.bookId = bookId;
        this.readerId = readerId;
        this.borrowTime = borrowTime;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getReaderId() { return readerId; }
    public void setReaderId(int readerId) { this.readerId = readerId; }

    public String getBorrowTime() { return borrowTime; }
    public void setBorrowTime(String borrowTime) { this.borrowTime = borrowTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getReaderName() { return readerName; }
    public void setReaderName(String readerName) { this.readerName = readerName; }
}
