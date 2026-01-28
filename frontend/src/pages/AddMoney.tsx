import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { ArrowLeft, Plus, DollarSign, Loader2 } from 'lucide-react';
import SuccessAnimation from '../components/SuccessAnimation';

const AddMoney: React.FC = () => {
    const [amount, setAmount] = useState('');
    const [description, setDescription] = useState('Manual Deposit');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            await api.post('/wallet/add-money', {
                amount: parseFloat(amount),
                description
            });
            setSuccess(true);
            setTimeout(() => {
                navigate('/dashboard');
            }, 2000);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to add money. Please try again.');
            setLoading(false);
        }
    };

    if (success) {
        return (
            <div className="min-h-screen flex items-center justify-center p-4">
                <SuccessAnimation
                    title="Money Added Successfully"
                    subtitle={`USD ${parseFloat(amount).toFixed(2)} added to your wallet`}
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
                        <div className="w-10 h-10 rounded-xl bg-green-500/20 flex items-center justify-center">
                            <Plus className="w-5 h-5 text-green-400" />
                        </div>
                        <div>
                            <h1 className="text-xl font-bold">Add Money</h1>
                            <p className="text-xs text-gray-400">Refill your wallet</p>
                        </div>
                    </div>

                    {error && (
                        <div className="error-box mb-6">
                            <span className="error-dot" />
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="space-y-2">
                            <label className="text-sm font-medium text-gray-300 ml-1">Amount</label>
                            <div className="relative">
                                <DollarSign className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                                <input
                                    type="number"
                                    required
                                    min="1"
                                    step="0.01"
                                    value={amount}
                                    onChange={(e) => setAmount(e.target.value)}
                                    className="glass-input w-full pl-12 text-lg font-semibold tracking-wide"
                                    placeholder="0.00"
                                    autoFocus
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-medium text-gray-300 ml-1">Source / Description</label>
                            <input
                                type="text"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                className="glass-input w-full bg-white/5"
                                placeholder="Bank Transfer, Card, etc."
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="glass-button w-full flex items-center justify-center gap-2 mt-4"
                        >
                            {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Confirm Deposit'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AddMoney;
