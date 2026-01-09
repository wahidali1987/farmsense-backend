INSERT INTO users (
    user_id, name, email, password_hash, role, is_active, created_at, updated_at
) VALUES
      (
          gen_random_uuid(),
          'Farmer One',
          'farmer1@farmsense.ai',
          '$2a$10$u1Zc1yq7yE6Z0V8ZJ9N6VOVf5yDkR0O0u2Fh9nK4N6F4vZ9Y0mB8G',
          'FARMER',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          gen_random_uuid(),
          'Manager One',
          'manager@farmsense.ai',
          '$2a$10$u1Zc1yq7yE6Z0V8ZJ9N6VOVf5yDkR0O0u2Fh9nK4N6F4vZ9Y0mB8G',
          'MANAGER',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      );