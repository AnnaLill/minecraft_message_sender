-- SQL Script for Minecraft Message Sender Database
-- Run this script to create the database and table

-- Create database (run this as PostgreSQL superuser)
CREATE DATABASE minecraft_messages;

-- Connect to the database
\c minecraft_messages;

-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    text VARCHAR(256) NOT NULL
);

-- Create index on uuid for faster lookups
CREATE INDEX IF NOT EXISTS idx_messages_uuid ON messages(uuid);

-- Sample query to view messages
SELECT * FROM messages ORDER BY id DESC LIMIT 10;

