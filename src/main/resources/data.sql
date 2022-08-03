MERGE INTO mpa (mpa_rating_id, mpa_name) VALUES (1, 'G');
MERGE INTO mpa (mpa_rating_id, mpa_name) VALUES (2, 'PG');
MERGE INTO mpa (mpa_rating_id, mpa_name) VALUES (3, 'PG-13');
MERGE INTO mpa (mpa_rating_id, mpa_name) VALUES (4, 'R');
MERGE INTO mpa (mpa_rating_id, mpa_name) VALUES (5, 'NC-17');

MERGE INTO genre (genre_id, genre_name) VALUES (1, 'Комедия');
MERGE INTO genre (genre_id, genre_name) VALUES (2, 'Драма');
MERGE INTO genre (genre_id, genre_name) VALUES (3, 'Мультфильм');
MERGE INTO genre (genre_id, genre_name) VALUES (4, 'Триллер');
MERGE INTO genre (genre_id, genre_name) VALUES (5, 'Документальный');
MERGE INTO genre (genre_id, genre_name) VALUES (6, 'Боевик');