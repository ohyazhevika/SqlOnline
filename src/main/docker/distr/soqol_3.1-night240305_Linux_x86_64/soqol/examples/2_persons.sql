create table persons (
        first_name varchar(100),
        last_name  varchar(100),
        age  int,
        gender  varchar(1));

create procedure fill_persons_proc()
as
begin
    insert into persons(first_name, last_name, age, gender) values ('Thomas', 'Allen', 25, 'M');
    insert into persons(first_name, last_name, age, gender) values ('Ronald', 'Mason', 51, 'M');
    insert into persons(first_name, last_name, age, gender) values ('Maria', 'Lloyd', 24, 'F');
    insert into persons(first_name, last_name, age, gender) values ('Martha', 'Kelly', 19, 'F');
    insert into persons(first_name, last_name, age, gender) values ('Susan', 'James', 44, 'F');
    insert into persons(first_name, last_name, age, gender) values ('Mark', 'Harris', 48, 'M');
    insert into persons(first_name, last_name, age, gender) values ('John', 'Gray', 35, 'M');
    insert into persons(first_name, last_name, age, gender) values ('Gary', 'Gibson', 22, 'M');
    insert into persons(first_name, last_name, age, gender) values ('Vincent', 'Foster', 62, 'M');
    insert into persons(first_name, last_name, age, gender) values ('Lisa', 'Elliot', 29, 'F'); 
    insert into persons(first_name, last_name, age, gender) values ('Betty', 'Evans', 34, 'F');
    insert into persons(first_name, last_name, age, gender) values ('Marilyn', 'Davies', 27, 'F');
    insert into persons(first_name, last_name, age, gender) values ('Roy', 'Armstrong', 55, 'M');
    insert into persons(first_name, last_name, age, gender) values ('Sin', 'Hu', 37, 'M');
    insert into persons(first_name, last_name, age, gender) values ('Tai', 'Boo', 31, 'M');
end;
/

call fill_persons_proc();
