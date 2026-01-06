const express = require('express');
const { body, validationResult } = require('express-validator');
const { User, BankAccount, Transaction } = require('../database');
const { authenticateToken } = require('../middleware/auth');
const insightsService = require('../services/insightsService');

const router = express.Router();

// Link bank account
router.post('/link-account', authenticateToken, [
  body('bankName').notEmpty(),
  body('institution').notEmpty()
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        error: errors.array()[0].msg
      });
    }

    const { bankName, institution } = req.body;
    const userId = req.user.id;

    // Generate bank account data
    const balance = Math.floor(Math.random() * 500000) + 10000;
    const accountNumber = `****${Math.floor(Math.random() * 9000) + 1000}`;

    // Create new bank account
    const newBankAccount = await BankAccount.create({
      user_id: userId,
      bank_name: bankName,
      institution,
      account_number: accountNumber,
      account_type: 'checking',
      balance,
      is_active: true
    });

    res.status(201).json({
      success: true,
      bankAccount: newBankAccount.toSummaryJSON(),
      message: 'Bank account linked successfully'
    });

  } catch (error) {
    console.error('Link account error:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error'
    });
  }
});

// Get user's bank accounts
router.get('/accounts', authenticateToken, async (req, res) => {
  try {
    const bankAccounts = await BankAccount.findByUserId(req.user.id);

    res.json({
      success: true,
      bankAccounts: bankAccounts.map(account => account.toSummaryJSON())
    });

  } catch (error) {
    console.error('Get accounts error:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error'
    });
  }
});

// Advanced Transfer funds with multiple features
router.post('/transfer', authenticateToken, [
  body('amount').isFloat({ min: 0.01 }),
  body('description').optional().isLength({ max: 255 }),
  body('category').notEmpty().isIn(['Food', 'Travel', 'Bills', 'Shopping', 'Entertainment', 'Health', 'Transfers', 'Education', 'Grocery', 'Rent', 'EMI', 'Utilities', 'Income', 'Other']),
  body('pin').isLength({ min: 4, max: 4 }).isNumeric(),
  body('senderAccountId').optional().isString(),
  // Recipient identification - at least one must be provided
  body().custom((value) => {
    const { recipientPublicId, recipientAccountNumber, recipientProfileUrl, recipientMobileNumber, recipientEmail } = value;
    if (!recipientPublicId && !recipientAccountNumber && !recipientProfileUrl && !recipientMobileNumber && !recipientEmail) {
      throw new Error('At least one recipient identifier is required (email, account number, customer ID, mobile number, or profile URL)');
    }
    return true;
  })
], async (req, res) => {
  try {
    console.log('Transfer request body:', req.body);
    console.log('Transfer request user:', req.user);

    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      console.log('Validation errors:', errors.array());
      return res.status(400).json({
        success: false,
        error: errors.array()[0].msg
      });
    }

    const {
      recipientPublicId,
      recipientAccountNumber,
      recipientProfileUrl,
      recipientMobileNumber,
      recipientEmail,
      amount,
      description = '',
      category,
      pin,
      senderAccountId
    } = req.body;

    const senderId = req.user.id;

    // Verify PIN before proceeding
    const sender = await User.findById(senderId);
    if (!sender) {
      return res.status(404).json({
        success: false,
        error: 'User not found'
      });
    }

    const isPINValid = await sender.comparePIN(pin);
    if (!isPINValid) {
      return res.status(401).json({
        success: false,
        error: 'Invalid PIN'
      });
    }

    // Resolve recipient by multiple methods
    let recipient = null;
    console.log('Looking for recipient with:', { recipientPublicId, recipientAccountNumber, recipientProfileUrl, recipientMobileNumber, recipientEmail });

    // Method 1: Search by email (NEW - unique identifier)
    if (recipientEmail && !recipient) {
      recipient = await User.findByEmail(recipientEmail);
      console.log('Found user by email:', recipient ? recipient.name : null);
    }

    // Method 2: Search by user's unique account number (NEW - unique identifier)
    if (recipientAccountNumber && !recipient) {
      recipient = await User.findByAccountNumber(recipientAccountNumber);
      console.log('Found user by account number:', recipient ? recipient.name : null);
    }

    // Method 3: Search by customer ID
    if (recipientPublicId && !recipient) {
      recipient = await User.findByCustomerId(recipientPublicId);
      console.log('Found user by customer ID:', recipient ? recipient.name : null);
    }

    // Method 4: Search by profile URL
    if (recipientProfileUrl && !recipient) {
      recipient = await User.findByPublicUrl(recipientProfileUrl);
      console.log('Found user by profile URL:', recipient ? recipient.name : null);
    }

    // Method 5: Search by mobile number
    if (recipientMobileNumber && !recipient) {
      recipient = await User.findByMobileNumber(recipientMobileNumber);
      console.log('Found user by mobile number:', recipient ? recipient.name : null);
    }

    if (!recipient) {
      return res.status(404).json({
        success: false,
        error: 'Recipient not found. Please check the email, account number, or other details.'
      });
    }

    console.log('Found recipient:', {
      id: recipient.id,
      name: recipient.name,
      email: recipient.email,
      account_number: recipient.account_number,
      customer_id: recipient.customer_id
    });

    if (recipient.id === senderId) {
      return res.status(400).json({
        success: false,
        error: 'Cannot transfer to yourself'
      });
    }

    // Check sender's balance
    const senderAccounts = await BankAccount.findByUserId(senderId);

    console.log('Sender accounts:', senderAccounts.length, 'accounts found');

    if (senderAccounts.length === 0) {
      return res.status(400).json({
        success: false,
        error: 'No active bank accounts found'
      });
    }

    const totalBalance = senderAccounts.reduce((sum, account) => sum + parseFloat(account.balance), 0);
    if (totalBalance < amount) {
      return res.status(400).json({
        success: false,
        error: 'Insufficient funds'
      });
    }

    // Pick sender account (specified or first)
    let senderAccount = senderAccounts[0];
    if (senderAccountId) {
      const match = senderAccounts.find(a => String(a.id) === String(senderAccountId));
      if (match) senderAccount = match;
    }

    // Create sender's transaction record (money going out)
    const senderTransaction = await Transaction.create({
      transaction_id: `TXN_${Math.random().toString(36).substr(2, 12).toUpperCase()}`,
      user_id: senderId,
      related_user_id: recipient.id,
      sender_id: senderId,
      recipient_id: recipient.id,
      amount: amount,
      category: category || 'Other',
      description: description || `Transfer to ${recipient.name || recipient.email || 'Unknown'}`,
      status: 'completed',
      transaction_type: 'transfer',
      direction: 'sent',
      priority: 'normal',
      processing_fee: 0,
      fraud_status: amount > 5000 ? 'PENDING' : 'NONE',
      flagged_reason: amount > 5000 ? 'High transaction amount (> $5000)' : null,
      sender_account_id: senderAccount.id
    });

    // Create recipient's transaction record (money coming in)
    await Transaction.create({
      transaction_id: `TXN_${Math.random().toString(36).substr(2, 12).toUpperCase()}`,
      user_id: recipient.id,
      related_user_id: senderId,
      sender_id: senderId,
      recipient_id: recipient.id,
      amount: amount,
      category: 'Transfers',
      description: `Payment from ${sender.name || sender.email || 'Unknown'}`,
      status: 'completed',
      transaction_type: 'deposit',
      direction: 'received',
      priority: 'normal',
      processing_fee: 0,
      fraud_status: 'NONE'
    });

    // Update sender's account balance (deduct)
    await BankAccount.updateBalance(senderAccount.id, -amount);

    // Update or create recipient's account balance
    const recipientAccounts = await BankAccount.findByUserId(recipient.id);

    if (recipientAccounts.length > 0) {
      // Add to first account
      await BankAccount.updateBalance(recipientAccounts[0].id, amount);
    } else {
      // Create a default account for recipient
      await BankAccount.create({
        user_id: recipient.id,
        bank_name: 'Primary Account',
        institution: 'NeoBank',
        account_number: `****${Math.floor(Math.random() * 9000) + 1000}`,
        account_type: 'checking',
        balance: amount,
        is_active: true
      });
    }

    res.status(201).json({
      success: true,
      transaction: senderTransaction.toDetailedJSON(),
      message: 'Transfer completed successfully'
    });

  } catch (error) {
    console.error('Transfer error:', error);
    console.error('Error stack:', error.stack);
    res.status(500).json({
      success: false,
      error: 'Internal server error',
      details: error.message
    });
  }
});

// Get transaction history
router.get('/transactions', authenticateToken, async (req, res) => {
  try {
    const page = parseInt(req.query.page || '1', 10);
    const limit = parseInt(req.query.limit || '20', 10);

    const transactions = await Transaction.findByUserId(req.user.id, page, limit);
    const total = await Transaction.countByUserId(req.user.id);

    res.json({
      success: true,
      transactions: transactions.map(transaction => transaction.toDetailedJSON()),
      pagination: {
        page: page,
        limit: limit,
        total,
        pages: Math.ceil(total / limit)
      }
    });

  } catch (error) {
    console.error('Get transactions error:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error'
    });
  }
});

// Personalized Insights: Income-based spending analysis
router.get('/ai-insights', authenticateToken, async (req, res) => {
  try {
    const userId = req.user.id;

    // Date ranges for current month
    const now = new Date();
    const firstOfThisMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const threeMonthsAgo = new Date(now.getFullYear(), now.getMonth() - 3, 1);

    // Get user's current balance
    const userAccounts = await BankAccount.findByUserId(userId);
    const userBalance = userAccounts.reduce((sum, account) => sum + parseFloat(account.balance), 0);

    // Get transactions for analysis
    const allTransactions = await Transaction.findByUserIdAndDateRange(userId, threeMonthsAgo, now);

    // Get category spending for current month
    const categorySpendData = await Transaction.getSpendingByCategory(userId, firstOfThisMonth, now);
    const categorySpendMap = categorySpendData.reduce((acc, row) => {
      acc[row.category || 'Other'] = parseFloat(row.total) || 0;
      return acc;
    }, {});

    // Generate insights
    const suggestions = [];
    const totalSpending = Object.values(categorySpendMap).reduce((sum, val) => sum + val, 0);

    if (totalSpending > userBalance * 0.7) {
      suggestions.push({
        title: 'High Spending Alert',
        detail: 'You have spent more than 70% of your balance this month. Consider reducing expenses.',
        category: 'Spending',
        priority: 'high',
        emoji: '⚠️',
        type: 'warning'
      });
    }

    // Find highest spending category
    const sortedCategories = Object.entries(categorySpendMap).sort((a, b) => b[1] - a[1]);
    if (sortedCategories.length > 0) {
      const [topCategory, topAmount] = sortedCategories[0];
      suggestions.push({
        title: `Highest Spending: ${topCategory}`,
        detail: `You spent ₹${topAmount.toFixed(2)} on ${topCategory} this month.`,
        category: topCategory,
        priority: 'medium',
        emoji: '💰',
        type: 'info'
      });
    }

    if (userBalance > 10000) {
      suggestions.push({
        title: 'Savings Opportunity',
        detail: 'Consider moving some funds to a savings account to earn interest.',
        category: 'Savings',
        priority: 'low',
        emoji: '💡',
        type: 'tip'
      });
    }

    res.json({
      success: true,
      period: {
        this_month: { start: firstOfThisMonth, end: now }
      },
      category_spend: categorySpendMap,
      suggestions,
      spending_summary: {
        total_balance: userBalance,
        current_spending: totalSpending,
        remaining_balance: userBalance,
        balance_usage_percentage: userBalance > 0 ? (totalSpending / userBalance) * 100 : 0
      }
    });
  } catch (error) {
    console.error('Personalized insights error:', error);
    res.status(500).json({ success: false, error: 'Internal server error' });
  }
});

// Get account balance
router.get('/balance', authenticateToken, async (req, res) => {
  try {
    const accounts = await BankAccount.findByUserId(req.user.id);
    const totalBalance = accounts.reduce((sum, account) => sum + parseFloat(account.balance), 0);

    res.json({
      success: true,
      accounts: accounts.map(account => account.toSummaryJSON()),
      totalBalance
    });

  } catch (error) {
    console.error('Get balance error:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error'
    });
  }
});

// Simulate an incoming credit and record it in transaction history
router.post('/simulate-credit', authenticateToken, async (req, res) => {
  try {
    // Find a target account
    const account = await BankAccount.findFirstByUserId(req.user.id);
    if (!account) {
      return res.status(400).json({ success: false, error: 'No active bank accounts found' });
    }

    // Determine credit amount
    const amount = Math.max(0.01, Number(req.body?.amount) || Math.round((Math.random() * 90 + 10) * 100) / 100);

    // Update balance
    await BankAccount.updateBalance(account.id, amount);

    // Create a deposit transaction for the user
    const creditTxn = await Transaction.create({
      transaction_id: `TXN_${Math.random().toString(36).substr(2, 12).toUpperCase()}`,
      user_id: req.user.id,
      related_user_id: req.user.id,
      sender_id: req.user.id,
      recipient_id: req.user.id,
      amount,
      description: req.body?.description || 'Automated credit',
      status: 'completed',
      transaction_type: 'deposit',
      direction: 'received',
      priority: 'normal',
      processing_fee: 0,
      fraud_status: 'NONE'
    });

    const accounts = await BankAccount.findByUserId(req.user.id);
    const totalBalance = accounts.reduce((sum, a) => sum + parseFloat(a.balance), 0);

    res.status(201).json({
      success: true,
      transaction: creditTxn.toDetailedJSON(),
      totalBalance
    });
  } catch (error) {
    console.error('Simulate credit error:', error);
    res.status(500).json({ success: false, error: 'Internal server error' });
  }
});

// Find user by public ID, email, account number, mobile number, or other identifier (for transfers)
router.get('/find-user/:identifier', async (req, res) => {
  try {
    const { identifier } = req.params;
    let user = null;

    // Method 1: Email lookup (new - unique identifier)
    if (identifier.includes('@')) {
      user = await User.findByEmail(identifier);
    }

    // Method 2: Account number lookup (new - unique identifier)
    if (!user && identifier.startsWith('NB')) {
      user = await User.findByAccountNumber(identifier);
    }

    // Method 3: Customer ID
    if (!user) {
      user = await User.findByCustomerId(identifier);
    }

    // Method 4: Public URL
    if (!user) {
      user = await User.findByPublicUrl(identifier);
    }

    // Method 5: Mobile number
    if (!user) {
      user = await User.findByMobileNumber(identifier);
    }

    if (!user) {
      return res.status(404).json({
        success: false,
        error: 'User not found'
      });
    }

    res.json({
      success: true,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        customer_id: user.customer_id,
        public_id: user.customer_id,
        public_url: user.public_url,
        account_number: user.account_number,
        profile_url: user.public_url,
        phone_number: user.phone_number
      }
    });

  } catch (error) {
    console.error('Find user error:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error'
    });
  }
});

module.exports = router;