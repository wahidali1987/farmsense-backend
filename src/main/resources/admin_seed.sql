INSERT INTO users (
    user_id,
    name,
    email,
    password_hash,
    role,
    is_active,
    created_at,
    updated_at
) VALUES (
             gen_random_uuid(),
             'Admin User',
             'admin@farmsense.ai',
             '$2a$10$zrRsvb/yY3/OicWVoO9EEuD.TAiJAiTOhkak5go/w1pwbyIrIabjK',
             'ADMIN',
             true,
             CURRENT_TIMESTAMP,
             CURRENT_TIMESTAMP
         );
