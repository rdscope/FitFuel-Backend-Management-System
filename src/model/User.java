package model;

import java.util.Date;

public class User {
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private Date birthday;
    private Date registerDate;
    private UserRole role; // 用 enum 代替 String

    public User() {}

    public User(int userId, String name, String email, String passwordHash, Date birthday, Date registerDate, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.birthday = birthday;
        this.registerDate = registerDate;
        this.role = role;
    }

    public int getUserId() {return userId;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    // 密碼敏感，僅允許 set，不開放 get
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}
    public Date getBirthday() {return birthday;}
    public void setBirthday(Date birthday) {this.birthday = birthday;}
    public Date getRegisterDate() {return registerDate;}
    public UserRole getRole() {return role;}
    public void setRole(UserRole role) {this.role = role;}

    public String getPasswordHashForDAO() {
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new IllegalStateException("User password is not initialized. Please hash it through the Service first.");
        }
        return passwordHash;
    } // 僅 DAO 內部允許用特殊方式存取

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", registerDate=" + registerDate +
                '}';
    }
}
