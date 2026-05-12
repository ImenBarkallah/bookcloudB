CREATE TABLE favorite_books (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_favorite_books_user_book UNIQUE (user_id, book_id),
    CONSTRAINT fk_favorite_books_user FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_books_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

INSERT INTO favorite_books (user_id, book_id, created_at)
SELECT user_id, book_id, COALESCE(created_at, CURRENT_TIMESTAMP)
FROM user_favorite_books
ORDER BY created_at ASC, user_id ASC, book_id ASC;

CREATE INDEX idx_favorite_books_user_id ON favorite_books(user_id);
CREATE INDEX idx_favorite_books_book_id ON favorite_books(book_id);
CREATE INDEX idx_favorite_books_created_at ON favorite_books(created_at);

DROP TABLE user_favorite_books;
