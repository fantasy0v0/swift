CREATE TABLE student (
  id     bigint NOT NULL,
  name   text NOT NULL,
  status INT NOT NULL,
  ext    text
);
CREATE TABLE datetime_test (
  id     bigint    NOT NULL,
  date    timestamp,
  date_tz timestamptz
);
insert into student(id, name, status) values
(1, '小明', 0), (2, '张三', 1),
(3, '李四', 2), (4, '董超', 2),
(5, '薛霸', 2);

CREATE TABLE swift_user (
  id          bigserial PRIMARY KEY,
  name        text not null,
  locale      text,
  avatar      text,
  status      integer                  not null,
  is_del      boolean,
  description text                     null,
  ext         json                     null,
  tags text[]                   null,
  created_at  timestamptz not null default now(),
  updated_at  timestamptz not null default now()
);
insert into swift_user(name, status, tags)
values ('小明', 0, '{"Java", "JDBC"}'),
       ('小王', 1, '{"Swift", "JDBC"}'),
       ('小李', 2, '{"Github", "PostgreSQL"}'),
       ('小赵', 2, null),
       ('小钱', 0, '{}');
