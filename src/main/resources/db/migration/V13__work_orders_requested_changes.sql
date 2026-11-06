ALTER TABLE work_orders DROP COLUMN IF EXISTS diagnosis_revision;
ALTER TABLE work_orders ADD COLUMN IF NOT EXISTS requested_changes TEXT;
