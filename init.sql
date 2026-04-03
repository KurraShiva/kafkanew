-- ─────────────────────────────────────────────────────────
-- MySQL Init Script — runs once on first container start
-- ─────────────────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS kafka_streaming_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE kafka_streaming_db;

-- Grant permissions to kafkauser
GRANT ALL PRIVILEGES ON kafka_streaming_db.* TO 'kafkauser'@'%';
FLUSH PRIVILEGES;

-- The 'bookings' table is auto-created by Hibernate (ddl-auto: update)
-- This seed data is optional — useful for testing the dashboard on first run.

-- Seed data (will only insert if table exists after Hibernate creates it)
-- Hibernate runs AFTER this script, so seed via a Spring CommandLineRunner instead.
