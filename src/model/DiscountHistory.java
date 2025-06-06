package model;

import java.util.List;

public class DiscountHistory {
    private List<UserDiscount> personalCoupons;
    private List<UsedDiscountCode> usedCodes;

    public DiscountHistory(List<UserDiscount> personalCoupons, List<UsedDiscountCode> usedCodes) {
        this.personalCoupons = personalCoupons;
        this.usedCodes = usedCodes;
    }

    public List<UserDiscount> getPersonalCoupons() {
        return personalCoupons;
    }

    public List<UsedDiscountCode> getUsedCodes() {
        return usedCodes;
    }

    @Override
    public String toString() {
        return "DiscountHistory{" +
                "personalCoupons=" + personalCoupons +
                ", usedCodes=" + usedCodes +
                '}';
    }
}