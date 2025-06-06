# FitFuel Backend Management System

FitFuel is a backend system designed for managing users, products, orders, inventory, and discounts for a fitness food e-commerce platform.  
It follows a layered architecture (DAO, Service, Model) and uses MySQL as the database.

## Features

- **User Management**  
  - User registration, login, role management (admin / user)  
  - User discount tracking

- **Product & Inventory Management**  
  - Product CRUD  
  - Raw material tracking and stock management  
  - ProductMaterial mapping (for made-to-order products)  
  - Low-stock alert for admin

- **Order Management**  
  - Multi-product order support (Order + OrderDetail)  
  - Order status tracking (unPaid / isPaid)  
  - Checkout with payment method selection

- **Discount System**  
  - Birthday discount (auto-generated)  
  - Anniversary discount (periodic event)  
  - Spend-based coupons (NT$5000 spent → NT$500 coupon)  
  - Discount codes (single-use / reusable)

- **Admin Functions**  
  - Assign personal discounts to users  
  - Manually create orders for users  
  - Monitor inventory and raw material stock  
  - View user discount usage history

- **Architecture**  
  - Layered: DAO → Service → Model → CLI Menu (AdminMenu / UserMenu)  
  - MySQL database with well-defined schema  
  - Audit log for key operations (planned or partial)

## Tech Stack

- **Language**: Java  
- **Database**: MySQL  
- **Architecture**: DAO / Service / Model  
- **UI**: Command-line interface (CLI)  
- **Build**: Manual / IDE-based (can be enhanced with Maven / Gradle)

## Database Schema

The project uses the `fitfuel_fitness_food_db` schema.  
You can initialize the database using:

```sql
-- Run this script to initialize database and tables
v11_init.sql
```

Main tables:

- `User`
- `Product`
- `RawMaterial`
- `ProductMaterial`
- `Order`
- `OrderDetail`
- `Discount`
- `UserDiscount`
- `UsedDiscountCode`

## How to Run

1. Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/FitFuel-Backend-Management-System.git
```

2. Import the project into your IDE (IntelliJ IDEA / Eclipse / VSCode).  
3. Set up MySQL connection (adjust `DBConnection.java` as needed).  
4. Run `MainApp.java` to start the CLI interface.  
5. Use AdminMenu / UserMenu to interact with the system.

## Possible Improvements

- Add RESTful API layer (Spring Boot)  
- Web front-end (React / Vue / Angular)  
- Unit tests (JUnit)  
- Advanced logging (AuditLog full support)  
- CI/CD pipeline

## Author

Developed by Jeffrey Hsu.  
Project created for backend development practice and e-commerce system design.
