# java-filmorate
Template repository for Filmorate project.

**Схема БД:**
![QuickDBD-export](https://user-images.githubusercontent.com/68199637/180275465-0a6671dc-832a-43b1-b2e2-b7f92bcac913.png)

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
