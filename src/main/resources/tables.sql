/* =========================================================
   FARMSENSE AI – DATABASE SCHEMA
   ========================================================= */

/* -------------------------
   EXTENSIONS (Postgres)
-------------------------- */
-- Uncomment if using PostgreSQL
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

/* -------------------------
   USERS
-------------------------- */
CREATE TABLE users (
                       user_id UUID PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       password_hash TEXT NOT NULL,
                       role VARCHAR(30) DEFAULT 'FARMER',
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   FARMS
-------------------------- */
CREATE TABLE farms (
                       farm_id UUID PRIMARY KEY,
                       user_id UUID REFERENCES users(user_id),
                       farm_name VARCHAR(150),
                       location VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   BATCHES
-------------------------- */
CREATE TABLE batches (
                         batch_id UUID PRIMARY KEY,
                         farm_id UUID REFERENCES farms(farm_id),
                         batch_code VARCHAR(50) UNIQUE NOT NULL,
                         bird_type VARCHAR(50),
                         start_date DATE NOT NULL,
                         initial_birds INT NOT NULL,
                         current_birds INT NOT NULL,
                         status VARCHAR(20) DEFAULT 'ACTIVE',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   DAILY ENTRIES
-------------------------- */
CREATE TABLE daily_entries (
                               entry_id UUID PRIMARY KEY,
                               batch_id UUID REFERENCES batches(batch_id),
                               day_no INT NOT NULL,
                               avg_weight_g DECIMAL(6,2),
                               feed_per_bird_g DECIMAL(6,2),
                               water_per_bird_ml DECIMAL(6,2),
                               mortality_count INT DEFAULT 0,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   FEED PLAN MASTER (SEEDED)
-------------------------- */
CREATE TABLE feed_plan_master (
                                  day_no INT PRIMARY KEY,
                                  recommended_feed_g DECIMAL(6,2),
                                  recommended_water_ml DECIMAL(6,2)
);

/* -------------------------
   HEALTH PLAN MASTER
-------------------------- */
CREATE TABLE health_plan_master (
                                    day_no INT PRIMARY KEY,
                                    support_type VARCHAR(50),
                                    description TEXT
);

/* -------------------------
   HEALTH ALERTS
-------------------------- */
CREATE TABLE health_alerts (
                               alert_id UUID PRIMARY KEY,
                               batch_id UUID REFERENCES batches(batch_id),
                               risk_level VARCHAR(10),
                               message TEXT,
                               confidence DECIMAL(4,2),
                               source VARCHAR(50),
                               is_reviewed BOOLEAN DEFAULT FALSE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   COST ENTRIES
-------------------------- */
CREATE TABLE cost_entries (
                              cost_id UUID PRIMARY KEY,
                              batch_id UUID REFERENCES batches(batch_id),
                              cost_type VARCHAR(50),
                              amount DECIMAL(12,2),
                              entry_date DATE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   PROFIT SNAPSHOT
-------------------------- */
CREATE TABLE profit_snapshots (
                                  snapshot_id UUID PRIMARY KEY,
                                  batch_id UUID REFERENCES batches(batch_id),
                                  total_cost DECIMAL(12,2),
                                  expected_revenue DECIMAL(12,2),
                                  expected_profit DECIMAL(12,2),
                                  cost_per_bird DECIMAL(8,2),
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   PROFIT TREND
-------------------------- */
CREATE TABLE profit_trend (
                              id UUID PRIMARY KEY,
                              batch_id UUID REFERENCES batches(batch_id),
                              day_no INT,
                              profit DECIMAL(12,2),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   FEED EFFICIENCY TREND
-------------------------- */
CREATE TABLE efficiency_trend (
                                  id UUID PRIMARY KEY,
                                  batch_id UUID REFERENCES batches(batch_id),
                                  day_no INT,
                                  feed_efficiency DECIMAL(4,2),
                                  profit DECIMAL(12,2),
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   SETTINGS
-------------------------- */
CREATE TABLE settings (
                          setting_id UUID PRIMARY KEY,
                          user_id UUID REFERENCES users(user_id),
                          notification_health BOOLEAN DEFAULT TRUE,
                          notification_profit BOOLEAN DEFAULT TRUE,
                          language VARCHAR(10) DEFAULT 'en',
                          currency VARCHAR(10) DEFAULT 'INR',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* -------------------------
   INDEXES
-------------------------- */
CREATE INDEX idx_batches_farm ON batches(farm_id);
CREATE INDEX idx_daily_entries_batch ON daily_entries(batch_id);
CREATE INDEX idx_alerts_batch ON health_alerts(batch_id);
CREATE INDEX idx_cost_batch ON cost_entries(batch_id);
CREATE INDEX idx_profit_batch ON profit_trend(batch_id);
CREATE INDEX idx_efficiency_batch ON efficiency_trend(batch_id);

/* =========================================================
   END OF SCHEMA
   ========================================================= */
/* ------------------------- USERS -------------------------- */
ALTER TABLE users
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- FARMS -------------------------- */
ALTER TABLE farms
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- BATCHES -------------------------- */
ALTER TABLE batches
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- DAILY ENTRIES -------------------------- */
ALTER TABLE daily_entries
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- FEED PLAN MASTER -------------------------- */
ALTER TABLE feed_plan_master
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- HEALTH PLAN MASTER -------------------------- */
ALTER TABLE health_plan_master
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- HEALTH ALERTS -------------------------- */
ALTER TABLE health_alerts
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- COST ENTRIES -------------------------- */
ALTER TABLE cost_entries
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- PROFIT SNAPSHOTS -------------------------- */
ALTER TABLE profit_snapshots
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- PROFIT TREND -------------------------- */
ALTER TABLE profit_trend
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- FEED EFFICIENCY TREND -------------------------- */
ALTER TABLE efficiency_trend
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

/* ------------------------- SETTINGS -------------------------- */
ALTER TABLE settings
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
