INSERT INTO public.role (name) VALUES ('ROLE_USER'), ('ROLE_MODERATOR'), ('ROLE_ADMIN');

INSERT INTO public.image (name) VALUES ('default.png');

INSERT INTO public.achievement (image_id, name) VALUES (1, 'Сигма');

INSERT INTO public.users (username, password, email, image_id, state) VALUES ('admin1', '$2a$10$05W9HZtQaPqaZvNCa6m6QOnnyZ18Qo8kQuPJX7lxnNr3A6SpWSA9.',  'admin1@admin1.com', 1, 'VISIBLE');

INSERT INTO public.users_role (user_id, role_id) VALUES (1, 3);

INSERT INTO public.tag (name) VALUES ('Воронеж'), ('Мусор');

INSERT INTO public.users_achievement (achievement_id, user_id) VALUES (1, 1)
