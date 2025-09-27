-- Create the pricing_config table
CREATE TABLE IF NOT EXISTS pricing_config
(
    item_key                VARCHAR(20) PRIMARY KEY,
    price_in_cents          INTEGER NOT NULL CHECK (price_in_cents > 0),
    offer_quantity          INTEGER CHECK (offer_quantity IS NULL OR offer_quantity >= 2),
    offer_savings_in_cents  INTEGER CHECK (offer_savings_in_cents IS NULL OR offer_savings_in_cents > 0),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT valid_offer  CHECK (
        (offer_quantity IS NULL AND offer_savings_in_cents IS NULL) OR
        (offer_quantity IS NOT NULL AND offer_savings_in_cents IS NOT NULL)
        )
);

-- Insert initial pricing data matching current application.properties configuration
-- Apple: 30 cents each, 2 for 45 cents (savings: 2*30-45 = 15 cents)
INSERT INTO pricing_config (item_key, price_in_cents, offer_quantity, offer_savings_in_cents)
SELECT 'apple', 30, 2, 15
WHERE NOT EXISTS (SELECT 1 FROM pricing_config WHERE item_key = 'apple');

-- Banana: 50 cents each, 3 for 130 cents (savings: 3*50-130 = 20 cents)
INSERT INTO pricing_config (item_key, price_in_cents, offer_quantity, offer_savings_in_cents)
SELECT 'banana', 50, 3, 20
WHERE NOT EXISTS (SELECT 1 FROM pricing_config WHERE item_key = 'banana');

-- Peach: 60 cents each, no offer
INSERT INTO pricing_config (item_key, price_in_cents, offer_quantity, offer_savings_in_cents)
SELECT 'peach', 60, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM pricing_config WHERE item_key = 'peach');

-- Kiwi: 20 cents each, no offer
INSERT INTO pricing_config (item_key, price_in_cents, offer_quantity, offer_savings_in_cents)
SELECT 'kiwi', 20, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM pricing_config WHERE item_key = 'kiwi');