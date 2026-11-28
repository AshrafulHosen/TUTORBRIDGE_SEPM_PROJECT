CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(30),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'TUTOR', 'GUARDIAN')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'SUSPENDED')),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE areas (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO subjects (name) VALUES
    ('Mathematics'),
    ('Physics'),
    ('Chemistry'),
    ('Higher Math'),
    ('ICT'),
    ('English'),
    ('Science');

INSERT INTO areas (name) VALUES
    ('Sonadanga'),
    ('Gollamari'),
    ('Fulbarigate'),
    ('Boyra'),
    ('Khalishpur'),
    ('Nirala'),
    ('Daulatpur');

CREATE TABLE tutor_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    institution VARCHAR(150),
    bio VARCHAR(1000),
    salary_min INTEGER CHECK (salary_min IS NULL OR salary_min >= 0),
    salary_max INTEGER CHECK (salary_max IS NULL OR salary_max >= 0),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    rating_avg DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    rating_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE guardian_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    home_area_id BIGINT REFERENCES areas(id),
    address_line VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE tutor_subjects (
    tutor_profile_id BIGINT NOT NULL REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    PRIMARY KEY (tutor_profile_id, subject_id)
);

CREATE TABLE tutor_areas (
    tutor_profile_id BIGINT NOT NULL REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    area_id BIGINT NOT NULL REFERENCES areas(id) ON DELETE CASCADE,
    PRIMARY KEY (tutor_profile_id, area_id)
);

CREATE TABLE tutor_class_levels (
    tutor_profile_id BIGINT NOT NULL REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    class_level VARCHAR(50) NOT NULL,
    PRIMARY KEY (tutor_profile_id, class_level)
);

CREATE TABLE tutor_mediums (
    tutor_profile_id BIGINT NOT NULL REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    medium VARCHAR(50) NOT NULL,
    PRIMARY KEY (tutor_profile_id, medium)
);

CREATE TABLE tuition_posts (
    id BIGSERIAL PRIMARY KEY,
    guardian_id BIGINT NOT NULL REFERENCES users(id),
    area_id BIGINT NOT NULL REFERENCES areas(id),
    title VARCHAR(200) NOT NULL,
    class_level VARCHAR(50) NOT NULL,
    medium VARCHAR(50) NOT NULL,
    salary INTEGER NOT NULL CHECK (salary > 0),
    days_per_week INTEGER NOT NULL CHECK (days_per_week BETWEEN 1 AND 7),
    description VARCHAR(2000),
    status VARCHAR(20) NOT NULL CHECK (status IN ('OPEN', 'IN_REVIEW', 'FILLED', 'CLOSED')),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE post_subjects (
    post_id BIGINT NOT NULL REFERENCES tuition_posts(id) ON DELETE CASCADE,
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    PRIMARY KEY (post_id, subject_id)
);

CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES tuition_posts(id) ON DELETE CASCADE,
    tutor_id BIGINT NOT NULL REFERENCES users(id),
    cover_note VARCHAR(1000),
    match_score INTEGER NOT NULL CHECK (match_score BETWEEN 0 AND 100),
    status VARCHAR(20) NOT NULL CHECK (status IN ('APPLIED', 'SHORTLISTED', 'DEMO_SCHEDULED', 'ACCEPTED', 'REJECTED', 'WITHDRAWN')),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_applications_post_tutor UNIQUE (post_id, tutor_id)
);

CREATE TABLE demo_sessions (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL UNIQUE REFERENCES applications(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMPTZ NOT NULL,
    mode VARCHAR(20) NOT NULL CHECK (mode IN ('ONLINE', 'IN_PERSON')),
    location VARCHAR(200) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL UNIQUE REFERENCES tuition_posts(id) ON DELETE CASCADE,
    guardian_id BIGINT NOT NULL REFERENCES users(id),
    tutor_id BIGINT NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    message VARCHAR(300) NOT NULL,
    link VARCHAR(200),
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT NOT NULL REFERENCES users(id),
    action VARCHAR(80) NOT NULL,
    target_type VARCHAR(80) NOT NULL,
    target_id BIGINT,
    details VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE revoked_tokens (
    jti VARCHAR(64) PRIMARY KEY,
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_tuition_posts_status_area ON tuition_posts (status, area_id);
CREATE INDEX idx_applications_post_status ON applications (post_id, status);
CREATE INDEX idx_applications_tutor ON applications (tutor_id);
CREATE INDEX idx_notifications_recipient_created ON notifications (recipient_id, created_at DESC);
