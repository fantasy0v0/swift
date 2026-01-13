CREATE TABLE student (
  id     bigint NOT NULL,
  name   text NOT NULL,
  status INT NOT NULL,
  ext    text
);
CREATE TABLE datetime_test (
  id     bigint    NOT NULL,
  date timestamp
);
insert into student(id, name, status) values
(1, '小明', 0), (2, '张三', 1),
(3, '李四', 2), (4, '董超', 2),
(5, '薛霸', 2);


CREATE TABLE swift_user (
  id          bigint PRIMARY KEY auto_increment,
  name        text not null,
  locale      text,
  avatar      text,
  status      integer                  not null,
  is_del      boolean,
  description text                     null,
  ext         json                     null,
  created_at  datetime not null default now(),
  updated_at  datetime not null default now()
);
insert into swift_user(name, status)
values ('小明', 0),
       ('小王', 1),
       ('小李', 2),
       ('小赵', 2),
       ('小钱', 0);
