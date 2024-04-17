INSERT INTO public.role (name) VALUES ('ROLE_USER'), ('ROLE_MODERATOR'), ('ROLE_ADMIN');

INSERT INTO public.users (username, password, email) VALUES ('admin1', '$2a$10$05W9HZtQaPqaZvNCa6m6QOnnyZ18Qo8kQuPJX7lxnNr3A6SpWSA9.',  'admin1@admin1.com');

INSERT INTO public.users_role (user_id, role_id) VALUES (1, 3);

INSERT INTO public.type_of_reaction (type) VALUES ('LIKE'), ('DISLIKE');

INSERT INTO public.state (type) VALUES ('VISIBLE'), ('BANNED');