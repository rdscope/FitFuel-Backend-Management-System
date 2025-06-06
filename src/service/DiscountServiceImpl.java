package service;

import dao.*;
import model.Discount;
import model.DiscountHistory;
import model.UsedDiscountCode;
import model.UserDiscount;
import service.SERVICE;
import service.ServiceException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DiscountServiceImpl implements SERVICE<Discount> {
    private final DiscountDao discountDao;
    private final Connection conn;

    public DiscountServiceImpl(Connection conn) {
        this.conn = conn;
        this.discountDao = new DiscountDaoImpl(conn);
    }

    @Override
    public boolean insert(Discount discount) throws ServiceException {
        try {
            if (!discountDao.insert(discount)) {
                throw new ServiceException("Failed Creating Discount...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Create Discount): ", e);
        }
    }

    @Override
    public boolean update(Discount discount) throws ServiceException {
        try {
            if (!discountDao.update(discount)) {
                throw new ServiceException("Failed Updating Discount...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Update Discount): ", e);
        }
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        try {
            if (!discountDao.deleteById(id)) {
                throw new ServiceException("Failed Deleting Discount...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Delete Discount): ", e);
        }
    }

    @Override
    public Discount getById(int id) throws ServiceException {
        try {
            Discount discount = discountDao.getById(id);
            if (discount == null) {
                throw new ServiceException("Discount Not Found");
            }
            return discount;
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching Discount...", e);
        }
    }

    @Override
    public List<Discount> getAll() throws ServiceException {
        try {
            return discountDao.getAll();
        } catch (SQLException e) {
            throw new ServiceException("Failed Grabbing Discounts...", e);
        }
    }

    public DiscountHistory getUserDiscountHistory(int userId) throws ServiceException {
        try {
            UserDiscountDao userDiscountDao = new UserDiscountDaoImpl(conn);
            UsedDiscountCodeDao usedDiscountCodeDao = new UsedDiscountCodeDaoImpl(conn);

            List<UserDiscount> personal = userDiscountDao.findByUser(userId);
            List<UsedDiscountCode> used = usedDiscountCodeDao.findByUser(userId);

            return new DiscountHistory(personal, used);
        } catch (SQLException e) {
            throw new ServiceException("Failed to load user discount history", e);
        }
    }

    public Discount getByName(String name) throws ServiceException {
        try {
            Discount d = discountDao.getByName(name);
            if (d == null) throw new ServiceException("Discount not found by name: " + name);
            return d;
        } catch (SQLException e) {
            throw new ServiceException("SQL error getting discount by name", e);
        }
    }

    public void sendBirthdayDiscountIfNeeded(int userId, Date birthday) throws ServiceException {
        try {
            int thisMonth = LocalDate.now().getMonthValue();

            if (birthday.toLocalDate().getMonthValue() != thisMonth) {
                return; // 不是生日月，略過
            }

            UserDiscountDao userDiscountDao = new UserDiscountDaoImpl(conn);
            Discount birthdayDiscount = discountDao.getByName("Birthday 15% OFF");

            if (birthdayDiscount == null) {
                System.out.println("Unable to send birthday discount: No discount named 'Birthday 15% OFF' found.");
                return;
            }

            List<UserDiscount> list = userDiscountDao.findByUser(userId);
            boolean alreadyHas = list.stream().anyMatch(ud ->
                    ud.getDiscountId() == birthdayDiscount.getDiscountId() &&
                            ud.getStatus().equals("Unused")
            );

            if (!alreadyHas) {
                UserDiscount newCoupon = new UserDiscount(
                        userId,
                        birthdayDiscount.getDiscountId(),
                        Date.valueOf(LocalDate.now()),
                        Date.valueOf(LocalDate.now().plusDays(30)),
                        "Unused"
                );
                userDiscountDao.insert(newCoupon);
                System.out.println("Birthday discount coupon has been automatically issued!");
            }

        } catch (SQLException e) {
            throw new ServiceException("Failed to issue birthday discount.", e);
        }
    }

    public void sendThresholdDiscountIfNeeded(int userId) throws ServiceException {
        try {
            OrderDao orderDao = new OrderDaoImpl(conn);
            UserDao userDao = new UserDaoImpl(conn);
            UserDiscountDao userDiscountDao = new UserDiscountDaoImpl(conn);

            Discount thresholdDiscount = discountDao.getByName("Threshold $500 OFF");

            if (thresholdDiscount == null) {
                System.out.println("Unable to send threshold discount: No discount named 'Threshold $500 OFF' found.");
                return;
            }

            // 查目前是否已有尚未使用的 Threshold 折扣券
            LocalDate today = LocalDate.now();

            boolean hasUnused = userDiscountDao.findValidByUser(userId, today).stream()
                    .anyMatch(ud -> ud.getDiscountId() == thresholdDiscount.getDiscountId());

            if (hasUnused) {
                System.out.println("*** Threshold coupon exists but is not used yet. Skipping issue.");
                return;
            }

            // 查目前累計消費與已扣除的門檻
            BigDecimal totalSpent = orderDao.sumTotalAmountByUserWithStatus(userId, "isPaid");
            BigDecimal accumulated = userDao.getAccumulatedThreshold(userId);  // SELECT threshold_accumulated
            BigDecimal usable = totalSpent.subtract(accumulated);

            if (usable.compareTo(BigDecimal.valueOf(5000)) >= 0) {
                // 發送 1 張券
                UserDiscount newCoupon = new UserDiscount(
                        userId,
                        thresholdDiscount.getDiscountId(),
                        Date.valueOf(today),
                        Date.valueOf(today.plusMonths(2)),
                        "Unused"
                );
                userDiscountDao.insert(newCoupon);
                System.out.println("*** Threshold discount coupon issued.");

                // 更新使用者的 threshold_accumulated += 5000（只加一次）
                BigDecimal newAccum = accumulated.add(BigDecimal.valueOf(5000));
                userDao.updateAccumulatedThreshold(userId, newAccum);
            } else {
                System.out.println("Threshold not met. Usable = " + usable);
            }

        } catch (SQLException e) {
            throw new ServiceException("Failed to issue threshold discount.", e);
        }
    }

}