CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    balance NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    currency VARCHAR(10) NOT NULL,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    type VARCHAR(20) NOT NULL, -- DEBIT, CREDIT
    amount NUMERIC(19, 4) NOT NULL,
    description VARCHAR(255) NOT NULL,
    balance_after NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ledger_wallet ON ledger_entries(wallet_id);
CREATE INDEX idx_ledger_transaction ON ledger_entries(transaction_id);
