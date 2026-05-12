CREATE TABLE app_users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    display_name VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(32) NOT NULL DEFAULT 'USER',
    phone VARCHAR(64),
    blocked BOOLEAN NOT NULL DEFAULT FALSE,
    max_active_loans INTEGER,
    membership_expires_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url TEXT
);

CREATE TABLE library_branches (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    opening_hours TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE authors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    bio TEXT,
    country VARCHAR(255),
    image_url TEXT,
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE books (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    author VARCHAR(512),
    isbn VARCHAR(64),
    category_id BIGINT,
    cover_url TEXT,
    total_copies INTEGER NOT NULL DEFAULT 0,
    available_copies INTEGER NOT NULL DEFAULT 0,
    default_branch_id BIGINT,
    publication_year INTEGER,
    language VARCHAR(64),
    publisher VARCHAR(255),
    media_format VARCHAR(64),
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_books_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_books_default_branch FOREIGN KEY (default_branch_id) REFERENCES library_branches(id)
);

CREATE TABLE book_authors (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_book_authors_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_book_authors_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

CREATE TABLE book_copies (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    branch_id BIGINT,
    barcode VARCHAR(255),
    status VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_book_copies_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_book_copies_branch FOREIGN KEY (branch_id) REFERENCES library_branches(id)
);

CREATE TABLE loans (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    branch_id BIGINT,
    copy_id BIGINT,
    borrowed_at TIMESTAMP NOT NULL,
    due_at TIMESTAMP NOT NULL,
    returned_at TIMESTAMP NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    renewal_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_loans_user FOREIGN KEY (user_id) REFERENCES app_users(id),
    CONSTRAINT fk_loans_branch FOREIGN KEY (branch_id) REFERENCES library_branches(id),
    CONSTRAINT fk_loans_copy FOREIGN KEY (copy_id) REFERENCES book_copies(id)
);

CREATE TABLE reservations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    pickup_branch_id BIGINT,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    queue_position INTEGER,
    CONSTRAINT fk_reservations_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES app_users(id),
    CONSTRAINT fk_reservations_branch FOREIGN KEY (pickup_branch_id) REFERENCES library_branches(id)
);

CREATE TABLE fines (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount_cents BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_fines_loan FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    CONSTRAINT fk_fines_user FOREIGN KEY (user_id) REFERENCES app_users(id)
);

CREATE TABLE partners (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tier VARCHAR(64),
    logo_url TEXT,
    website TEXT,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE news (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    type VARCHAR(64),
    image_url TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE offers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url TEXT,
    type VARCHAR(64),
    start_date DATE,
    end_date DATE,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE offer_related_books (
    offer_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    PRIMARY KEY (offer_id, book_id),
    CONSTRAINT fk_offer_related_books_offer FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE,
    CONSTRAINT fk_offer_related_books_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE TABLE offer_categories (
    offer_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (offer_id, category_id),
    CONSTRAINT fk_offer_categories_offer FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE,
    CONSTRAINT fk_offer_categories_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE staff_assignments (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    branch_id BIGINT,
    staff_role VARCHAR(128),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_assignments_user FOREIGN KEY (user_id) REFERENCES app_users(id),
    CONSTRAINT fk_staff_assignments_branch FOREIGN KEY (branch_id) REFERENCES library_branches(id)
);

CREATE TABLE moderation_reports (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(64) NOT NULL,
    entity_id BIGINT,
    reason VARCHAR(255),
    details TEXT,
    status VARCHAR(64) NOT NULL,
    reporter_uid VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    resolved_by_uid VARCHAR(255),
    resolution_note TEXT,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE app_notifications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    channel VARCHAR(64) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_app_notifications_user FOREIGN KEY (user_id) REFERENCES app_users(id)
);

CREATE TABLE library_history_entries (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    type VARCHAR(64) NOT NULL,
    reference_id BIGINT,
    book_id BIGINT,
    summary TEXT,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_library_history_user FOREIGN KEY (user_id) REFERENCES app_users(id),
    CONSTRAINT fk_library_history_book FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE library_settings (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    default_loan_days INTEGER NOT NULL DEFAULT 14,
    max_active_loans_default INTEGER NOT NULL DEFAULT 5,
    reservation_expiry_days INTEGER NOT NULL DEFAULT 7,
    fine_per_day DOUBLE PRECISION NOT NULL DEFAULT 0,
    max_renewals_per_loan INTEGER NOT NULL DEFAULT 2,
    overdue_fine_per_day_cents BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE transaction_records (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    reservation_phase VARCHAR(64),
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    book_id BIGINT,
    reference_id BIGINT,
    CONSTRAINT fk_transaction_records_user FOREIGN KEY (user_id) REFERENCES app_users(id),
    CONSTRAINT fk_transaction_records_book FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE user_favorite_books (
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, book_id),
    CONSTRAINT fk_user_favorite_books_user FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_favorite_books_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE INDEX idx_loans_user_status ON loans(user_id, status);
CREATE INDEX idx_reservations_book_status ON reservations(book_id, status);
CREATE INDEX idx_books_hidden_featured ON books(hidden, featured);
CREATE INDEX idx_books_category_id ON books(category_id);
CREATE INDEX idx_app_users_uid ON app_users(uid);
CREATE INDEX idx_app_users_email ON app_users(email);
