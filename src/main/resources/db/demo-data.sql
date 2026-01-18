-- =====================================================
-- DEMO DATA FOR AUTHENTICATION TESTING
-- Password for all users: "password123"
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4P6U6TvDsuGi3zThh6q5C8lvaO4K
-- =====================================================

-- 1. Insert Permissions
INSERT INTO permissions (id, code, description, created_at)
VALUES 
    ('a1111111-1111-1111-1111-111111111111', 'user:read', 'View user information', NOW()),
    ('a2222222-2222-2222-2222-222222222222', 'user:write', 'Create and update users', NOW()),
    ('a3333333-3333-3333-3333-333333333333', 'user:delete', 'Delete users', NOW()),
    ('a4444444-4444-4444-4444-444444444444', 'role:read', 'View roles', NOW()),
    ('a5555555-5555-5555-5555-555555555555', 'role:write', 'Manage roles', NOW()),
    ('a6666666-6666-6666-6666-666666666666', 'product:read', 'View products', NOW()),
    ('a7777777-7777-7777-7777-777777777777', 'product:write', 'Manage products', NOW()),
    ('a8888888-8888-8888-8888-888888888888', 'order:read', 'View orders', NOW()),
    ('a9999999-9999-9999-9999-999999999999', 'order:write', 'Manage orders', NOW())
ON CONFLICT (id) DO NOTHING;

-- 2. Insert Roles
INSERT INTO roles (id, code, name, description, status, created_at)
VALUES 
    ('b1111111-1111-1111-1111-111111111111', 'ADMIN', 'Administrator', 'Full system access', 'ACTIVE', NOW()),
    ('b2222222-2222-2222-2222-222222222222', 'MANAGER', 'Manager', 'Manage products and orders', 'ACTIVE', NOW()),
    ('b3333333-3333-3333-3333-333333333333', 'SALES', 'Sales Staff', 'Handle orders', 'ACTIVE', NOW()),
    ('b4444444-4444-4444-4444-444444444444', 'VIEWER', 'Viewer', 'Read-only access', 'ACTIVE', NOW())
ON CONFLICT (id) DO NOTHING;

-- 3. Assign Permissions to Roles

-- ADMIN: All permissions
INSERT INTO role_permissions (role_id, permission_id)
VALUES 
    ('b1111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111'),
    ('b1111111-1111-1111-1111-111111111111', 'a2222222-2222-2222-2222-222222222222'),
    ('b1111111-1111-1111-1111-111111111111', 'a3333333-3333-3333-3333-333333333333'),
    ('b1111111-1111-1111-1111-111111111111', 'a4444444-4444-4444-4444-444444444444'),
    ('b1111111-1111-1111-1111-111111111111', 'a5555555-5555-5555-5555-555555555555'),
    ('b1111111-1111-1111-1111-111111111111', 'a6666666-6666-6666-6666-666666666666'),
    ('b1111111-1111-1111-1111-111111111111', 'a7777777-7777-7777-7777-777777777777'),
    ('b1111111-1111-1111-1111-111111111111', 'a8888888-8888-8888-8888-888888888888'),
    ('b1111111-1111-1111-1111-111111111111', 'a9999999-9999-9999-9999-999999999999')
ON CONFLICT DO NOTHING;

-- MANAGER: Product and order management
INSERT INTO role_permissions (role_id, permission_id)
VALUES 
    ('b2222222-2222-2222-2222-222222222222', 'a1111111-1111-1111-1111-111111111111'),
    ('b2222222-2222-2222-2222-222222222222', 'a6666666-6666-6666-6666-666666666666'),
    ('b2222222-2222-2222-2222-222222222222', 'a7777777-7777-7777-7777-777777777777'),
    ('b2222222-2222-2222-2222-222222222222', 'a8888888-8888-8888-8888-888888888888'),
    ('b2222222-2222-2222-2222-222222222222', 'a9999999-9999-9999-9999-999999999999')
ON CONFLICT DO NOTHING;

-- SALES: Order management
INSERT INTO role_permissions (role_id, permission_id)
VALUES 
    ('b3333333-3333-3333-3333-333333333333', 'a6666666-6666-6666-6666-666666666666'),
    ('b3333333-3333-3333-3333-333333333333', 'a8888888-8888-8888-8888-888888888888'),
    ('b3333333-3333-3333-3333-333333333333', 'a9999999-9999-9999-9999-999999999999')
ON CONFLICT DO NOTHING;

-- VIEWER: Read-only
INSERT INTO role_permissions (role_id, permission_id)
VALUES 
    ('b4444444-4444-4444-4444-444444444444', 'a1111111-1111-1111-1111-111111111111'),
    ('b4444444-4444-4444-4444-444444444444', 'a6666666-6666-6666-6666-666666666666'),
    ('b4444444-4444-4444-4444-444444444444', 'a8888888-8888-8888-8888-888888888888')
ON CONFLICT DO NOTHING;

-- 4. Insert Users (Password: password123)
INSERT INTO users (id, username, email, password_hash, status, is_system, created_at, updated_at)
VALUES 
    ('c1111111-1111-1111-1111-111111111111', 'admin', 'admin@example.com', 
     '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4P6U6TvDsuGi3zThh6q5C8lvaO4K', 
     'ACTIVE', true, NOW(), NOW()),
    ('c2222222-2222-2222-2222-222222222222', 'manager', 'manager@example.com', 
     '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4P6U6TvDsuGi3zThh6q5C8lvaO4K', 
     'ACTIVE', false, NOW(), NOW()),
    ('c3333333-3333-3333-3333-333333333333', 'sales01', 'sales01@example.com', 
     '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4P6U6TvDsuGi3zThh6q5C8lvaO4K', 
     'ACTIVE', false, NOW(), NOW()),
    ('c4444444-4444-4444-4444-444444444444', 'viewer', 'viewer@example.com', 
     '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4P6U6TvDsuGi3zThh6q5C8lvaO4K', 
     'ACTIVE', false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 5. Assign Roles to Users
INSERT INTO user_roles (user_id, role_id, assigned_at)
VALUES 
    ('c1111111-1111-1111-1111-111111111111', 'b1111111-1111-1111-1111-111111111111', NOW()), -- admin -> ADMIN
    ('c2222222-2222-2222-2222-222222222222', 'b2222222-2222-2222-2222-222222222222', NOW()), -- manager -> MANAGER
    ('c3333333-3333-3333-3333-333333333333', 'b3333333-3333-3333-3333-333333333333', NOW()), -- sales01 -> SALES
    ('c4444444-4444-4444-4444-444444444444', 'b4444444-4444-4444-4444-444444444444', NOW())  -- viewer -> VIEWER
ON CONFLICT DO NOTHING;

-- =====================================================
-- VERIFICATION QUERY
-- =====================================================
-- Run this to verify data:
-- SELECT u.username, r.code as role, p.code as permission
-- FROM users u
-- JOIN user_roles ur ON u.id = ur.user_id
-- JOIN roles r ON ur.role_id = r.id
-- JOIN role_permissions rp ON r.id = rp.role_id
-- JOIN permissions p ON rp.permission_id = p.id
-- ORDER BY u.username, r.code, p.code;
