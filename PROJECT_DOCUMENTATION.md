# NeoBank Project Documentation

## 1. Project Overview
NeoBank is a comprehensive digital banking simulation platform designed to demonstrate modern banking features. It includes a responsive React-based frontend and a robust Spring Boot backend, facilitating secure user authentication, fund transfers, real-time balance tracking, and an administrative dashboard for fraud detection and user management.

## 2. Technology Stack

### Frontend
- **Framework:** React.js
- **Styling:** Tailwind CSS (for modern, responsive UI)
- **Icons:** Lucide React
- **Routing:** React Router DOM
- **HTTP Client:** Axios

### Backend
- **Framework:** Java Spring Boot
- **Database:** MySQL
- **ORM:** Hibernate / Spring Data JPA
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Build Tool:** Maven

## 3. Key Features
- **User Authentication:** Secure Login and Registration with JWT.
- **Dashboard:** Real-time view of Balance, Recent Transactions, and Account details.
- **Fund Transfers:**
  - Instant internal transfers using Email, Account Number, or Public ID.
  - **Fraud Detection System:** Automatic flagging of high-value transactions (> 5,000 INR) for manual review.
  - **PIN Verification:** Secure 4-digit PIN requirement for every transfer.
- **Transaction History:** Detailed filtering and pagination of user transactions.
- **Admin Panel:**
  - **Fraud Monitoring:** Review pending suspicious transactions.
  - **Decision System:** Approve (release funds) or Block (refund sender) transactions.
  - **User Management:** Freeze, Block, or Activate user accounts.
  - **Audit Logs:** Immutable record of all administrative actions.

---

## 4. Project Structure & Scope

### A. Frontend (`neo_bank-main/src`)
The frontend is structured to separate UI components, page logic, and global state.

| Folder | Scope & Description |
|--------|---------------------|
| **`components`** | Reusable UI blocks. <br> - `TransferFunds.js`: Handles the transfer form, PIN input, and success modals. <br> - `TransactionHistory.js`: Displays tables of past user activity. <br> - `Layout.js`: Main wrapper with Sidebar and Header. |
| **`contexts`** | Global state management. <br> - `AuthContext.js`: Manages user login state, token storage, and session persistence. |
| **`pages`** | Full-page views mapped to routes. <br> - `Dashboard.js`: User homepage with charts and summaries. <br> - `AdminDashboard.js`: The control center for Admins to view audits and manage fraud. <br> - `Login.js` / `Register.js`: Auth pages. |
| **`services`** | API communication layer. <br> - `api.js`: Centralized Axios instance with interceptors for token handling and error management. All backend calls (auth, banking, admin) live here. |

### B. Backend (`backend/src/main/java/com/neobank`)
The backend follows a standard controller-service-repository architecture.

| Package | Scope & Description |
|---------|---------------------|
| **`controller`** | REST API Endpoints. <br> - `AuthController.java`: Login/Register endpoints. <br> - `BankingController.java`: Endpoints for transfers, balance, and history. <br> - `AdminController.java`: Secured endpoints for fraud review and user management. |
| **`service`** | Business Logic. <br> - `BankingService.java`: Core logic for `transfer()` including balance checks, PIN validation, and **Fraud Detection**. <br> - `AdminService.java`: Logic for approving/blocking transactions and handling refunds/credits. <br> - `AuthService.java`: User creation and JWT generation. |
| **`repository`** | Database Access Layer (Interfaces extending `JpaRepository`). <br> - `TransactionRepository.java`: Queries for fetching user or flagged transactions. <br> - `UserRepository.java`: Queries for finding users by various identifiers. |
| **`entity`** | Database Models. <br> - `User.java`: User profile data. <br> - `Transaction.java`: Stores amount, status (`PENDING`, `COMPLETED`), and fraud flags. <br> - `BankAccount.java`: Links users to balances. |
| **`dto`** | Data Transfer Objects. <br> - `TransferRequest.java`: Defines the payload structure for transfer API calls (ensures JSON compatibility). |

---

## 5. How It Works

### The Transfer Flow (With Fraud Logic)
1.  **User Initiates:** User fills `TransferFunds` form and enters PIN.
2.  **Request Sent:** Frontend sends data to `/api/banking/transfer`.
3.  **Backend Processing (`BankingService`):**
    *   Validates PIN and Balance.
    *   Deducts money from **Sender's** account immediately.
    *   **Fraud Check:** Checks if `amount > 5000`.
        *   **If Safe:** Immediately credits **Recipient**. Status = `COMPLETED`.
        *   **If Suspicious:** Does **NOT** credit Recipient. transaction flagged as `PENDING`.
4.  **Admin Review (`AdminDashboard`):**
    *   Admin sees the `PENDING` transaction.
    *   **Action: Approve:** System finds Recipient and credits the held amount.
    *   **Action: Block:** System finds Sender and **refunds** the deducted amount.

### Authentication Flow
1.  User enters credentials.
2.  Backend validates against `users` table.
3.  On success, returns a **JWT (Bearer Token)**.
4.  Frontend `AuthContext` saves this token (in memory or storage) and attaches it to every subsequent Axios request header (`Authorization: Bearer <token>`).

## 6. Running the Project

### Prerequisites
- Node.js & npm
- JDK 17+ (Java 25 is currently configured)
- MySQL Server (running on port 3306)

### Steps
1.  **Database:** Ensure MySQL is running and `neobank` database exists.
2.  **Backend:**
    ```bash
    cd backend
    mvn spring-boot:run
    ```
    *Server starts on `localhost:8080`*
3.  **Frontend:**
    ```bash
    cd neo_bank-main
    npm install
    npm start
    ```
    *App launches on `localhost:3000`*
