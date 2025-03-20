DO $$
BEGIN
    -- Проверяем, существует ли пользователь "mirmad"
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'mirmad') THEN
        CREATE USER mirmad WITH PASSWORD 'password';
    END IF;

    -- Проверяем, существует ли база данных "vkrdb"
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'vkrdb') THEN
        CREATE DATABASE vkrdb OWNER mirmad;
    END IF;

    -- Проверяем, существует ли таблица users
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
        CREATE TABLE users (
            id SERIAL PRIMARY KEY,
            username VARCHAR(50) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(100) UNIQUE NOT NULL
        );
    END IF;
END $$;