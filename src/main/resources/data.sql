INSERT INTO patient_table (medical_record, dni, name, address, birthdate, telephone, email) VALUES
                        (215, 429875421, 'María López', 'Av. Rivadavia 2020', '1995-05-23', 1145678920, 'maria.lopez@gmail.com'),
                        (318, 412345678, 'Carlos Fernández', 'Mitre 789', '1988-09-14', 1167890123, 'carlos.fernandez@gmail.com'),
                        (507, 437654321, 'Ana Martínez', 'San Martín 456', '1992-12-30', 1156789012, 'ana.martinez@gmail.com'),
                        (623, 405678912, 'Sofía Ramírez', 'Corrientes 333', '1999-07-18', 1176543289, 'sofia.ramirez@gmail.com'),
                        (731, 428901234, 'Diego Torres', 'Belgrano 220', '1985-03-05', 1134567890, 'diego.torres@gmail.com');

INSERT INTO tooth_table (patient_id, "number", "center", "down", "left", "right", "up")
                VALUES (215, 1, 0, 0, 2, 0, 0),
                         (215,2,2,0,2, 1, 0),
                         (215,23,1,0,2, 1, 0);
