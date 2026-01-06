const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');

// MySQL connection pool
let pool = null;

// Generate unique account number
const generateAccountNumber = () => {
    const timestamp = Date.now().toString().slice(-6);
    const random = Math.random().toString(36).substr(2, 6).toUpperCase();
    return `NB${timestamp}${random}`;
};

// Initialize database tables
const initTables = async () => {
    const connection = await pool.getConnection();

    try {
        // Users table with unique email and account_number
        await connection.execute(`
      CREATE TABLE IF NOT EXISTS users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        email VARCHAR(255) UNIQUE NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        name VARCHAR(100) NOT NULL,
        customer_id VARCHAR(50) UNIQUE NOT NULL,
        public_url VARCHAR(255) UNIQUE NOT NULL,
        account_number VARCHAR(50) UNIQUE NOT NULL,
        pin_hash VARCHAR(255),
        profile_photo VARCHAR(500),
        phone_number VARCHAR(20),
        address_street VARCHAR(200),
        address_city VARCHAR(100),
        address_state VARCHAR(100),
        address_zip VARCHAR(20),
        address_country VARCHAR(100),
        date_of_birth VARCHAR(20),
        gender VARCHAR(20),
        nationality VARCHAR(100),
        marital_status VARCHAR(20),
        occupation VARCHAR(100),
        mobile_number VARCHAR(20),
        landline_number VARCHAR(20),
        preferred_language VARCHAR(50) DEFAULT 'English',
        pan_number VARCHAR(20),
        aadhaar_number VARCHAR(20),
        passport_number VARCHAR(20),
        driving_license VARCHAR(50),
        voter_id VARCHAR(50),
        kyc_status VARCHAR(20) DEFAULT 'Pending',
        kyc_last_updated DATETIME,
        internet_banking TINYINT DEFAULT 0,
        mobile_banking TINYINT DEFAULT 0,
        sms_alerts TINYINT DEFAULT 0,
        e_statements TINYINT DEFAULT 0,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX idx_email (email),
        INDEX idx_customer_id (customer_id),
        INDEX idx_account_number (account_number)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    `);

        // Bank accounts table
        await connection.execute(`
      CREATE TABLE IF NOT EXISTS bank_accounts (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        bank_name VARCHAR(100) NOT NULL,
        institution VARCHAR(100),
        account_number VARCHAR(50),
        account_type VARCHAR(50) DEFAULT 'checking',
        balance DECIMAL(15,2) DEFAULT 0.00,
        is_active TINYINT DEFAULT 1,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX idx_user_id (user_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    `);

        // Transactions table
        await connection.execute(`
      CREATE TABLE IF NOT EXISTS transactions (
        id INT AUTO_INCREMENT PRIMARY KEY,
        transaction_id VARCHAR(50) UNIQUE NOT NULL,
        user_id INT NOT NULL,
        related_user_id INT,
        sender_id INT,
        recipient_id INT,
        sender_account_id INT,
        amount DECIMAL(15,2) NOT NULL,
        category VARCHAR(50) DEFAULT 'Other',
        description VARCHAR(500),
        status VARCHAR(20) DEFAULT 'pending',
        transaction_type VARCHAR(20) DEFAULT 'transfer',
        direction VARCHAR(20),
        priority VARCHAR(20) DEFAULT 'normal',
        processing_fee DECIMAL(15,2) DEFAULT 0.00,
        fraud_status VARCHAR(20) DEFAULT 'NONE',
        flagged_reason VARCHAR(500),
        scheduled_date DATETIME,
        recurring_frequency VARCHAR(20),
        recurring_end_date DATETIME,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        INDEX idx_user_id (user_id),
        INDEX idx_created_at (created_at),
        INDEX idx_direction (direction),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
        FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE SET NULL
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    `);

        console.log('✅ Database tables initialized successfully!');
    } finally {
        connection.release();
    }
};

// Connect to database
const connect = async () => {
    try {
        pool = mysql.createPool({
            host: process.env.DB_HOST || 'localhost',
            port: parseInt(process.env.DB_PORT || '3306', 10),
            user: process.env.DB_USER || 'root',
            password: process.env.DB_PASSWORD || '',
            database: process.env.DB_NAME || 'neobank',
            waitForConnections: true,
            connectionLimit: 10,
            queueLimit: 0
        });

        // Test connection
        const connection = await pool.getConnection();
        console.log(`📁 Connected to MySQL database: ${process.env.DB_NAME || 'neobank'}`);
        connection.release();

        // Initialize tables
        await initTables();
    } catch (error) {
        console.error('❌ Database connection error:', error);
        throw error;
    }
};

// Close database connection
const close = async () => {
    if (pool) {
        await pool.end();
        console.log('🔒 Database connection pool closed');
    }
};

// Helper function to run a query
const run = async (sql, params = []) => {
    const [result] = await pool.execute(sql, params);
    return { lastID: result.insertId, changes: result.affectedRows };
};

// Helper function to get a single row
const get = async (sql, params = []) => {
    const [rows] = await pool.execute(sql, params);
    return rows.length > 0 ? rows[0] : null;
};

// Helper function to get all rows
const all = async (sql, params = []) => {
    const [rows] = await pool.execute(sql, params);
    return rows || [];
};

// ==================== USER OPERATIONS ====================

const BCRYPT_ROUNDS = parseInt(process.env.BCRYPT_ROUNDS || '12', 10);

const User = {
    // Create a new user
    create: async (userData) => {
        const { email, password, name } = userData;
        const passwordHash = await bcrypt.hash(password, BCRYPT_ROUNDS);
        const customerId = `CUST_${Math.random().toString(36).substr(2, 9).toUpperCase()}`;
        const publicUrl = `https://neobank.com/user/${Math.random().toString(36).substr(2, 9)}`;
        const accountNumber = generateAccountNumber();

        const sql = `
      INSERT INTO users (email, password_hash, name, customer_id, public_url, account_number)
      VALUES (?, ?, ?, ?, ?, ?)
    `;

        const result = await run(sql, [email.toLowerCase(), passwordHash, name, customerId, publicUrl, accountNumber]);
        return User.findById(result.lastID);
    },

    // Find user by ID
    findById: async (id) => {
        const sql = `SELECT * FROM users WHERE id = ?`;
        const user = await get(sql, [id]);
        return user ? User.addMethods(user) : null;
    },

    // Find user by email
    findByEmail: async (email) => {
        const sql = `SELECT * FROM users WHERE email = ?`;
        const user = await get(sql, [email.toLowerCase()]);
        return user ? User.addMethods(user) : null;
    },

    // Find user by customer ID
    findByCustomerId: async (customerId) => {
        const sql = `SELECT * FROM users WHERE customer_id = ?`;
        const user = await get(sql, [customerId]);
        return user ? User.addMethods(user) : null;
    },

    // Find user by account number
    findByAccountNumber: async (accountNumber) => {
        const sql = `SELECT * FROM users WHERE account_number = ?`;
        const user = await get(sql, [accountNumber]);
        return user ? User.addMethods(user) : null;
    },

    // Find user by mobile number
    findByMobileNumber: async (mobileNumber) => {
        const cleanNumber = mobileNumber.replace(/[\s\-\(\)\+]/g, '');
        const sql = `SELECT * FROM users WHERE phone_number LIKE ? OR mobile_number LIKE ?`;
        const user = await get(sql, [`%${cleanNumber}%`, `%${cleanNumber}%`]);
        return user ? User.addMethods(user) : null;
    },

    // Find user by public URL
    findByPublicUrl: async (publicUrl) => {
        const sql = `SELECT * FROM users WHERE public_url LIKE ?`;
        const user = await get(sql, [`%${publicUrl}%`]);
        return user ? User.addMethods(user) : null;
    },

    // Update user
    update: async (id, updates) => {
        const allowedFields = [
            'name', 'phone_number', 'profile_photo', 'pin_hash',
            'address_street', 'address_city', 'address_state', 'address_zip', 'address_country',
            'date_of_birth', 'gender', 'nationality', 'marital_status', 'occupation',
            'mobile_number', 'landline_number', 'preferred_language',
            'pan_number', 'aadhaar_number', 'passport_number', 'driving_license', 'voter_id',
            'kyc_status', 'kyc_last_updated',
            'internet_banking', 'mobile_banking', 'sms_alerts', 'e_statements'
        ];

        const fields = [];
        const values = [];

        for (const [key, value] of Object.entries(updates)) {
            const dbKey = key.replace(/([A-Z])/g, '_$1').toLowerCase(); // camelCase to snake_case
            if (allowedFields.includes(dbKey)) {
                fields.push(`${dbKey} = ?`);
                values.push(value);
            }
        }

        if (fields.length === 0) return User.findById(id);

        values.push(id);

        const sql = `UPDATE users SET ${fields.join(', ')} WHERE id = ?`;
        await run(sql, values);
        return User.findById(id);
    },

    // Add methods to user object (simulating Mongoose instance methods)
    addMethods: (user) => {
        return {
            ...user,
            _id: user.id, // For compatibility

            comparePassword: async function (candidatePassword) {
                return bcrypt.compare(candidatePassword, this.password_hash);
            },

            comparePIN: async function (candidatePIN) {
                if (!this.pin_hash) return false;
                return bcrypt.compare(candidatePIN, this.pin_hash);
            },

            setPIN: async function (pin) {
                const pinHash = await bcrypt.hash(pin, BCRYPT_ROUNDS);
                await run('UPDATE users SET pin_hash = ? WHERE id = ?', [pinHash, this.id]);
                this.pin_hash = pinHash;
            },

            setPassword: async function (password) {
                const passwordHash = await bcrypt.hash(password, BCRYPT_ROUNDS);
                await run('UPDATE users SET password_hash = ? WHERE id = ?', [passwordHash, this.id]);
                this.password_hash = passwordHash;
            },

            save: async function () {
                // For compatibility - updates are handled immediately
                return this;
            },

            toPublicJSON: function () {
                return {
                    id: this.id,
                    _id: this.id,
                    name: this.name,
                    email: this.email,
                    customer_id: this.customer_id,
                    public_url: this.public_url,
                    account_number: this.account_number,
                    profile_photo: this.profile_photo,
                    phone_number: this.phone_number,
                    address: {
                        street: this.address_street,
                        city: this.address_city,
                        state: this.address_state,
                        zip_code: this.address_zip,
                        country: this.address_country
                    },
                    dateOfBirth: this.date_of_birth,
                    gender: this.gender,
                    nationality: this.nationality,
                    maritalStatus: this.marital_status,
                    occupation: this.occupation,
                    phoneNumbers: {
                        mobile: this.mobile_number,
                        landline: this.landline_number
                    },
                    preferredLanguage: this.preferred_language,
                    kyc: {
                        panNumber: this.pan_number,
                        aadhaarNumber: this.aadhaar_number,
                        passportNumber: this.passport_number,
                        drivingLicense: this.driving_license,
                        voterId: this.voter_id,
                        kycStatus: this.kyc_status,
                        lastUpdated: this.kyc_last_updated
                    },
                    linkedServices: {
                        internetBanking: !!this.internet_banking,
                        mobileBanking: !!this.mobile_banking,
                        smsAlerts: !!this.sms_alerts,
                        eStatements: !!this.e_statements
                    },
                    created_at: this.created_at
                };
            }
        };
    }
};

// ==================== BANK ACCOUNT OPERATIONS ====================

const BankAccount = {
    // Create a new bank account
    create: async (data) => {
        const { user_id, bank_name, institution, account_number, account_type, balance, is_active } = data;
        const accNum = account_number || `****${Math.floor(Math.random() * 9000) + 1000}`;

        const sql = `
      INSERT INTO bank_accounts (user_id, bank_name, institution, account_number, account_type, balance, is_active)
      VALUES (?, ?, ?, ?, ?, ?, ?)
    `;

        const result = await run(sql, [user_id, bank_name, institution || 'NeoBank', accNum, account_type || 'checking', balance || 0, is_active !== false ? 1 : 0]);
        return BankAccount.findById(result.lastID);
    },

    // Find by ID
    findById: async (id) => {
        const sql = `SELECT * FROM bank_accounts WHERE id = ?`;
        const account = await get(sql, [id]);
        return account ? BankAccount.addMethods(account) : null;
    },

    // Find by user ID
    findByUserId: async (userId, activeOnly = true) => {
        let sql = `SELECT * FROM bank_accounts WHERE user_id = ?`;
        if (activeOnly) sql += ' AND is_active = 1';
        const accounts = await all(sql, [userId]);
        return accounts.map(acc => BankAccount.addMethods(acc));
    },

    // Find first active account for user
    findFirstByUserId: async (userId) => {
        const sql = `SELECT * FROM bank_accounts WHERE user_id = ? AND is_active = 1 LIMIT 1`;
        const account = await get(sql, [userId]);
        return account ? BankAccount.addMethods(account) : null;
    },

    // Find by account number
    findByAccountNumber: async (accountNumber) => {
        const sql = `SELECT * FROM bank_accounts WHERE account_number = ? AND is_active = 1`;
        const account = await get(sql, [accountNumber]);
        return account ? BankAccount.addMethods(account) : null;
    },

    // Update balance
    updateBalance: async (id, amount) => {
        const sql = `UPDATE bank_accounts SET balance = balance + ? WHERE id = ?`;
        await run(sql, [amount, id]);
        return BankAccount.findById(id);
    },

    // Add methods to bank account object
    addMethods: (account) => {
        return {
            ...account,
            _id: account.id,

            toSummaryJSON: function () {
                return {
                    id: this.id,
                    _id: this.id,
                    bank_name: this.bank_name,
                    institution: this.institution,
                    account_number: this.account_number,
                    account_type: this.account_type,
                    balance: parseFloat(this.balance),
                    is_active: !!this.is_active
                };
            },

            save: async function () {
                return this;
            }
        };
    }
};

// ==================== TRANSACTION OPERATIONS ====================

const Transaction = {
    // Create a new transaction
    create: async (data) => {
        const transactionId = data.transaction_id || `TXN_${Math.random().toString(36).substr(2, 12).toUpperCase()}`;

        const sql = `
      INSERT INTO transactions (
        transaction_id, user_id, related_user_id, sender_id, recipient_id,
        sender_account_id, amount, category, description, status,
        transaction_type, direction, priority, processing_fee,
        fraud_status, flagged_reason, scheduled_date, recurring_frequency, recurring_end_date
      )
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `;

        const result = await run(sql, [
            transactionId,
            data.user_id,
            data.related_user_id || null,
            data.sender_id || null,
            data.recipient_id || null,
            data.sender_account_id || null,
            data.amount,
            data.category || 'Other',
            data.description || '',
            data.status || 'completed',
            data.transaction_type || 'transfer',
            data.direction || 'sent',
            data.priority || 'normal',
            data.processing_fee || 0,
            data.fraud_status || 'NONE',
            data.flagged_reason || null,
            data.scheduled_date || null,
            data.recurring_frequency || null,
            data.recurring_end_date || null
        ]);

        return Transaction.findById(result.lastID);
    },

    // Find by ID
    findById: async (id) => {
        const sql = `SELECT * FROM transactions WHERE id = ?`;
        const txn = await get(sql, [id]);
        return txn ? Transaction.addMethods(txn) : null;
    },

    // Find by user ID with pagination
    findByUserId: async (userId, page = 1, limit = 20) => {
        const offset = (page - 1) * limit;
        const sql = `SELECT * FROM transactions WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?`;
        const txns = await all(sql, [userId, limit, offset]);
        return txns.map(txn => Transaction.addMethods(txn));
    },

    // Count by user ID
    countByUserId: async (userId) => {
        const sql = `SELECT COUNT(*) as count FROM transactions WHERE user_id = ?`;
        const result = await get(sql, [userId]);
        return result ? result.count : 0;
    },

    // Find recent transactions with date range
    findByUserIdAndDateRange: async (userId, startDate, endDate) => {
        const sql = `
      SELECT * FROM transactions 
      WHERE user_id = ? AND created_at >= ? AND created_at <= ?
      ORDER BY created_at DESC
    `;
        const txns = await all(sql, [userId, startDate.toISOString(), endDate.toISOString()]);
        return txns.map(txn => Transaction.addMethods(txn));
    },

    // Group spending by category for a user
    getSpendingByCategory: async (userId, startDate, endDate) => {
        const sql = `
      SELECT category, SUM(amount) as total
      FROM transactions 
      WHERE user_id = ? AND direction = 'sent' AND created_at >= ? AND created_at <= ?
      GROUP BY category
    `;
        return all(sql, [userId, startDate.toISOString(), endDate.toISOString()]);
    },

    // Add methods to transaction object
    addMethods: (txn) => {
        return {
            ...txn,
            _id: txn.id,

            toDetailedJSON: function () {
                return {
                    id: this.id,
                    _id: this.id,
                    transaction_id: this.transaction_id,
                    user_id: this.user_id,
                    related_user_id: this.related_user_id,
                    sender_id: this.sender_id,
                    recipient_id: this.recipient_id,
                    amount: parseFloat(this.amount),
                    category: this.category,
                    description: this.description,
                    status: this.status,
                    transaction_type: this.transaction_type,
                    direction: this.direction,
                    priority: this.priority,
                    processing_fee: parseFloat(this.processing_fee),
                    fraud_status: this.fraud_status,
                    flagged_reason: this.flagged_reason,
                    created_at: this.created_at
                };
            },

            save: async function () {
                return this;
            }
        };
    }
};

module.exports = {
    connect,
    close,
    run,
    get,
    all,
    User,
    BankAccount,
    Transaction,
    generateAccountNumber
};
