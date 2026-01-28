import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { ArrowLeft, Send, Search, Loader2 } from 'lucide-react';
import SuccessAnimation from '../components/SuccessAnimation';

const Transfer: React.FC = () => {
    const [email, setEmail] = useState('');
    const [amount, setAmount] = useState('');
    const [note, setNote] = useState('');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const idempotencyKey = crypto.randomUUID();
            await api.post('/transactions/transfer', {
                receiverEmail: email,
                amount: parseFloat(amount),
                note
            }, {
                headers: {
                    'Idempotency-Key': idempotencyKey
                }
            });
            setSuccess(true);
            setTimeout(() => {
                navigate('/dashboard');
            }, 2000);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Transfer failed. Please check the details and try again.');
            setLoading(false);
        }
    };

    if (success) {
        return (
            <div className="min-h-screen flex items-center justify-center p-4">
                <SuccessAnimation
                    title="Transfer Successful"
                    subtitle="Your money has been sent securely"
                />
            </div>
        );
    }

    return (
        <div className="min-h-screen p-4 flex items-center justify-center">
            <div className="max-w-md w-full animate-fade-in">
                <button
                    onClick={() => navigate('/dashboard')}
                    className="secondary-glass-button mb-6"
                >
                    <ArrowLeft className="w-4 h-4 mr-2" />
                    Back to Dashboard
                </button>

                <div className="glass-panel p-8 rounded-2xl">
                    <div className="flex items-center gap-3 mb-6">
                        <div className="w-10 h-10 rounded-xl bg-purple-500/20 flex items-center justify-center">
                            <Send className="w-5 h-5 text-purple-400" />
                        </div>
                        <div>
                            <h1 className="text-xl font-bold">Send Money</h1>
                            <p className="text-xs text-gray-400">Secure transfer</p>
                        </div>
                    </div>

                    {error && (
                        <div className="bg-red-500/10 border border-red-500/20 text-red-200 p-3 rounded-lg mb-6 text-sm">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="space-y-2">
                            <label className="text-sm font-medium text-gray-300 ml-1">Recipient Email</label>
                            <div className="relative">
                                <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                                <input
                                    type="email"
                                    required
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="glass-input w-full pl-12 bg-white/5"
                                    placeholder="friend@example.com"
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-medium text-gray-300 ml-1">Amount</label>
                            <div className="relative">
                                <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 font-semibold">$</span>
                                <input
                                    type="number"
                                    required
                                    min="0.01"
                                    step="0.01"
                                    value={amount}
                                    onChange={(e) => setAmount(e.target.value)}
                                    className="glass-input w-full pl-12 text-lg font-semibold tracking-wide"
                                    placeholder="0.00"
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-medium text-gray-300 ml-1">Note (Optional)</label>
                            <input
                                type="text"
                                value={note}
                                onChange={(e) => setNote(e.target.value)}
                                className="glass-input w-full"
                                placeholder="Dinner, Rent, etc."
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="glass-button w-full flex items-center justify-center gap-2 mt-4"
                        >
                            {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Send Now'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Transfer;
