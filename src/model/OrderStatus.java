package model;

public enum OrderStatus {
    isPaid,
    unPaid;

    public static boolean isValid(String input) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

}