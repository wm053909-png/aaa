package com.booksystem.model;

/**
 * 图书实体类
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private int categoryId;
    private String categoryName; // 关联查询时使用
    private int stock;
    private String coverPath;

    public Book() {}

    public Book(int id, String title, String author, String isbn, int categoryId, int stock, String coverPath) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.categoryId = categoryId;
        this.stock = stock;
        this.coverPath = coverPath;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCoverPath() { return coverPath; }
    public void setCoverPath(String coverPath) { this.coverPath = coverPath; }
}
