ALTER TABLE IF EXISTS cinema_service.room_seats
    ADD COLUMN is_booked BOOLEAN NOT NULL DEFAULT FALSE;