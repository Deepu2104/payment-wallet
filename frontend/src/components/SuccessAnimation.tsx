import React from 'react';

interface SuccessAnimationProps {
    title?: string;
    subtitle?: string;
}

const SuccessAnimation: React.FC<SuccessAnimationProps> = ({
    title = "Transfer Successful",
    subtitle = "Your money has been sent securely"
}) => {
    return (
        <div className="gpay-success-container">
            <svg
                className="checkmark"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 52 52"
                width="100"
                height="100"
            >
                <circle
                    className="checkmark-circle"
                    cx="26"
                    cy="26"
                    r="25"
                    fill="none"
                />
                <path
                    className="checkmark-check"
                    fill="none"
                    d="M14.1 27.2l7.1 7.2 16.7-16.8"
                />
            </svg>
            <h2 className="text-2xl font-bold mt-6 text-white text-center">{title}</h2>
            <p className="text-gray-400 mt-2 text-center">{subtitle}</p>
        </div>
    );
};

export default SuccessAnimation;
