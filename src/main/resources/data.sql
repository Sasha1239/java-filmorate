MERGE INTO MPA (MPA_RATING_ID, MPA_NAME) VALUES (1, 'G');
MERGE INTO MPA (MPA_RATING_ID, MPA_NAME) VALUES (2, 'PG');
MERGE INTO MPA (MPA_RATING_ID, MPA_NAME) VALUES (3, 'PG-13');
MERGE INTO MPA (MPA_RATING_ID, MPA_NAME) VALUES (4, 'R');
MERGE INTO MPA (MPA_RATING_ID, MPA_NAME) VALUES (5, 'NC-17');

MERGE INTO GENRE (GENRE_ID, GENRE_NAME) VALUES (1, 'Комедия');
MERGE INTO GENRE (GENRE_ID, GENRE_NAME) VALUES (2, 'Драма');
MERGE INTO GENRE (GENRE_ID, GENRE_NAME) VALUES (3, 'Мультфильм');
MERGE INTO GENRE (GENRE_ID, GENRE_NAME) VALUES (4, 'Триллер');
MERGE INTO GENRE (GENRE_ID, GENRE_NAME) VALUES (5, 'Документальный');
MERGE INTO GENRE (GENRE_ID, GENRE_NAME) VALUES (6, 'Боевик');

MERGE INTO DIRECTORS (DIRECTOR_ID, DIRECTOR_NAME) VALUES ( 1, 'Test' );