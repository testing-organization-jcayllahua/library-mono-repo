IF DB_ID('example_db') IS NULL
BEGIN
    CREATE DATABASE [example_db];
END;

-- Esperar hasta que la base de datos esté disponible
DECLARE @counter INT = 0;
WHILE DB_ID('example_db') IS NULL AND @counter < 30
BEGIN
    WAITFOR DELAY '00:00:02';  -- Espera de 2 segundos
    SET @counter = @counter + 1;
END;

-- Ahora intenta usar la base de datos
USE [example_db];

IF OBJECT_ID('customer', 'U') IS NULL
BEGIN
CREATE TABLE customer (
                          id UNIQUEIDENTIFIER PRIMARY KEY,
                          first_name NVARCHAR(50),
                          last_name NVARCHAR(50),
                          email NVARCHAR(255),
                          phone_number NVARCHAR(20),
                          created_at DATETIME2,
                          updated_at DATETIME2
);
END;
