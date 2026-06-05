
    create table cleaning_history (
        was_completed bit not null,
        cleaned_at datetime(6) not null,
        id bigint not null auto_increment,
        room_id bigint not null,
        task_id bigint,
        user_id bigint not null,
        wg_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table rooms (
        frequency integer,
        id bigint not null auto_increment,
        wg_id bigint not null,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table rooms_assignments (
        is_completed bit not null,
        id bigint not null auto_increment,
        room_id bigint not null,
        user_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table tasks (
        level integer,
        id bigint not null auto_increment,
        room_id bigint not null,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table tasks_assignments (
        is_completed bit not null,
        id bigint not null auto_increment,
        task_id bigint not null,
        user_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table users (
        active bit not null,
        birthday date,
        user_type integer,
        id bigint not null auto_increment,
        wg_id bigint,
        email varchar(255) not null,
        name varchar(255) not null,
        password varchar(255) not null,
        refresh_token varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table wgs (
        cleaning_style integer not null,
        rent_style integer,
        id bigint not null auto_increment,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    alter table rooms 
       add constraint UK1kuqhbfxed2e8t571uo82n545 unique (name);

    alter table users 
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table cleaning_history 
       add constraint FK49t4bvt3p079fwbg4vxpv4vl4 
       foreign key (room_id) 
       references rooms (id);

    alter table cleaning_history 
       add constraint FKn5biqoe9unxlnuhkqjq9rhklo 
       foreign key (task_id) 
       references tasks (id);

    alter table cleaning_history 
       add constraint FK21ib7bmjigqr0jx2wv7nevlnb 
       foreign key (user_id) 
       references users (id);

    alter table cleaning_history 
       add constraint FKljg2i1fc6odq37obmgpsoj9jv 
       foreign key (wg_id) 
       references wgs (id);

    alter table rooms 
       add constraint FKrf9fvvcki4w0iv8v32iwualbe 
       foreign key (wg_id) 
       references wgs (id);

    alter table rooms_assignments 
       add constraint FK3qc1314il91v2klrjpbh4n0a4 
       foreign key (room_id) 
       references rooms (id);

    alter table rooms_assignments 
       add constraint FKop9sb9kyt906ln2yc7xn1ihin 
       foreign key (user_id) 
       references users (id);

    alter table tasks 
       add constraint FK2qgc49xgrpfaevy9q4d9rm1xo 
       foreign key (room_id) 
       references rooms (id);

    alter table tasks_assignments 
       add constraint FK8y5ge703k7oybmtfl05ln8bmg 
       foreign key (task_id) 
       references tasks (id);

    alter table tasks_assignments 
       add constraint FKdy3j2bbx01nthpj8panxk3nwn 
       foreign key (user_id) 
       references users (id);

    alter table users 
       add constraint FKrf2fqhoi1emmasa7myi6g3ljv 
       foreign key (wg_id) 
       references wgs (id);
