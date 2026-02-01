-- Seed menu items from restaurant menu

-- Steaks
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Chicken Tandoori Steak', 700, 'Steaks', 'STK-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Chicken Tarragon Steak', 800, 'Steaks', 'STK-002', '00000000', 1, datetime('now'), datetime('now'));

-- Premium Burgers
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Big Show', 600, 'Premium Burger', 'PB-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Zizo Bun', 450, 'Premium Burger', 'PB-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Spicy Hulk', 700, 'Premium Burger', 'PB-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Fire House', 600, 'Premium Burger', 'PB-004', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Burger Buzz', 650, 'Premium Burger', 'PB-005', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Grilled Thrilled', 650, 'Premium Burger', 'PB-006', '00000000', 1, datetime('now'), datetime('now'));

-- Regular Flavours Pizza - Small
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'BBQ Pizza (S)', 450, 'Pizza - Regular', 'PZ-BBQ-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Fajita Pizza (S)', 450, 'Pizza - Regular', 'PZ-FAJ-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Achari Pizza (S)', 450, 'Pizza - Regular', 'PZ-ACH-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tikka Pizza (S)', 450, 'Pizza - Regular', 'PZ-TIK-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tandoori Pizza (S)', 450, 'Pizza - Regular', 'PZ-TAN-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Cheese Lover Pizza (S)', 350, 'Pizza - Regular', 'PZ-CHL-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Veg. Lover Pizza (S)', 350, 'Pizza - Regular', 'PZ-VEG-S', '00000000', 1, datetime('now'), datetime('now'));

-- Regular Flavours Pizza - Medium
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'BBQ Pizza (M)', 830, 'Pizza - Regular', 'PZ-BBQ-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Fajita Pizza (M)', 830, 'Pizza - Regular', 'PZ-FAJ-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Achari Pizza (M)', 830, 'Pizza - Regular', 'PZ-ACH-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tikka Pizza (M)', 830, 'Pizza - Regular', 'PZ-TIK-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tandoori Pizza (M)', 830, 'Pizza - Regular', 'PZ-TAN-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Cheese Lover Pizza (M)', 650, 'Pizza - Regular', 'PZ-CHL-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Veg. Lover Pizza (M)', 600, 'Pizza - Regular', 'PZ-VEG-M', '00000000', 1, datetime('now'), datetime('now'));

-- Regular Flavours Pizza - Large
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'BBQ Pizza (L)', 1150, 'Pizza - Regular', 'PZ-BBQ-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Fajita Pizza (L)', 1150, 'Pizza - Regular', 'PZ-FAJ-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Achari Pizza (L)', 1150, 'Pizza - Regular', 'PZ-ACH-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tikka Pizza (L)', 1150, 'Pizza - Regular', 'PZ-TIK-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tandoori Pizza (L)', 1150, 'Pizza - Regular', 'PZ-TAN-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Cheese Lover Pizza (L)', 800, 'Pizza - Regular', 'PZ-CHL-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Veg. Lover Pizza (L)', 800, 'Pizza - Regular', 'PZ-VEG-L', '00000000', 1, datetime('now'), datetime('now'));

-- Classic Flavours Pizza - Small
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Malai Pizza (S)', 480, 'Pizza - Classic', 'PZ-MAL-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Sauceges Pizza (S)', 480, 'Pizza - Classic', 'PZ-SAU-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Supreme Pizza (S)', 480, 'Pizza - Classic', 'PZ-SUP-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Bone Fire Pizza (S)', 480, 'Pizza - Classic', 'PZ-BON-S', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Lava Pizza (S)', 650, 'Pizza - Classic', 'PZ-LAV-S', '00000000', 1, datetime('now'), datetime('now'));

-- Classic Flavours Pizza - Medium
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Malai Pizza (M)', 870, 'Pizza - Classic', 'PZ-MAL-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Sauceges Pizza (M)', 870, 'Pizza - Classic', 'PZ-SAU-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Supreme Pizza (M)', 870, 'Pizza - Classic', 'PZ-SUP-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Bone Fire Pizza (M)', 870, 'Pizza - Classic', 'PZ-BON-M', '00000000', 1, datetime('now'), datetime('now'));

-- Classic Flavours Pizza - Large
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Malai Pizza (L)', 1250, 'Pizza - Classic', 'PZ-MAL-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Sauceges Pizza (L)', 1250, 'Pizza - Classic', 'PZ-SAU-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Supreme Pizza (L)', 1250, 'Pizza - Classic', 'PZ-SUP-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Bone Fire Pizza (L)', 1250, 'Pizza - Classic', 'PZ-BON-L', '00000000', 1, datetime('now'), datetime('now'));

-- Specialty Pizzas - Medium
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Mughlai Kabab Pizza (M)', 1100, 'Pizza - Specialty', 'PZ-MUG-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Afghani Creamy Tikki Pizza (M)', 1100, 'Pizza - Specialty', 'PZ-AFG-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Crispy Star Pizza (M)', 1000, 'Pizza - Specialty', 'PZ-CRS-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Al-Farido Pizza (M)', 1000, 'Pizza - Specialty', 'PZ-ALF-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Extreme Pizza (M)', 1050, 'Pizza - Specialty', 'PZ-EXT-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Crown Crush Pizza (M)', 1050, 'Pizza - Specialty', 'PZ-CRW-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Kababish Pizza (M)', 950, 'Pizza - Specialty', 'PZ-KAB-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Chef Grilled Pizza (M)', 900, 'Pizza - Specialty', 'PZ-CHG-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Behari Kabab Pizza (M)', 950, 'Pizza - Specialty', 'PZ-BEH-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Peri Peri Pizza (M)', 900, 'Pizza - Specialty', 'PZ-PER-M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Lava Special Pizza (M)', 1100, 'Pizza - Specialty', 'PZ-LVS-M', '00000000', 1, datetime('now'), datetime('now'));

-- Specialty Pizzas - Large
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Mughlai Kabab Pizza (L)', 1500, 'Pizza - Specialty', 'PZ-MUG-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Afghani Creamy Tikki Pizza (L)', 1500, 'Pizza - Specialty', 'PZ-AFG-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Crispy Star Pizza (L)', 1400, 'Pizza - Specialty', 'PZ-CRS-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Al-Farido Pizza (L)', 1400, 'Pizza - Specialty', 'PZ-ALF-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Extreme Pizza (L)', 1450, 'Pizza - Specialty', 'PZ-EXT-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Chef Shahi Pizza (L)', 1950, 'Pizza - Specialty', 'PZ-CHS-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'CC Special Pizza (L)', 1250, 'Pizza - Specialty', 'PZ-CCS-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Kabab Stuffer Pizza (L)', 1350, 'Pizza - Specialty', 'PZ-KBS-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Crown Crush Pizza (L)', 1450, 'Pizza - Specialty', 'PZ-CRW-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Chef Grilled Pizza (L)', 1300, 'Pizza - Specialty', 'PZ-CHG-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Behari Kabab Pizza (L)', 1350, 'Pizza - Specialty', 'PZ-BEH-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Peri Peri Pizza (L)', 1500, 'Pizza - Specialty', 'PZ-PER-L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Lava Special Pizza (L)', 1500, 'Pizza - Specialty', 'PZ-LVS-L', '00000000', 1, datetime('now'), datetime('now'));

-- Burgers
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Zinger Bun', 330, 'Burger', 'BG-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Chef Patty Bun', 240, 'Burger', 'BG-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'KPK Bun', 250, 'Burger', 'BG-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tikka Bun', 240, 'Burger', 'BG-004', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Spicy Special', 270, 'Burger', 'BG-005', '00000000', 1, datetime('now'), datetime('now'));

-- Grilled Burgers
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Mr.Smoky', 350, 'Grilled Burger', 'GB-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Arabian Burger', 260, 'Grilled Burger', 'GB-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Double Dacker', 600, 'Grilled Burger', 'GB-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Senior Grilled', 550, 'Grilled Burger', 'GB-004', '00000000', 1, datetime('now'), datetime('now'));

-- Classic Burgers
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Beef Burger', 650, 'Classic Burger', 'CB-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Fish Burger', 650, 'Classic Burger', 'CB-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Chef Show', 500, 'Classic Burger', 'CB-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Grilled Hulk Show', 680, 'Classic Burger', 'CB-004', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Chipotlay Special', 370, 'Classic Burger', 'CB-005', '00000000', 1, datetime('now'), datetime('now'));

-- Pratha Wraps
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Tikka Wraps', 240, 'Pratha Wraps', 'PW-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Kabab Wraps', 300, 'Pratha Wraps', 'PW-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Bingo Wraps', 350, 'Pratha Wraps', 'PW-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Mexican Wraps', 300, 'Pratha Wraps', 'PW-004', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Arabian Wraps', 250, 'Pratha Wraps', 'PW-005', '00000000', 1, datetime('now'), datetime('now'));

-- Pizza Wraps
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Pizza Roll', 550, 'Pizza Wraps', 'PWR-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Pizza Wrap', 500, 'Pizza Wraps', 'PWR-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tartilaa Cheese Wrap', 550, 'Pizza Wraps', 'PWR-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Tartilaa Tandoori Wrap', 500, 'Pizza Wraps', 'PWR-004', '00000000', 1, datetime('now'), datetime('now'));

-- Mineral Water
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Mineral Water 1.5 Ltr', 100, 'Beverages', 'BEV-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Mineral Water 0.5 Ltr', 50, 'Beverages', 'BEV-002', '00000000', 1, datetime('now'), datetime('now'));

-- Deep Fried
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Nuggets 6-Pc', 280, 'Deep Fried', 'DF-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Hot Wings 8-Pc', 450, 'Deep Fried', 'DF-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Zinger Piece', 250, 'Deep Fried', 'DF-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Loaded Fries (M)', 400, 'Deep Fried', 'DF-004M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Loaded Fries (L)', 550, 'Deep Fried', 'DF-004L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Family Fries', 200, 'Deep Fried', 'DF-005', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Pizza Fries', 600, 'Deep Fried', 'DF-006', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Large Plane Fries', 300, 'Deep Fried', 'DF-007', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'B.B.Q Wing', 500, 'Deep Fried', 'DF-008', '00000000', 1, datetime('now'), datetime('now'));

-- Fish and Chips
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Fish and Chips', 1150, 'Fish and Chips', 'FC-001', '00000000', 1, datetime('now'), datetime('now'));

-- Pizza Pratha
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Tandori Pizza Paratha', 450, 'Pizza Pratha', 'PP-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Triple Cheese Paratha', 500, 'Pizza Pratha', 'PP-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Chef Cooks Special', 550, 'Pizza Pratha', 'PP-003', '00000000', 1, datetime('now'), datetime('now'));

-- Pasta
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Flaming Pasta (M)', 450, 'Pasta', 'PA-001M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Flaming Pasta (L)', 600, 'Pasta', 'PA-001L', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Al-Frido Pasta', 700, 'Pasta', 'PA-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Crunchy Pasta (M)', 450, 'Pasta', 'PA-003M', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Crunchy Pasta (L)', 600, 'Pasta', 'PA-003L', '00000000', 1, datetime('now'), datetime('now'));

-- Add Ons
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Dip Sauce', 50, 'Add On', 'AO-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Ex Cheese Topping', 150, 'Add On', 'AO-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Ex Chicken Topping', 100, 'Add On', 'AO-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Cheese Slice', 70, 'Add On', 'AO-004', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Dip Sauce Special', 70, 'Add On', 'AO-005', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Dip Sauce Garlic Mayo', 50, 'Add On', 'AO-006', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Dip Sauce Bar B.Q', 50, 'Add On', 'AO-007', '00000000', 1, datetime('now'), datetime('now'));

-- Drinks
INSERT INTO items (id, name, price, category, item_code, pct_code, is_active, created_at, updated_at) VALUES
(randomblob(16), 'Reg. Drinks', 70, 'Drinks', 'DR-001', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Reg. Sting', 80, 'Drinks', 'DR-002', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), '1 Ltr Drink', 180, 'Drinks', 'DR-003', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), '1.5 Ltr Drink', 220, 'Drinks', 'DR-004', '00000000', 1, datetime('now'), datetime('now')),
(randomblob(16), 'Lemon Soda', 150, 'Drinks', 'DR-005', '00000000', 1, datetime('now'), datetime('now'));


