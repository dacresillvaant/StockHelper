-- Flyway migration: add pays_dividend boolean column to owned_stock

ALTER TABLE public.owned_stock
    ADD COLUMN IF NOT EXISTS pays_dividend boolean;