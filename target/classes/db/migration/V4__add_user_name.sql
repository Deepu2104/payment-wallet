-- Add name column to users table
ALTER TABLE users ADD COLUMN name VARCHAR(255);

-- Update existing users to have a default name (email prefix)
UPDATE users SET name = SPLIT_PART(email, '@', 1) WHERE name IS NULL;

-- Make name column NOT NULL after setting defaults
ALTER TABLE users ALTER COLUMN name SET NOT NULL;
