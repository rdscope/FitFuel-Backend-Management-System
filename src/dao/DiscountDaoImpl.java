package dao;

import model.Discount;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscountDaoImpl implements DiscountDao {
    private final Connection conn;

    public DiscountDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(Discount d) throws SQLException {
        String sql = "INSERT INTO Discount (name, code, discount_amount, is_percentage, is_single_use, is_recurring, " +
                "recurring_start_month, recurring_start_day, recurring_end_month, recurring_end_day, " +
                "start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getCode());
            ps.setBigDecimal(3, d.getDiscountAmount());
            ps.setBoolean(4, d.isPercentage());
            ps.setBoolean(5, d.isSingleUse());
            ps.setBoolean(6, d.isRecurring());
            ps.setInt(7, d.getRecurringStartMonth());
            ps.setInt(8, d.getRecurringStartDay());
            ps.setInt(9, d.getRecurringEndMonth());
            ps.setInt(10, d.getRecurringEndDay());
            ps.setDate(11, d.getStartDate());
            ps.setDate(12, d.getEndDate());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Discount d) throws SQLException {
        String sql = "UPDATE Discount SET name=?, code=?, discount_amount=?, is_percentage=?, is_single_use=?, is_recurring=?, " +
                "recurring_start_month=?, recurring_start_day=?, recurring_end_month=?, recurring_end_day=?, " +
                "start_date=?, end_date=? WHERE discount_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getCode());
            ps.setBigDecimal(3, d.getDiscountAmount());
            ps.setBoolean(4, d.isPercentage());
            ps.setBoolean(5, d.isSingleUse());
            ps.setBoolean(6, d.isRecurring());
            ps.setInt(7, d.getRecurringStartMonth());
            ps.setInt(8, d.getRecurringStartDay());
            ps.setInt(9, d.getRecurringEndMonth());
            ps.setInt(10, d.getRecurringEndDay());
            ps.setDate(11, d.getStartDate());
            ps.setDate(12, d.getEndDate());
            ps.setInt(13, d.getDiscountId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM Discount WHERE discount_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Discount getById(int id) throws SQLException {
        String sql = "SELECT * FROM Discount WHERE discount_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    @Override
    public Discount getByName(String name) throws SQLException {
        String sql = "SELECT * FROM Discount WHERE name=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    @Override
    public List<Discount> getAll() throws SQLException {
        List<Discount> list = new ArrayList<>();
        String sql = "SELECT * FROM Discount";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<Discount> findActiveDiscounts(Date today) throws SQLException {
        List<Discount> result = new ArrayList<>();
        LocalDate now = new java.sql.Date(today.getTime()).toLocalDate();

        String sql = "SELECT * FROM Discount";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Discount d = map(rs);

                if (d.isRecurring()) {
                    LocalDate start = LocalDate.of(now.getYear(), d.getRecurringStartMonth(), d.getRecurringStartDay());
                    LocalDate end = LocalDate.of(now.getYear(), d.getRecurringEndMonth(), d.getRecurringEndDay());

                    if (end.isBefore(start)) {
                        // 跨年活動
                        if (!now.isBefore(start) || !now.isAfter(end.plusYears(1))) {
                            result.add(d);
                        }
                    } else {
                        if (!now.isBefore(start) && !now.isAfter(end)) {
                            result.add(d);
                        }
                    }
                } else {
                    if (d.getStartDate() != null && d.getEndDate() != null) {
                        LocalDate start = d.getStartDate().toLocalDate();
                        LocalDate end = d.getEndDate().toLocalDate();
                        if (!now.isBefore(start) && !now.isAfter(end)) {
                            result.add(d);
                        }
                    }
                }
            }
        }
        return result;
    }

    private Discount map(ResultSet rs) throws SQLException {
        return new Discount(
                rs.getInt("discount_id"),
                rs.getString("name"),
                rs.getString("code"),
                rs.getBigDecimal("discount_amount"),
                rs.getBoolean("is_percentage"),
                rs.getBoolean("is_single_use"),
                rs.getBoolean("is_recurring"),
                rs.getInt("recurring_start_month"),
                rs.getInt("recurring_start_day"),
                rs.getInt("recurring_end_month"),
                rs.getInt("recurring_end_day"),
                rs.getDate("start_date"),
                rs.getDate("end_date"),
                rs.getTimestamp("created_at")
        );
    }
}
