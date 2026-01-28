import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { LogOut, Send, Plus, ArrowUpRight, ArrowDownLeft, Wallet, Loader2 } from 'lucide-react';

interface WalletData {
    balance: number;
    currency: string;
}

interface Transaction {
    id: string;
    type: 'CREDIT' | 'DEBIT';
    amount: number;
    description: string;
    createdAt: string;
}

const Dashboard: React.FC = () => {
    const [wallet, setWallet] = useState<WalletData | null>(null);
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [loading, setLoading] = useState(true);
    const { logout } = useAuth();
    const navigate = useNavigate();

    const fetchData = async () => {
        setLoading(true);
        try {
            const [walletRes, statementRes] = await Promise.all([
                api.get('/wallet/balance'),
                api.get('/wallet/statement')
            ]);
            setWallet(walletRes.data);
            setTransactions(statementRes.data);
        } catch (error) {
            console.error('Failed to fetch dashboard data', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <Loader2 className="w-8 h-8 animate-spin text-purple-500" />
            </div>
        );
    }

    return (
        <div className="min-h-screen pb-20">
            {/* Navbar */}
            <nav className="border-b border-white/10 bg-black/20 backdrop-blur-xl sticky top-0 z-50">
                <div className="max-w-5xl mx-auto px-4 h-16 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-purple-500 to-cyan-500 flex items-center justify-center">
                            <Wallet className="w-5 h-5 text-white" />
                        </div>
                        <span className="font-bold text-lg tracking-tight">PayWallet</span>
                    </div>
                    <button
                        onClick={handleLogout}
                        className="secondary-glass-button"
                    >
                        <LogOut className="w-4 h-4 mr-2" />
                        Logout
                    </button>
                </div>
            </nav>

            <div className="max-w-5xl mx-auto px-4 mt-8">
                {/* Balance Card */}
                <div className="glass-panel p-8 rounded-2xl relative overflow-hidden mb-8">
                    <div className="absolute top-0 right-0 p-8 opacity-20 transform translate-x-1/4 -translate-y-1/4">
                        <div className="w-64 h-64 bg-purple-500 rounded-full blur-[100px]" />
                    </div>

                    <div className="relative z-10">
                        <p className="text-gray-400 mb-2">Total Balance</p>
                        <h1 className="text-5xl font-bold mb-6 tracking-tight">
                            {wallet?.currency} {wallet?.balance.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                        </h1>

                        <div className="flex gap-4">
                            <Link to="/transfer" className="glass-button flex items-center gap-2 px-6">
                                <Send className="w-4 h-4" />
                                Send Money
                            </Link>
                            <Link
                                to="/add-money"
                                className="glass-button bg-none bg-white/5 hover:bg-white/10 flex items-center gap-2 px-6"
                            >
                                <Plus className="w-4 h-4" />
                                Add Money
                            </Link>
                        </div>
                    </div>
                </div>

                {/* Transactions */}
                <div className="grid gap-6">
                    <h2 className="text-xl font-semibold">Recent Transactions</h2>

                    {transactions.length === 0 ? (
                        <div className="text-center py-12 text-gray-500 glass-panel rounded-xl">
                            No transactions yet. Start by adding money!
                        </div>
                    ) : (
                        <div className="glass-panel rounded-xl overflow-hidden">
                            {transactions.map((tx) => (
                                <div key={tx.id} className="p-4 border-b border-white/5 last:border-0 hover:bg-white/5 transition-colors flex items-center justify-between group">
                                    <div className="flex items-center gap-4">
                                        <div className={`w-10 h-10 rounded-full flex items-center justify-center ${tx.type === 'CREDIT' ? 'bg-green-500/10 text-green-500' : 'bg-red-500/10 text-red-500'
                                            }`}>
                                            {tx.type === 'CREDIT' ? <ArrowDownLeft className="w-5 h-5" /> : <ArrowUpRight className="w-5 h-5" />}
                                        </div>
                                        <div>
                                            <p className="font-medium text-white">{tx.description}</p>
                                            <p className="text-xs text-gray-500">{new Date(tx.createdAt).toLocaleDateString()} • {new Date(tx.createdAt).toLocaleTimeString()}</p>
                                        </div>
                                    </div>
                                    <span className={`font-semibold ${tx.type === 'CREDIT' ? 'text-green-400' : 'text-white'
                                        }`}>
                                        {tx.type === 'CREDIT' ? '+' : '-'}{wallet?.currency} {tx.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                                    </span>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
