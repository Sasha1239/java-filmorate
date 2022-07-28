# java-filmorate
Template repository for Filmorate project.

**Схема БД:**
![QuickDBD-export (1)](https://user-images.githubusercontent.com/68199637/181582812-8cd779d0-702a-4320-a8f2-5d266caec2f7.png)

**Основные SQL запросы:**

*Пользователи:*
 - Получение всех пользователей:
 
`SELECT * FROM User u;`

- Получение пользователя по идентификатору:

`SELECT * FROM User u`
WHERE user_id = идентификатор пользователя;`

- Вывод друзей пользователя:

`SELECT * FROM Friends fr
WHERE user_id = идентификатор пользователя`

- Вывод друзей общих с пользователем:

`SELECT u.*
FROM friends f
JOIN users u ON f.friend_id = u.user_id
WHERE f.user_id = идентификатор пользователя AND status = 'true';`

*Фильмы:*

- Получение всех фильмов:

`SELECT * FROM Film f`

- Получение фильма по идентификатору:

`SELECT * FROM Film f
WHERE film_id = идентификатор фильма;`

- Получение самых популярныйх фильмов по кол-ву лайков или первые 10 фильмов:

`SELECT f.*, fl.raiting
FROM film f
JOIN (SELECT film_id, COUNT(user_id) raiting
FROM film_likes
GROUP BY film_id) fl ON f.film_id =  fl.film_id
ORDER BY fl.raiting DESC
LIMIT 3;`
