package model;

import java.sql.Date;
import java.sql.Timestamp;

public class UserDiscount {
    private int id;
    private int userId;
    private int discountId;
    private Date startDate;
    private Date endDate;
    private String status;
    private Timestamp issuedAt;

    public UserDiscount() {}

    public UserDiscount(int id, int userId, int discountId,
                        Date startDate, Date endDate, String status, Timestamp issuedAt) {
        this.id = id;
        this.userId = userId;
        this.discountId = discountId;
        this.startDate = startDate;
        this.endDate = endDate;
        setStatus(status);
        this.issuedAt = issuedAt;
    }
    public UserDiscount(int userId, int discountId, Date startDate, Date endDate, String status) {
        this.userId = userId;
        this.discountId = discountId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() { return id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDiscountId() { return discountId; }
    public void setDiscountId(int discountId) { this.discountId = discountId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (!status.equals("Unused") && !status.equals("Used")) {
            throw new IllegalArgumentException("Status must be 'Unused' or 'Used'.");
        }
        this.status = status;
    }

    public Timestamp getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Timestamp issuedAt) { this.issuedAt = issuedAt; }

    @Override
    public String toString() {
        return "UserDiscount{" +
                "id=" + id +
                ", userId=" + userId +
                ", discountId=" + discountId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
