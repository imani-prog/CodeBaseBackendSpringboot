-- Add facilities column to hospitals table
-- Migration: V3__add_facilities_to_hospital.sql
-- This adds a text field to store comma-separated list of hospital facilities

ALTER TABLE hospitals ADD COLUMN facilities TEXT;

-- Add column comment for documentation
COMMENT ON COLUMN hospitals.facilities IS 'Comma-separated list of available facilities (e.g., LABORATORY, PHARMACY, RADIOLOGY, ICU, EMERGENCY, MATERNITY, etc.)';

