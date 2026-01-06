import React, { useState, useEffect } from 'react';
import {
    Shield,
    Users,
    Activity,
    FileText,
    AlertTriangle,
    CheckCircle,
    XCircle,
    Info,
    Search,
    RefreshCw,
    MoreVertical,
    Flag,
    UserCheck,
    UserMinus,
    ArrowRight,
    TrendingUp,
    AlertCircle
} from 'lucide-react';
import { adminAPI } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const AdminDashboard = () => {
    const { token } = useAuth();
    const [activeTab, setActiveTab] = useState('flagged');
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState({
        flaggedTransactions: [],
        allTransactions: [],
        users: [],
        auditLogs: [],
        pagination: {
            currentPage: 0,
            totalPages: 0,
            totalItems: 0
        }
    });
    const [stats, setStats] = useState({
        totalUsers: 0,
        flaggedCount: 0,
        totalTransactions: 0,
        systemHealth: 'Optimal'
    });

    const fetchData = async (tab, page = 0) => {
        setLoading(true);
        try {
            let response;
            switch (tab) {
                case 'flagged':
                    response = await adminAPI.getFlaggedTransactions(page, 10, token);
                    setData(prev => ({
                        ...prev,
                        flaggedTransactions: response.transactions || [],
                        pagination: response.pagination ? {
                            currentPage: response.pagination.current_page || 0,
                            totalPages: response.pagination.total_pages || 0,
                            totalItems: response.pagination.total_items || 0
                        } : prev.pagination
                    }));
                    setStats(prev => ({ ...prev, flaggedCount: response.pagination?.total_items || 0 }));
                    break;
                case 'all-transactions':
                    response = await adminAPI.getAllTransactions(page, 10, token);
                    setData(prev => ({
                        ...prev,
                        allTransactions: response.transactions || [],
                        pagination: response.pagination ? {
                            currentPage: response.pagination.current_page || 0,
                            totalPages: response.pagination.total_pages || 0,
                            totalItems: response.pagination.total_items || 0
                        } : prev.pagination
                    }));
                    setStats(prev => ({ ...prev, totalTransactions: response.pagination?.total_items || 0 }));
                    break;
                case 'users':
                    response = await adminAPI.getAllUsers(page, 10, token);
                    setData(prev => ({
                        ...prev,
                        users: response.users || [],
                        pagination: response.pagination ? {
                            currentPage: response.pagination.current_page || 0,
                            totalPages: response.pagination.total_pages || 0,
                            totalItems: response.pagination.total_items || 0
                        } : prev.pagination
                    }));
                    setStats(prev => ({ ...prev, totalUsers: response.pagination?.total_items || 0 }));
                    break;
                case 'audit':
                    response = await adminAPI.getAuditLogs(token);
                    // Audit logs doesn't have pagination in my current backend implementation
                    setData(prev => ({ ...prev, auditLogs: response }));
                    break;
                default:
                    break;
            }
        } catch (error) {
            console.error('Error fetching admin data:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData(activeTab);
    }, [activeTab, token]);

    const handleTransactionAction = async (id, status, reason = 'Administrative decision') => {
        try {
            await adminAPI.updateTransactionFraudStatus(id, status, reason, token);
            fetchData(activeTab);
        } catch (error) {
            alert('Error updating transaction: ' + error.message);
        }
    };

    const handleUserStatusChange = async (id, status, reason = 'Administrative decision') => {
        try {
            await adminAPI.updateUserStatus(id, status, reason, token);
            fetchData(activeTab);
        } catch (error) {
            alert('Error updating user status: ' + error.message);
        }
    };

    const renderStats = () => (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center space-x-4">
                <div className="p-3 bg-blue-50 rounded-xl">
                    <Users className="h-6 w-6 text-blue-600" />
                </div>
                <div>
                    <p className="text-sm text-gray-500 font-medium">Total Users</p>
                    <h3 className="text-2xl font-bold text-gray-900">{stats.totalUsers}</h3>
                </div>
            </div>
            <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center space-x-4">
                <div className="p-3 bg-red-50 rounded-xl">
                    <AlertTriangle className="h-6 w-6 text-red-600" />
                </div>
                <div>
                    <p className="text-sm text-gray-500 font-medium">Flagged Transactions</p>
                    <h3 className="text-2xl font-bold text-gray-900">{stats.flaggedCount}</h3>
                </div>
            </div>
            <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center space-x-4">
                <div className="p-3 bg-green-50 rounded-xl">
                    <TrendingUp className="h-6 w-6 text-green-600" />
                </div>
                <div>
                    <p className="text-sm text-gray-500 font-medium">Total Operations</p>
                    <h3 className="text-2xl font-bold text-gray-900">{stats.totalTransactions}</h3>
                </div>
            </div>
            <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center space-x-4">
                <div className="p-3 bg-indigo-50 rounded-xl">
                    <Activity className="h-6 w-6 text-indigo-600" />
                </div>
                <div>
                    <p className="text-sm text-gray-500 font-medium">System Health</p>
                    <h3 className="text-2xl font-bold text-green-600">{stats.systemHealth}</h3>
                </div>
            </div>
        </div>
    );

    const renderFlaggedTransactions = () => (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50/50">
                <h2 className="text-lg font-bold text-gray-900 flex items-center">
                    <Flag className="h-5 w-5 text-red-500 mr-2" />
                    Suspicious Transaction Review
                </h2>
                <button onClick={() => fetchData('flagged')} className="p-2 hover:bg-white rounded-full transition-colors border border-gray-200 shadow-sm">
                    <RefreshCw className={`h-5 w-5 ${loading ? 'animate-spin' : ''} text-gray-500`} />
                </button>
            </div>
            <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="bg-gray-50">
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Transaction ID</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Amount</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Details</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Reason Flagged</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {data.flaggedTransactions.length === 0 ? (
                            <tr>
                                <td colSpan="5" className="px-6 py-12 text-center text-gray-500">
                                    <div className="flex flex-col items-center">
                                        <CheckCircle className="h-10 w-10 text-green-400 mb-2" />
                                        <p className="font-medium text-lg">No suspicious transactions found</p>
                                        <p className="text-sm">Everything looks good for now.</p>
                                    </div>
                                </td>
                            </tr>
                        ) : (
                            data.flaggedTransactions.map((tx) => (
                                <tr key={tx.id} className="hover:bg-gray-50 transition-colors">
                                    <td className="px-6 py-4 font-mono text-sm text-gray-600">{tx.transaction_id}</td>
                                    <td className="px-6 py-4">
                                        <span className="text-lg font-bold text-gray-900">${tx.amount.toFixed(2)}</span>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="text-sm">
                                            <p className="font-semibold text-gray-800">{tx.category}</p>
                                            <p className="text-gray-500">{tx.description}</p>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className="px-3 py-1 bg-amber-50 text-amber-700 text-xs font-semibold rounded-full border border-amber-100">
                                            {tx.flagged_reason || 'High Value / Rapid Transfer'}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center justify-center space-x-2">
                                            <button
                                                onClick={() => handleTransactionAction(tx.id, 'APPROVED')}
                                                className="p-2 bg-green-50 text-green-600 hover:bg-green-100 rounded-lg transition-colors border border-green-100 shadow-sm"
                                                title="Approve"
                                            >
                                                <CheckCircle className="h-5 w-5" />
                                            </button>
                                            <button
                                                onClick={() => handleTransactionAction(tx.id, 'BLOCKED')}
                                                className="p-2 bg-red-50 text-red-600 hover:bg-red-100 rounded-lg transition-colors border border-red-100 shadow-sm"
                                                title="Block"
                                            >
                                                <XCircle className="h-5 w-5" />
                                            </button>
                                            <button
                                                onClick={() => handleTransactionAction(tx.id, 'INVESTIGATING')}
                                                className="p-2 bg-blue-50 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors border border-blue-100 shadow-sm"
                                                title="Investigate"
                                            >
                                                <Info className="h-5 w-5" />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );

    const renderAllTransactions = () => (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-100 flex justify-between items-center">
                <h2 className="text-lg font-bold text-gray-900">Transaction Monitoring</h2>
                <div className="flex space-x-2">
                    <div className="relative">
                        <Search className="h-5 w-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                        <input
                            type="text"
                            placeholder="Search transactions..."
                            className="pl-10 pr-4 py-2 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none"
                        />
                    </div>
                    <button onClick={() => fetchData('all-transactions')} className="p-2 hover:bg-gray-50 rounded-xl transition-colors border border-gray-200">
                        <RefreshCw className={`h-5 w-5 ${loading ? 'animate-spin' : ''} text-gray-500`} />
                    </button>
                </div>
            </div>
            <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="bg-gray-50">
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">ID</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Amount</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Type / Category</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Date</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {data.allTransactions.map((tx) => (
                            <tr key={tx.id} className="hover:bg-gray-50 transition-colors">
                                <td className="px-6 py-4 font-mono text-sm text-gray-600">{tx.transaction_id}</td>
                                <td className="px-6 py-4">
                                    <span className={`font-bold ${tx.direction === 'sent' ? 'text-red-600' : 'text-green-600'}`}>
                                        {tx.direction === 'sent' ? '-' : '+'}${tx.amount.toFixed(2)}
                                    </span>
                                </td>
                                <td className="px-6 py-4">
                                    <span className={`px-2 py-1 text-xs font-bold rounded-full ${tx.status === 'COMPLETED' ? 'bg-green-100 text-green-700' :
                                        tx.status === 'PENDING' ? 'bg-amber-100 text-amber-700' : 'bg-red-100 text-red-700'
                                        }`}>
                                        {tx.status}
                                    </span>
                                </td>
                                <td className="px-6 py-4 text-sm font-medium text-gray-700">{tx.type} / {tx.category}</td>
                                <td className="px-6 py-4 text-sm text-gray-500">{new Date(tx.created_at).toLocaleString()}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );

    const renderUsers = () => (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-100 flex justify-between items-center">
                <h2 className="text-lg font-bold text-gray-900">Account Control / User Management</h2>
                <button onClick={() => fetchData('users')} className="p-2 hover:bg-gray-50 rounded-xl border border-gray-200">
                    <RefreshCw className={`h-5 w-5 ${loading ? 'animate-spin' : ''} text-gray-500`} />
                </button>
            </div>
            <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="bg-gray-50">
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">User</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Customer ID</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Role</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {data.users.map((user) => (
                            <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                                <td className="px-6 py-4">
                                    <div className="flex items-center space-x-3">
                                        <div className="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold">
                                            {user.name.charAt(0)}
                                        </div>
                                        <div>
                                            <p className="font-semibold text-gray-900">{user.name}</p>
                                            <p className="text-xs text-gray-500">{user.email}</p>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-6 py-4 font-mono text-sm">{user.customer_id}</td>
                                <td className="px-6 py-4">
                                    <span className={`px-2 py-1 text-xs font-bold rounded-full ${user.status === 'ACTIVE' ? 'bg-green-100 text-green-700' :
                                        user.status === 'BLOCKED' ? 'bg-red-100 text-red-700' : 'bg-amber-100 text-amber-700'
                                        }`}>
                                        {user.status || 'ACTIVE'}
                                    </span>
                                </td>
                                <td className="px-6 py-4 text-sm">{user.role}</td>
                                <td className="px-6 py-4 text-center">
                                    <div className="flex justify-center space-x-2">
                                        {user.status !== 'ACTIVE' && (
                                            <button
                                                onClick={() => handleUserStatusChange(user.id, 'ACTIVE')}
                                                className="p-1.5 text-green-600 hover:bg-green-50 rounded-lg transition-colors border border-green-100"
                                                title="Activate"
                                            >
                                                <UserCheck className="h-5 w-5" />
                                            </button>
                                        )}
                                        {user.status !== 'FROZEN' && (
                                            <button
                                                onClick={() => handleUserStatusChange(user.id, 'FROZEN')}
                                                className="p-1.5 text-amber-600 hover:bg-amber-50 rounded-lg transition-colors border border-amber-100"
                                                title="Freeze"
                                            >
                                                <RefreshCw className="h-5 w-5" />
                                            </button>
                                        )}
                                        {user.status !== 'BLOCKED' && (
                                            <button
                                                onClick={() => handleUserStatusChange(user.id, 'BLOCKED')}
                                                className="p-1.5 text-red-600 hover:bg-red-50 rounded-lg transition-colors border border-red-100"
                                                title="Block"
                                            >
                                                <UserMinus className="h-5 w-5" />
                                            </button>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );

    const renderAuditLogs = () => (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-100 bg-gray-50/50">
                <h2 className="text-lg font-bold text-gray-900 flex items-center">
                    <FileText className="h-5 w-5 text-gray-500 mr-2" />
                    Audit and Compliance Logs
                </h2>
            </div>
            <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="bg-gray-50">
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Date</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Admin</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Action</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Target</th>
                            <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Reason / Details</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {data.auditLogs.map((log) => (
                            <tr key={log.id} className="hover:bg-gray-50 transition-colors">
                                <td className="px-6 py-4 text-sm text-gray-500">{new Date(log.created_at).toLocaleString()}</td>
                                <td className="px-6 py-4 font-medium text-gray-900 text-sm">{log.admin_email}</td>
                                <td className="px-6 py-4">
                                    <span className="px-2 py-1 bg-gray-100 text-gray-700 text-xs font-bold rounded uppercase">
                                        {log.action.replace(/_/g, ' ')}
                                    </span>
                                </td>
                                <td className="px-6 py-4 text-sm">{log.target_type} #{log.target_id}</td>
                                <td className="px-6 py-4 text-sm text-gray-600">
                                    <p className="font-semibold">{log.reason}</p>
                                    <p className="text-xs text-gray-400">{log.details}</p>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );

    return (
        <div className="min-h-screen bg-gray-50 pb-12">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-8">
                <div className="flex justify-between items-center mb-8">
                    <div>
                        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center">
                            <Shield className="h-8 w-8 text-blue-600 mr-3" />
                            Administrative Control Center
                        </h1>
                        <p className="mt-2 text-gray-600 font-medium">System supervisory authority and operations monitoring.</p>
                    </div>
                    <div className="flex space-x-3">
                        <div className="px-4 py-2 bg-white rounded-xl shadow-sm border border-gray-100 flex items-center">
                            <span className="w-2 h-2 bg-green-500 rounded-full mr-2 animate-pulse"></span>
                            <span className="text-sm font-bold text-gray-700">Live Security Monitoring</span>
                        </div>
                    </div>
                </div>

                {renderStats()}

                {/* Tab Navigation */}
                <div className="flex space-x-1 mb-8 bg-white p-1.5 rounded-2xl shadow-sm border border-gray-100 max-w-fit">
                    {[
                        { id: 'flagged', label: 'Fraud Alerts', icon: AlertTriangle },
                        { id: 'all-transactions', label: 'Transactions', icon: Activity },
                        { id: 'users', label: 'User Control', icon: Users },
                        { id: 'audit', label: 'Audit Logs', icon: FileText }
                    ].map((tab) => (
                        <button
                            key={tab.id}
                            onClick={() => setActiveTab(tab.id)}
                            className={`
                flex items-center space-x-2 px-6 py-2.5 rounded-xl text-sm font-bold transition-all duration-200
                ${activeTab === tab.id
                                    ? 'bg-blue-600 text-white shadow-lg shadow-blue-200'
                                    : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'
                                }
              `}
                        >
                            <tab.icon className="h-4 w-4" />
                            <span>{tab.label}</span>
                        </button>
                    ))}
                </div>

                {/* Tab Content */}
                <div className="transition-all duration-300">
                    {activeTab === 'flagged' && renderFlaggedTransactions()}
                    {activeTab === 'all-transactions' && renderAllTransactions()}
                    {activeTab === 'users' && renderUsers()}
                    {activeTab === 'audit' && renderAuditLogs()}
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
