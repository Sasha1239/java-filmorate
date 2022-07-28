# java-filmorate
Template repository for Filmorate project.

**Схема БД:**
![QuickDBD-export (1)](https://user-images.githubusercontent.com/68199637/181582278-a0d5bc50-875d-44fc-b75d-74891467ac7c.png)

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

`SELECT * FROM Friends fr
WHERE friend_id (SELECT * FROM Friends fr
WHERE user_id = идентификатор пользователя);`

*Фильмы:*

- Получение всех фильмов:

`SELECT * FROM Film f`

- Получение фильма по идентификатору:

`SELECT * FROM Film f
WHERE film_id = идентификатор фильма;`

- Получение самых популярныйх фильмов по кол-ву лайков или первые 10 фильмов:

`SELECT * FROM Film f
INNER JOIN Film_Likes as fl ON fl.film_id = f.film_id
LIMIT 10;`
