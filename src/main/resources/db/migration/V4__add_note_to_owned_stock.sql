-- Flyway migration: add note boolean column to owned_stock

ALTER TABLE public.owned_stock
    ADD COLUMN IF NOT EXISTS note varchar;