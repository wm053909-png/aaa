package com.booksystem.dao;

import com.booksystem.model.Reader;
import com.booksystem.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 读者数据访问对象
 */
public class ReaderDao {

    /**
     * 查询所有读者
     */
    public List<Reader> getAllReaders() {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT id, name, card_no FROM readers ORDER BY id";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reader reader = new Reader();
                reader.setId(rs.getInt("id"));
                reader.setName(rs.getString("name"));
                reader.setCardNo(rs.getString("card_no"));
                readers.add(reader);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readers;
    }

    /**
     * 根据ID查询读者
     */
    public Reader getReaderById(int id) {
        String sql = "SELECT id, name, card_no FROM readers WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Reader reader = new Reader();
                reader.setId(rs.getInt("id"));
                reader.setName(rs.getString("name"));
                reader.setCardNo(rs.getString("card_no"));
                return reader;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
