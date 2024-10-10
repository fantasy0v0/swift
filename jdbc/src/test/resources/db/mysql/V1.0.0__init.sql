CREATE TABLE student (
  id     bigint NOT NULL,
  name   text NOT NULL,
  status INT NOT NULL,
  ext    text
);
CREATE TABLE datetime_test (
  id     bigint    NOT NULL,
  date   timestamp NOT NULL
);
insert into student(id, name, status) values
(1, '小明', 0), (2, '张三', 1),
(3, '李四', 2), (4, '董超', 2),
(5, '薛霸', 2);
