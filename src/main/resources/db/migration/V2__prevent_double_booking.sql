ALTER TABLE appointment
    ADD COLUMN active_slot DATETIME(6)
        GENERATED ALWAYS AS (IF(status IN ('PENDING', 'CONFIRMED'), date_time, NULL)) STORED,
    ADD CONSTRAINT uk_appointment_professional_active_slot UNIQUE (professional_id, active_slot);