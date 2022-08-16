CREATE TABLE IF NOT EXISTS USERS(
    USER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    USER_NAME VARCHAR,
    EMAIL VARCHAR,
    LOGIN VARCHAR,
    BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS MPA(
    MPA_RATING_ID INTEGER PRIMARY KEY,
    MPA_NAME VARCHAR
);

CREATE TABLE IF NOT EXISTS FILM(
    FILM_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    FILM_NAME VARCHAR,
    DESCRIPTION VARCHAR(200),
    RELEASE_DATE DATE,
    DURATION INTEGER,
    MPA_RATING INTEGER,
    CONSTRAINT film_mpa_rating FOREIGN KEY (MPA_RATING) REFERENCES MPA (MPA_RATING_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS GENRE(
    GENRE_ID INTEGER PRIMARY KEY,
    GENRE_NAME VARCHAR
);

CREATE TABLE IF NOT EXISTS FILM_GENRE(
    FILM_ID INTEGER,
    GENRE_ID INTEGER,
    PRIMARY KEY (FILM_ID, GENRE_ID),
    CONSTRAINT film_genre_id FOREIGN KEY (FILM_ID) REFERENCES FILM (FILM_ID) ON DELETE CASCADE,
    CONSTRAINT genre_genre_id FOREIGN KEY (GENRE_ID) REFERENCES GENRE (GENRE_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FRIENDS(
    USER_ID INTEGER,
    FRIEND_ID INTEGER,
    PRIMARY KEY (USER_ID, FRIEND_ID),
    CONSTRAINT user_id_friend_id FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FILM_LIKES(
    FILM_ID INTEGER,
    USER_ID INTEGER,
    PRIMARY KEY (FILM_ID, USER_ID),
    CONSTRAINT film_like_film FOREIGN KEY (FILM_ID) REFERENCES FILM (FILM_ID) ON DELETE CASCADE,
    CONSTRAINT user_like_film FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DIRECTORS (
    DIRECTOR_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    DIRECTOR_NAME VARCHAR
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR (
    FILM_ID INTEGER,
    DIRECTOR_ID INTEGER,
    PRIMARY KEY (FILM_ID, DIRECTOR_ID),
    CONSTRAINT FILM_DIRECTOR_FILM_ID FOREIGN KEY (FILM_ID) REFERENCES FILM (FILM_ID) ON DELETE CASCADE ,
    CONSTRAINT FILM_DIRECTOR_DIRECTOR_ID FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTORS (DIRECTOR_ID) ON DELETE CASCADE
)

