INSERT INTO dentist_table (id, username, name, password, email,role) VALUES
                        (1, 'User1', 'User1_name', '$2a$10$Cgy3bxDG3Anp5pmEzVX.CuplTtqri/BgCy2UIzpJXTw7ZicAfkGqy','User01@gmail.com',0),
                        (2, 'User2', 'User2_name', '$2a$10$Yv77WND4ZhFQZLXT11jk8Ow/zgJeYfdHwd/DbSuHTPR0UCU1B.tJG','User02@gmail.com',0)
ON CONFLICT DO NOTHING;

INSERT INTO patient_table (dentist_id,medical_record, dni, name, address, birthdate, telephone, email) VALUES
                        (1, 215, 429875421, 'María López', 'Av. Rivadavia 2020', '1995-05-23', 1145678920, 'maria.lopez@gmail.com'),
                        (1, 318, 412345678, 'Carlos Fernández', 'Mitre 789', '1988-09-14', 1167890123, 'carlos.fernandez@gmail.com'),
                        (1, 507, 437654321, 'Ana Martínez', 'San Martín 456', '1992-12-30', 1156789012, 'ana.martinez@gmail.com'),
                        (2, 623, 405678912, 'Sofía Ramírez', 'Corrientes 333', '1999-07-18', 1176543289, 'sofia.ramirez@gmail.com'),
                        (2, 731, 428901234, 'Diego Torres', 'Belgrano 220', '1985-03-05', 1134567890, 'diego.torres@gmail.com')
ON CONFLICT DO NOTHING;