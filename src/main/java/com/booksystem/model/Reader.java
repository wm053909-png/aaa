package com.booksystem.model;

/**
 * 读者实体类
 */
public class Reader {
    private int id;
    private String name;
    private String cardNo;

    public Reader() {}

    public Reader(int id, String name, String cardNo) {
        this.id = id;
        this.name = name;
        this.cardNo = cardNo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCardNo() { return cardNo; }
    public void setCardNo(String cardNo) { this.cardNo = cardNo; }
}
