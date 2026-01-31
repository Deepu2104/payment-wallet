import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Lock, Mail, UserPlus, Loader2 } from 'lucide-react';
import SuccessAnimation from '../components/SuccessAnimation';

const Register: React.FC = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            const response = await api.post('/auth/register', { name, email, password });
            login(response.data.accessToken);
            setSuccess(true);
            setTimeout(() => {
                navigate('/dashboard');
            }, 2000);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Registration failed. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    if (success) {
        return (
            <div className="min-h-screen flex items-center justify-center p-4">
                <SuccessAnimation
                    title="Account Created"
                    subtitle="Your wallet is ready! Redirecting..."
                />
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
            {/* Background Decor */}
            <div className="absolute top-0 left-0 w-full h-full overflow-hidden -z-10">
                <div className="blob blob-purple" />
                <div className="blob blob-cyan" />
            </div>

            <div className="glass-panel p-8 rounded-2xl w-full max-w-md animate-fade-in">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold mb-2 text-gradient">Create Account</h1>
                    <p className="text-gray-400">Join us and start sending money</p>
                </div>

                {error && (
                    <div className="error-box">
                        <span className="error-dot" />
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div className="mb-6">
                        <label className="block text-sm font-medium text-gray-300 ml-1 mb-2">Full Name</label>
                        <div className="relative">
                            <UserPlus className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                            <input
                                type="text"
                                required
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="glass-input pl-12"
                                placeholder="John Doe"
                            />
                        </div>
                    </div>

                    <div className="mb-6">
                        <label className="block text-sm font-medium text-gray-300 ml-1 mb-2">Email</label>
                        <div className="relative">
                            <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                            <input
                                type="email"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="glass-input pl-12"
                                placeholder="name@example.com"
                            />
                        </div>
                    </div>

                    <div className="mb-6">
                        <label className="block text-sm font-medium text-gray-300 ml-1 mb-2">Password</label>
                        <div className="relative">
                            <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                            <input
                                type="password"
                                required
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="glass-input pl-12"
                                placeholder="••••••••"
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="glass-button w-full mt-2 group"
                    >
                        {isLoading ? (
                            <Loader2 className="w-5 h-5 animate-spin" />
                        ) : (
                            <>
                                Sign Up
                                <UserPlus className="w-5 h-5 group-hover-translate-x-1 transition-transform ml-2" />
                            </>
                        )}
                    </button>
                </form>

                <p className="mt-8 text-center text-sm text-gray-400">
                    Already have an account?{' '}
                    <Link to="/login" className="text-cyan-400 hover:text-cyan-300 font-medium transition-colors">
                        Sign in
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Register;
