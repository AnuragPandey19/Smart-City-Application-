-- =====================================================================
-- Smart City — full database bootstrap.
-- Derived from every SQL query referenced in the Java source.
--
-- HOW TO USE
--   1. Open MySQL Workbench (or `mysql -u root -p` in a terminal).
--   2. Run this entire file as a script.
--   3. Done. Sign up via the app to create your user.
--
-- This script is safe to re-run: every table is dropped + recreated,
-- so re-running it resets the data to the seed values below.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS smartcity
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE smartcity;

-- ---------------------------------------------------------------------
-- 1. users
--    Auth + profile. Password column holds PBKDF2-SHA256 records
--    of the form  pbkdf2_sha256$<iters>$<saltB64>$<hashB64>  (~120 chars)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(120)  NOT NULL,
    username  VARCHAR(60)   NOT NULL UNIQUE,
    password  VARCHAR(255)  NOT NULL,           -- fits PBKDF2 record
    dob       DATE          NOT NULL,
    country   VARCHAR(80)   NOT NULL,
    state     VARCHAR(80)   NOT NULL,
    pincode   VARCHAR(10)   NOT NULL,           -- string to preserve leading zeros
    mobile    VARCHAR(15)   NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- No seed users — create your account via the in-app Sign Up screen
-- so the password is properly hashed by PasswordUtil.

-- ---------------------------------------------------------------------
-- 2. city_information
--    Looked up by exact name match in CityInfoPanel.
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS city_information;
CREATE TABLE city_information (
    name                VARCHAR(80)   PRIMARY KEY,
    state               VARCHAR(80)   NOT NULL,
    country             VARCHAR(80)   NOT NULL,
    population          INT           NOT NULL,
    area_sqkm           DECIMAL(10,2) NOT NULL,
    languages           VARCHAR(200)  NOT NULL,
    famous_for          VARCHAR(255)  NOT NULL,
    best_time_to_visit  VARCHAR(80)   NOT NULL,
    description         TEXT          NOT NULL,
    emergency_contacts  VARCHAR(255)  NOT NULL
);

INSERT INTO city_information
    (name, state, country, population, area_sqkm, languages, famous_for, best_time_to_visit, description, emergency_contacts)
VALUES
('Mumbai', 'Maharashtra', 'India', 20411000, 603.40,
 'Marathi, Hindi, English',
 'Gateway of India, Marine Drive, Bollywood film industry, vada pav',
 'November to February',
 'India''s financial capital on the west coast. Home to the Bombay Stock Exchange, Bollywood, and an iconic skyline along the Arabian Sea. Known for its fast-paced street-food culture and historical colonial architecture.',
 'Police: 100  |  Ambulance: 102  |  Fire: 101  |  Women Helpline: 1091'),

('Bengaluru', 'Karnataka', 'India', 13193000, 741.00,
 'Kannada, English, Hindi, Tamil',
 'IT industry, breweries, Lalbagh Garden, Bangalore Palace',
 'October to February',
 'Capital of Karnataka and India''s technology hub. Pleasant climate year-round, vibrant cafe and pub scene, and headquarters of countless software companies and startups. Often called the "Silicon Valley of India".',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Women Helpline: 1091'),

('Delhi', 'Delhi', 'India', 32941000, 1484.00,
 'Hindi, Punjabi, Urdu, English',
 'Red Fort, India Gate, Qutub Minar, Lotus Temple, street food',
 'October to March',
 'Capital of India and a city of striking contrasts. Old Delhi houses Mughal-era monuments and narrow lanes of Chandni Chowk, while New Delhi features wide colonial boulevards, embassies, and modern infrastructure.',
 'Police: 112  |  Ambulance: 102  |  Fire: 101  |  Women Helpline: 1091'),

('Chennai', 'Tamil Nadu', 'India', 11503000, 426.00,
 'Tamil, English',
 'Marina Beach, Kapaleeshwarar Temple, Carnatic music, filter coffee',
 'November to February',
 'Capital of Tamil Nadu on the Bay of Bengal. Known for Marina Beach (one of the longest in the world), classical Carnatic music, ancient Dravidian temples, and authentic South Indian cuisine.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Women Helpline: 1091'),

('Jaipur', 'Rajasthan', 'India', 4067000, 467.00,
 'Hindi, Rajasthani, English',
 'Amber Fort, Hawa Mahal, City Palace, Jantar Mantar, block-printed textiles',
 'October to March',
 'The "Pink City" and capital of Rajasthan, named for the distinctive terracotta-pink colour of its old town buildings. Part of India''s Golden Triangle tourist circuit, famous for forts, palaces, and a rich royal heritage.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Tourist Helpline: 1363'),

('Kolkata', 'West Bengal', 'India', 14850000, 206.10,
 'Bengali, Hindi, English',
 'Victoria Memorial, Howrah Bridge, literature, sweets, festivals',
 'October to March',
 'Cultural capital of India and former capital of British India. Famous for its intellectual heritage, vibrant Durga Puja celebrations, traditional yellow Ambassador taxis, and the legendary sweets of College Street.',
 'Police: 100  |  Ambulance: 102  |  Fire: 101  |  Women Helpline: 1091'),

('Hyderabad', 'Telangana', 'India', 10269000, 650.00,
 'Telugu, Urdu, Hindi, English',
 'Charminar, Golconda Fort, biryani, IT corridor, pearls',
 'October to February',
 'Joint capital of Telangana and Andhra Pradesh, famous for its rich Nizami heritage and world-renowned Hyderabadi biryani. A leading IT and biotech hub with the buzzing HITEC City alongside historical Mughal-era monuments.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Women Helpline: 1091'),

('Pune', 'Maharashtra', 'India', 7400000, 331.26,
 'Marathi, Hindi, English',
 'Shaniwar Wada, Aga Khan Palace, education hubs, IT industry',
 'October to February',
 'Cultural capital of Maharashtra known as the "Oxford of the East" for its prestigious educational institutions. A growing IT and automotive hub with a pleasant climate, rich Maratha history, and a thriving cafe culture.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Women Helpline: 1091'),

('Goa', 'Goa', 'India', 1458000, 3702.00,
 'Konkani, Marathi, English, Portuguese',
 'Beaches, Portuguese churches, nightlife, seafood, sunset cruises',
 'November to February',
 'India''s smallest state by area, famous for golden beaches, Portuguese colonial architecture, and a laid-back coastal lifestyle. A popular destination for nightlife, water sports, and Catholic heritage including the Basilica of Bom Jesus.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Tourist Police: 100'),

('Kochi', 'Kerala', 'India', 2120000, 94.88,
 'Malayalam, English, Hindi, Tamil',
 'Chinese fishing nets, backwaters, spice markets, Kathakali',
 'September to March',
 'A vibrant port city in Kerala known for its scenic backwaters, Chinese fishing nets at Fort Kochi, and a unique blend of Dutch, Portuguese, and British colonial influences. Gateway to Kerala''s lush spice plantations and houseboat cruises.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Coastal Police: 1093'),

('Ahmedabad', 'Gujarat', 'India', 8253000, 464.00,
 'Gujarati, Hindi, English',
 'Sabarmati Ashram, textiles, stepwells, Navratri festival',
 'November to February',
 'India''s first UNESCO World Heritage City. Famous for Mahatma Gandhi''s Sabarmati Ashram, intricate carved stepwells, a thriving textile industry, and grand nine-night Navratri celebrations.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Women Helpline: 1091'),

('Varanasi', 'Uttar Pradesh', 'India', 1435000, 82.10,
 'Hindi, Bhojpuri, Sanskrit, English',
 'Ganga Aarti, ghats, ancient temples, Banarasi sarees',
 'November to February',
 'One of the world''s oldest continuously inhabited cities and the spiritual capital of India. Famous for its sacred ghats on the Ganges, evening Ganga Aarti, ancient temples, and silk weaving traditions dating back centuries.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Tourist Helpline: 1363'),

('Udaipur', 'Rajasthan', 'India', 451000, 64.00,
 'Hindi, Rajasthani, English',
 'Lake Pichola, City Palace, Mewar heritage, miniature paintings',
 'September to March',
 'The "City of Lakes" and former capital of the Mewar kingdom. Romantic palaces float on shimmering man-made lakes against the backdrop of the Aravalli Hills. A favourite for destination weddings and royal heritage tours.',
 'Police: 100  |  Ambulance: 108  |  Fire: 101  |  Tourist Helpline: 1363'),

('Shimla', 'Himachal Pradesh', 'India', 169578, 35.34,
 'Hindi, English, Pahari',
 'Toy train, Mall Road, colonial architecture, mountain views',
 'March to June, September to November',
 'Former summer capital of British India, perched in the Himalayan foothills. Famous for the UNESCO-listed Kalka-Shimla toy train, Tudor-Gothic colonial buildings along Mall Road, and panoramic snow-capped mountain views.',
 'Police: 100  |  Ambulance: 102  |  Fire: 101  |  Disaster Helpline: 1077'),

('Darjeeling', 'West Bengal', 'India', 132016, 10.57,
 'Nepali, Hindi, Bengali, English',
 'Tea estates, Himalayan railway, Mount Kanchenjunga views',
 'March to May, September to November',
 'A hill station famous worldwide for its eponymous tea, the UNESCO-listed Darjeeling Himalayan Railway, and breathtaking sunrise views of Mount Kanchenjunga from Tiger Hill.',
 'Police: 100  |  Ambulance: 102  |  Fire: 101  |  Disaster Helpline: 1077'),

('Manali', 'Himachal Pradesh', 'India', 8096, 17.50,
 'Hindi, English, Pahari',
 'Snow sports, Solang Valley, Rohtang Pass, adventure tourism',
 'October to February (snow), March to June (general)',
 'A high-altitude Himalayan resort town on the banks of the Beas River. A year-round magnet for honeymooners, snow-sport enthusiasts, paragliders, and trekkers heading toward Leh-Ladakh.',
 'Police: 100  |  Ambulance: 102  |  Fire: 101  |  Disaster Helpline: 1077'),

('Springfield', 'Illinois', 'USA', 165000, 158.10,
 'English',
 'Abraham Lincoln historic sites, Old State Capitol, horseshoe sandwich',
 'April to October',
 'A mid-sized inland city and capital of Illinois, best known as the long-time home of Abraham Lincoln. Compact, walkable, with quiet residential neighborhoods, parks, and a family-friendly atmosphere.',
 'Police: 911  |  Ambulance: 911  |  Fire: 911  |  Non-Emergency: 311'),

('TechVille', 'Karnataka', 'India', 220000, 78.50,
 'English, Kannada, Hindi',
 'Innovation parks, startup incubators, coworking campuses',
 'Year-round',
 'A planned technology township built around innovation parks, coworking campuses, and startup incubators. Modern infrastructure, fibre internet everywhere, and a young demographic make it a magnet for software professionals.',
 'Security: 100  |  Medical: 102  |  Fire: 101  |  Campus Helpdesk: 1800-200-7000'),

('Singapore', 'Central', 'Singapore', 5900000, 728.60,
 'English, Mandarin, Malay, Tamil',
 'Marina Bay Sands, Gardens by the Bay, hawker food, Sentosa',
 'February to April',
 'A city-state and global financial hub at the tip of the Malay Peninsula. Famed for futuristic architecture, immaculate cleanliness, multicultural cuisine, and being one of the safest, most efficient cities in the world.',
 'Police: 999  |  Ambulance: 995  |  Non-Emergency: 1777'),

('Dubai', 'Dubai', 'UAE', 3490000, 4114.00,
 'Arabic, English, Hindi, Urdu',
 'Burj Khalifa, Palm Jumeirah, desert safaris, gold souks, shopping',
 'November to March',
 'The largest city in the United Arab Emirates and a global icon of modern luxury. Home to the world''s tallest building (Burj Khalifa), man-made islands, vast shopping malls, and a desert-meets-skyscraper aesthetic.',
 'Police: 999  |  Ambulance: 998  |  Fire: 997  |  Tourist Police: 901');

-- ---------------------------------------------------------------------
-- 3. hotels
--    Searched by location with LIKE %x%.
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS hotels;
CREATE TABLE hotels (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    hotel_name  VARCHAR(120)  NOT NULL,
    location    VARCHAR(120)  NOT NULL,
    contact     VARCHAR(40)   NOT NULL,
    price_range VARCHAR(40)   NOT NULL,
    rating      DECIMAL(2,1)  NOT NULL,
    INDEX idx_hotels_location (location)
);

INSERT INTO hotels (hotel_name, location, contact, price_range, rating) VALUES
('The Taj Palace',         'Mumbai',    '+91-22-6665-3366', '₹8,000 - ₹25,000', 4.8),
('Sea View Inn',           'Mumbai',    '+91-22-3344-1122', '₹2,500 - ₹5,500',  4.1),
('The Leela Palace',       'Bengaluru', '+91-80-2521-1234', '₹10,000 - ₹30,000',4.9),
('Urban Stay Suites',      'Bengaluru', '+91-80-4040-8080', '₹2,000 - ₹4,500',  4.0),
('Imperial Heritage',      'Delhi',     '+91-11-2334-5678', '₹6,500 - ₹18,000', 4.6),
('Connaught Boutique',     'Delhi',     '+91-11-4242-1010', '₹3,000 - ₹6,000',  4.2),
('Marina Grand',           'Chennai',   '+91-44-2811-0011', '₹4,500 - ₹12,000', 4.3),
('Heritage Pink Palace',   'Jaipur',    '+91-141-256-9090', '₹7,000 - ₹20,000', 4.7),
('Springfield Lodge',      'Springfield','+1-555-0145',     '$70 - $130',       3.9),
('TechVille Innovation Hotel','TechVille','+91-80-7000-7000','₹3,500 - ₹7,000', 4.4);

-- ---------------------------------------------------------------------
-- 4. tourism  (places to visit)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS tourism;
CREATE TABLE tourism (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    place_name    VARCHAR(120)  NOT NULL,
    location      VARCHAR(120)  NOT NULL,
    description   VARCHAR(500)  NOT NULL,
    entry_fee     DECIMAL(8,2)  NOT NULL,
    opening_hours VARCHAR(80)   NOT NULL,
    INDEX idx_tourism_location (location)
);

INSERT INTO tourism (place_name, location, description, entry_fee, opening_hours) VALUES
('Gateway of India',  'Mumbai',     'Iconic 26-metre arch monument overlooking the Arabian Sea, built in 1924.',                0.00,  '24 hours'),
('Marine Drive',      'Mumbai',     'A 3.6-km-long arc-shaped boulevard along the south Mumbai coastline, nicknamed the Queen''s Necklace.', 0.00, '24 hours'),
('Lalbagh Garden',    'Bengaluru',  '240-acre botanical garden featuring rare plants, a glass house, and a 3,000-million-year-old rock.', 50.00, '6:00 AM - 7:00 PM'),
('Bangalore Palace',  'Bengaluru',  'Tudor-style royal palace inspired by Windsor Castle, completed in 1878.',                 230.00, '10:00 AM - 5:30 PM'),
('Red Fort',          'Delhi',      'UNESCO World Heritage Mughal-era fortress, residence of emperors for nearly 200 years.',   35.00, '9:30 AM - 4:30 PM'),
('Qutub Minar',       'Delhi',      '73-metre tall victory tower from 1193, the tallest brick minaret in the world.',           40.00, '7:00 AM - 5:00 PM'),
('Marina Beach',      'Chennai',    'One of the longest urban beaches in the world, stretching for 13 km along the Bay of Bengal.', 0.00, '24 hours'),
('Amber Fort',        'Jaipur',     'Hilltop fort known for its artistic Hindu-style elements and stunning mirror palace.',     200.00, '8:00 AM - 5:30 PM'),
('Springfield Park',  'Springfield','A large public park with hiking trails, a lake, and weekend farmers'' markets.',           0.00,  '6:00 AM - 9:00 PM'),
('Innovation Plaza',  'TechVille',  'Open-air tech plaza with interactive AR installations and a startup history museum.',     100.00, '10:00 AM - 8:00 PM');

-- ---------------------------------------------------------------------
-- 5. restaurants
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS restaurants;
CREATE TABLE restaurants (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_name VARCHAR(120) NOT NULL,
    location        VARCHAR(120) NOT NULL,
    cuisine         VARCHAR(80)  NOT NULL,
    price_range     VARCHAR(40)  NOT NULL,
    contact         VARCHAR(40)  NOT NULL,
    INDEX idx_restaurants_location (location)
);

INSERT INTO restaurants (restaurant_name, location, cuisine, price_range, contact) VALUES
('Trishna',                     'Mumbai',     'Coastal Indian',     '₹1,500 - ₹3,000 for two', '+91-22-2270-3214'),
('Britannia & Co.',             'Mumbai',     'Parsi',              '₹800 - ₹1,500 for two',   '+91-22-2261-5264'),
('MTR 1924',                    'Bengaluru',  'South Indian',       '₹400 - ₹800 for two',     '+91-80-2222-0796'),
('Toit Brewpub',                'Bengaluru',  'European / Pub',     '₹1,800 - ₹3,500 for two', '+91-80-4115-3793'),
('Karim''s',                    'Delhi',      'Mughlai',            '₹600 - ₹1,200 for two',   '+91-11-2326-9880'),
('Indian Accent',               'Delhi',      'Modern Indian',      '₹6,000 - ₹10,000 for two','+91-11-6617-7088'),
('Dakshin',                     'Chennai',    'South Indian',       '₹2,000 - ₹3,500 for two', '+91-44-2499-4101'),
('Suvarna Mahal',               'Jaipur',     'Royal Rajasthani',   '₹5,000 - ₹9,000 for two', '+91-141-238-5700'),
('Springfield Diner',           'Springfield','American Comfort',   '$20 - $40 for two',       '+1-555-0177'),
('Byte & Brew',                 'TechVille',  'Fusion / Cafe',      '₹700 - ₹1,400 for two',   '+91-80-7100-7100');

-- ---------------------------------------------------------------------
-- 6. transportation
--    Looked up by exact (pickup, destination) match. Pickup + destination
--    values MUST match the dropdowns in TransportationPanel.java exactly.
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS transportation;
CREATE TABLE transportation (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    pickup_point    VARCHAR(80)  NOT NULL,
    destination     VARCHAR(80)  NOT NULL,
    transport_type  VARCHAR(40)  NOT NULL,
    fare            DECIMAL(8,2) NOT NULL,
    booking_method  VARCHAR(80)  NOT NULL,
    INDEX idx_transport_route (pickup_point, destination)
);

INSERT INTO transportation (pickup_point, destination, transport_type, fare, booking_method) VALUES
-- Airport routes
('Airport',         'Downtown',         'Airport Taxi',     25.00, 'Uber / Ola'),
('Airport',         'Downtown',         'Airport Shuttle',  10.00, 'Counter at Terminal 2'),
('Airport',         'City Center',      'Airport Express',   8.00, 'Metro card'),
('Airport',         'TechVille',        'App Cab',          30.00, 'Uber / Ola'),
('Airport',         'Coastal Town',     'Pre-paid Taxi',    45.00, 'Airport pre-paid counter'),

-- Central Station routes
('Central Station', 'Downtown',         'City Bus',          2.00, 'Tap-to-pay / Conductor'),
('Central Station', 'City Center',      'Metro',             3.50, 'Metro card / QR ticket'),
('Central Station', 'TechVille',        'Shared Cab',       12.00, 'Uber Pool / Ola Share'),
('Central Station', 'Old Town',         'Heritage Tram',     5.00, 'On-board ticket'),

-- Metro Station routes
('Metro Station',   'Downtown',         'Metro',             2.50, 'Metro card / QR ticket'),
('Metro Station',   'TechVille',        'Metro',             4.00, 'Metro card / QR ticket'),
('Metro Station',   'City Center',      'Metro',             2.00, 'Metro card / QR ticket'),

-- Railway Station routes
('Railway Station', 'Downtown',         'Auto Rickshaw',     6.00, 'On-meter'),
('Railway Station', 'City Center',      'City Bus',          1.50, 'Tap-to-pay / Conductor'),
('Railway Station', 'Springfield',      'Intercity Bus',    18.00, 'RedBus / Counter'),

-- Uptown routes
('Uptown',          'Downtown',         'Taxi',              8.00, 'Uber / Ola'),
('Uptown',          'City Center',      'Bike Taxi',         4.50, 'Rapido'),

-- Harbor routes
('Harbor',          'Coastal Town',     'Ferry',             5.00, 'Ticket window at dock'),
('Harbor',          'Downtown',         'Water Taxi',       15.00, 'On-app booking'),

-- Bike Station / Scooter Stand routes
('Bike Station',    'City Center',      'Rental Bicycle',    1.50, 'YULU / smart-bike app'),
('Bike Station',    'TechVille',        'Rental E-Bike',     3.00, 'YULU / smart-bike app'),
('Scooter Stand',   'Downtown',         'Rental E-Scooter',  2.50, 'Bounce / Vogo app'),
('Scooter Stand',   'TechVille',        'Rental E-Scooter',  3.50, 'Bounce / Vogo app'),

-- Hotel routes
('Hotel',           'Airport',          'Airport Taxi',     25.00, 'Hotel concierge'),
('Hotel',           'Downtown',         'Hotel Shuttle',     0.00, 'Hotel concierge'),
('Hotel',           'City Center',      'App Cab',           7.00, 'Uber / Ola'),

-- Downtown routes
('Downtown',        'Airport',          'Airport Taxi',     22.00, 'Uber / Ola'),
('Downtown',        'TechVille',        'Metro',             4.00, 'Metro card / QR ticket'),
('Downtown',        'Springfield',      'Intercity Bus',    16.00, 'RedBus / Counter'),
('Downtown',        'Any Destination',  'App Cab',          12.00, 'Uber / Ola');

-- ---------------------------------------------------------------------
-- 7. shopping_mall
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS shopping_mall;
CREATE TABLE shopping_mall (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    mall_name     VARCHAR(120) NOT NULL,
    location      VARCHAR(120) NOT NULL,
    opening_time  VARCHAR(20)  NOT NULL,
    closing_time  VARCHAR(20)  NOT NULL,
    INDEX idx_mall_location (location)
);

INSERT INTO shopping_mall (mall_name, location, opening_time, closing_time) VALUES
('High Street Phoenix',     'Mumbai',     '10:00 AM', '10:00 PM'),
('Inorbit Mall',            'Mumbai',     '10:30 AM', '10:30 PM'),
('Phoenix Marketcity',      'Bengaluru',  '10:00 AM', '10:00 PM'),
('Orion Mall',              'Bengaluru',  '11:00 AM', '10:00 PM'),
('Select Citywalk',         'Delhi',      '10:00 AM', '11:00 PM'),
('DLF Mall of India',       'Delhi',      '10:00 AM', '10:00 PM'),
('Express Avenue',          'Chennai',    '10:00 AM', '10:00 PM'),
('World Trade Park',        'Jaipur',     '10:30 AM', '10:30 PM'),
('Springfield Town Center', 'Springfield','09:00 AM', '09:00 PM'),
('TechVille Galleria',      'TechVille',  '10:00 AM', '11:00 PM');

-- ---------------------------------------------------------------------
-- Quick sanity check — should print row counts for every table.
-- ---------------------------------------------------------------------
SELECT 'users'             AS table_name, COUNT(*) AS row_count FROM users
UNION ALL SELECT 'city_information', COUNT(*) FROM city_information
UNION ALL SELECT 'hotels',           COUNT(*) FROM hotels
UNION ALL SELECT 'tourism',          COUNT(*) FROM tourism
UNION ALL SELECT 'restaurants',      COUNT(*) FROM restaurants
UNION ALL SELECT 'transportation',   COUNT(*) FROM transportation
UNION ALL SELECT 'shopping_mall',    COUNT(*) FROM shopping_mall;
