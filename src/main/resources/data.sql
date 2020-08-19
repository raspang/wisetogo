
create table if not exists persistent_logins ( 
  username varchar(100) not null, 
  series varchar(64) primary key, 
  token varchar(64) not null, 
  last_used timestamp not null
);

delete from  user_roles;
delete from  roles;
delete from  users;


INSERT INTO roles (id, name) VALUES 
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER')
;


INSERT INTO users (id, email, username, password, name) VALUES 
(1, 'admin@gmail.com', 'admin', '$2a$10$baR1hKyJn/UGu/8AKeQq7uKpTqdSC47mudh8S6LLIEdxOTl496qRW', 'Admin'),
(2, 'user@gmail.com', 'user', '$2a$10$baR1hKyJn/UGu/8AKeQq7uKpTqdSC47mudh8S6LLIEdxOTl496qRW', 'User');


insert into user_roles(user_id, role_id) values
(1,1),
(1,2),
(2,2);
