INSERT INTO training_types (training_type_name)
VALUES
    ('Strength'),
    ('Cardio'),
    ('Functional Training')
ON CONFLICT (training_type_name) DO NOTHING;