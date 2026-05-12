INSERT INTO categories (name, description)
SELECT 'Fiction', 'Novels and literary fiction'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Fiction');

INSERT INTO categories (name, description)
SELECT 'Science', 'Science and technology'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Science');

INSERT INTO categories (name, description)
SELECT 'History', 'Historical works'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'History');

INSERT INTO authors (name, bio, country)
SELECT 'Imen Barkallah', 'BookCloud demo author', 'Tunisia'
WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Imen Barkallah');

INSERT INTO books (
    title,
    description,
    author,
    category_id,
    total_copies,
    available_copies,
    default_branch_id,
    publication_year,
    language,
    publisher,
    featured,
    hidden
)
SELECT
    'The Digital Library',
    'A demo title to verify the catalogue and home page.',
    'Imen Barkallah',
    (SELECT id FROM categories WHERE name = 'Fiction' LIMIT 1),
    3,
    3,
    (SELECT id FROM library_branches ORDER BY id LIMIT 1),
    2026,
    'French',
    'BookCloud Press',
    TRUE,
    FALSE
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title = 'The Digital Library');

INSERT INTO books (
    title,
    description,
    author,
    category_id,
    total_copies,
    available_copies,
    default_branch_id,
    publication_year,
    language,
    publisher,
    featured,
    hidden
)
SELECT
    'Spring Boot in Practice',
    'A demo technical book for the catalogue.',
    'Imen Barkallah',
    (SELECT id FROM categories WHERE name = 'Science' LIMIT 1),
    2,
    2,
    (SELECT id FROM library_branches ORDER BY id LIMIT 1),
    2025,
    'English',
    'BookCloud Press',
    TRUE,
    FALSE
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title = 'Spring Boot in Practice');

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id
FROM books b
JOIN authors a ON a.name = 'Imen Barkallah'
WHERE b.title IN ('The Digital Library', 'Spring Boot in Practice')
  AND NOT EXISTS (
      SELECT 1
      FROM book_authors ba
      WHERE ba.book_id = b.id AND ba.author_id = a.id
  );
