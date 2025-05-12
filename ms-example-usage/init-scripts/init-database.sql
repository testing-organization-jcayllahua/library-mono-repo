IF DB_ID('example_db') IS NULL
BEGIN
    CREATE DATABASE [example_db];
END;

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
