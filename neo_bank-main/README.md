# NeoBank App

A modern banking application with secure account management and fund transfers, now with full MySQL database integration.

## Features

- 🔐 **Secure Authentication**: JWT-based authentication with password hashing
- 💳 **Bank Account Management**: Link and manage multiple bank accounts
- 💸 **Fund Transfers**: Send money to other users using **Email**, **Account Number**, Customer ID, or Mobile Number
- 📊 **Transaction History**: View detailed transaction records with pagination
- 🛡️ **Security**: Rate limiting, CORS protection, PIN verification, and input validation
- 🗄️ **Database Storage**: MySQL database for persistent data storage

## Tech Stack

### Frontend
- React 18
- Tailwind CSS
- Axios for API calls
- React Router for navigation

### Backend
- Node.js with Express
- MySQL database (mysql2)
- JWT authentication
- bcryptjs for password hashing
- Express validation and rate limiting

## Database Schema

The application uses MySQL with the following tables:

- **users**: User accounts with unique email and account_number
- **bank_accounts**: Linked bank account information
- **transactions**: Transfer and transaction records

## Transfer Methods

You can transfer funds using any of the following **unique identifiers**:

| Method | Format | Example |
|--------|--------|---------|
| **Email** | user@example.com | john@gmail.com |
| **Account Number** | NB + alphanumeric | NB123ABC456 |
| Customer ID | CUST_ + alphanumeric | CUST_ABC123 |
| Mobile Number | 10 digits | 9876543210 |

## Setup Instructions

### Prerequisites
- Node.js (v14 or higher)
- MySQL Server (v5.7 or higher)
- npm or yarn

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd neobank-app
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Create MySQL Database**
   ```sql
   CREATE DATABASE neobank;
   ```

4. **Configure database connection**
   
   Edit `server/config.env`:
   ```env
   DB_HOST=localhost
   DB_PORT=3306
   DB_USER=root
   DB_PASSWORD=your_password
   DB_NAME=neobank
   ```

5. **Start the development server**
   ```bash
   npm run dev
   ```

This will start both the backend server (port 5000) and frontend development server (port 3000).
The database tables will be automatically created on first run.

### Alternative: Run servers separately

**Backend only:**
```bash
npm run server
```

**Frontend only:**
```bash
npm start
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/profile` - Get user profile
- `PUT /api/auth/profile` - Update user profile
- `PUT /api/auth/change-password` - Change password
- `POST /api/auth/set-pin` - Set transfer PIN

### Banking
- `POST /api/banking/link-account` - Link bank account
- `GET /api/banking/accounts` - Get user's bank accounts
- `POST /api/banking/transfer` - Transfer funds (supports email, account number, etc.)
- `GET /api/banking/transactions` - Get transaction history
- `GET /api/banking/balance` - Get account balance
- `GET /api/banking/find-user/:identifier` - Find user by email, account number, etc.
- `GET /api/banking/ai-insights` - Get personalized spending insights

### Health Check
- `GET /api/health` - API health status

## Environment Variables

Create a `server/config.env` file with the following variables:

```env
# Database Configuration (MySQL)
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=
DB_NAME=neobank

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRES_IN=24h

# Server Configuration
PORT=5000
NODE_ENV=development

# Security
BCRYPT_ROUNDS=12
```

## Security Features

- **Password Hashing**: All passwords are hashed using bcryptjs
- **PIN Protection**: 4-digit PIN required for all transfers
- **JWT Authentication**: Secure token-based authentication
- **Rate Limiting**: API rate limiting to prevent abuse
- **Input Validation**: Server-side validation for all inputs
- **CORS Protection**: Configured CORS for security
- **Helmet**: Security headers middleware

## Unique Identifiers

Each user has the following **unique identifiers**:

| Field | Description | Auto-Generated |
|-------|-------------|----------------|
| `email` | User's email address | No (user provided) |
| `account_number` | NeoBank account (NB...) | Yes |
| `customer_id` | Customer ID (CUST_...) | Yes |

## Troubleshooting

### Common Issues

1. **MySQL connection failed**: 
   - Ensure MySQL server is running
   - Check credentials in config.env
   - Run `CREATE DATABASE neobank;` if database doesn't exist

2. **Port already in use**: Change the PORT in config.env or kill the process using the port

3. **CORS errors**: Ensure the frontend URL is correctly configured in the backend CORS settings

4. **Transfer failed - Recipient not found**: 
   - Verify the email or account number is correct
   - Account numbers start with "NB"

### Logs

Check the console output for detailed error messages and API request logs.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.