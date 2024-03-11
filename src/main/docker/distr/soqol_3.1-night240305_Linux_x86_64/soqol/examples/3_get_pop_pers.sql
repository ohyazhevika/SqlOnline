create table results(num integer, info varchar(4000));

create or replace procedure get_popular_persons(pers_gender varchar(1), up boolean, result_str out varchar(4000))
as
    pers_fname varchar(100);
    pers_lname varchar(100);
    pers_age   integer;

    nrank integer;

    str varchar(4000);

    cursor curs_pers is select first_name, last_name, age from persons order by age;
    cursor curs_names is select distinct num from popular_names where name = pers_fname and gender = pers_gender;

    no_data  EXCEPTION;
    PRAGMA   EXCEPTION_INIT(no_data, -1403);
begin
    delete from results;
    result_str := 'Popular persons names: ';
    open curs_pers;
    fetch curs_pers into pers_fname, pers_lname, pers_age;
    while (curs_pers%found)
    loop
        open curs_names;
        fetch curs_names into nrank;
        while (curs_names%found)
        loop
            result_str := result_str ||  pers_lname || ' , ';
            str := 'First name: '|| pers_fname || ', ' ||' Last name: ' || pers_lname || ', ' || ' Age: ' || pers_age;
            if up then
                str := upper(str);
            end if;
            insert into results values (nrank, str);
            fetch curs_names into nrank;
        end loop;
        close curs_names;
        fetch curs_pers into pers_fname, pers_lname, pers_age;
    end loop;
    close curs_pers;
    exception
        when no_data then
          result_str := 'No data found';
        when others then
          result_str := 'Something went wrong';
end;
/


variable male_popular varchar (4000);

call get_popular_persons('M', false, :male_popular);

print male_popular;

select * from results order by num;

variable femail_popular varchar (4000);

call get_popular_persons('F', true, :femail_popular);

print femail_popular;

select * from results order by num;

