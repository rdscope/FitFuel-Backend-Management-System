package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Discount {
    private int discountId;
    private String name;
    private String code;
    private BigDecimal discountAmount;
    private boolean isPercentage;
    private boolean isSingleUse;
    private boolean isRecurring;
    private int recurringStartMonth;
    private int recurringStartDay;
    private int recurringEndMonth;
    private int recurringEndDay;
    private Date startDate;
    private Date endDate;
    private Timestamp createdAt;

    public Discount() {}

    public Discount(int discountId, String name, String code, BigDecimal discountAmount,
                    boolean isPercentage, boolean isSingleUse, boolean isRecurring,
                    int recurringStartMonth, int recurringStartDay,
                    int recurringEndMonth, int recurringEndDay,
                    Date startDate, Date endDate, Timestamp createdAt) {
        this.discountId = discountId;
        setName(name);
        setCode(code);
        this.discountAmount = discountAmount;
        this.isPercentage = isPercentage;
        this.isSingleUse = isSingleUse;
        this.isRecurring = isRecurring;
        this.recurringStartMonth = recurringStartMonth;
        this.recurringStartDay = recurringStartDay;
        this.recurringEndMonth = recurringEndMonth;
        this.recurringEndDay = recurringEndDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public int getDiscountId() { return discountId; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Discount name cannot be blank.");
        }
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) {
        if (code != null && code.isBlank()) {
            throw new IllegalArgumentException("If discount code is provided, it must not be blank.");
        }
        this.code = code;
    }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public boolean isPercentage() { return isPercentage; }
    public void setPercentage(boolean percentage) { isPercentage = percentage; }

    public boolean isSingleUse() { return isSingleUse; }
    public void setSingleUse(boolean singleUse) { isSingleUse = singleUse; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public int getRecurringStartMonth() { return recurringStartMonth; }
    public void setRecurringStartMonth(int recurringStartMonth) { this.recurringStartMonth = recurringStartMonth; }

    public int getRecurringStartDay() { return recurringStartDay; }
    public void setRecurringStartDay(int recurringStartDay) { this.recurringStartDay = recurringStartDay; }

    public int getRecurringEndMonth() { return recurringEndMonth; }
    public void setRecurringEndMonth(int recurringEndMonth) { this.recurringEndMonth = recurringEndMonth; }

    public int getRecurringEndDay() { return recurringEndDay; }
    public void setRecurringEndDay(int recurringEndDay) { this.recurringEndDay = recurringEndDay; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Discount{" +
                "discountId=" + discountId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", discountAmount=" + discountAmount +
                ", isPercentage=" + isPercentage +
                ", isSingleUse=" + isSingleUse +
                ", isRecurring=" + isRecurring +
                ", recurringStart=" + recurringStartMonth + "/" + recurringStartDay +
                ", recurringEnd=" + recurringEndMonth + "/" + recurringEndDay +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
