INSERT INTO training_types (training_type_name)
VALUES
    ('Strength'),
    ('Cardio'),
    ('Functional Training'),
    ('STRENGTH'),
    ('CARDIO'),
    ('YOGA'),
    ('PILATES'),
    ('CROSSFIT')
ON CONFLICT (training_type_name) DO NOTHING;
