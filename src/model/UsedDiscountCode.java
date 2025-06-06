package model;

import java.sql.Timestamp;

public class UsedDiscountCode {
    private int id;
    private int userId;
    private String code;
    private Timestamp usedAt;

    public UsedDiscountCode() {}

    public UsedDiscountCode(int id, int userId, String code, Timestamp usedAt) {
        this.id = id;
        this.userId = userId;
        setCode(code);
        this.usedAt = usedAt;
    }

    public int getId() { return id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCode() { return code; }
    public void setCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Discount code cannot be blank.");
        }
        this.code = code;
    }

    public Timestamp getUsedAt() { return usedAt; }
    public void setUsedAt(Timestamp usedAt) { this.usedAt = usedAt; }

    @Override
    public String toString() {
        return "UsedDiscountCode{" +
                "id=" + id +
                ", userId=" + userId +
                ", code='" + code + '\'' +
                ", usedAt=" + usedAt +
                '}';
    }
}
