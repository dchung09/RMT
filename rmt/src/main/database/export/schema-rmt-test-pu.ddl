
    create table Club (
        id bigint generated by default as identity,
        name varchar(255),
        primary key (id)
    );

    create table Event (
        eventType varchar(31) not null,
        id bigint generated by default as identity,
        comment varchar(255),
        date timestamp not null,
        summary varchar(40) not null,
        time time not null,
        kickOff time,
        club_id bigint not null,
        team_id bigint not null,
        venue_id bigint,
        jersey_id bigint,
        opponent_id bigint,
        primary key (id)
    );

    create table Invitation (
        id bigint generated by default as identity,
        body varchar(255) not null,
        date timestamp not null,
        subject varchar(255) not null,
        event_id bigint,
        primary key (id)
    );

    create table Jersey (
        id bigint generated by default as identity,
        shirt varchar(255),
        shorts varchar(255),
        socks varchar(255),
        team_id bigint,
        primary key (id)
    );

    create table Manager (
        team_id bigint not null,
        user_id bigint not null
    );

    create table Opponent (
        id bigint generated by default as identity,
        name varchar(40) not null,
        url varchar(255),
        primary key (id)
    );

    create table Player (
        id bigint generated by default as identity,
        optional boolean not null,
        team_id bigint,
        user_id bigint,
        primary key (id)
    );

    create table Response (
        id bigint generated by default as identity,
        comment varchar(255),
        date timestamp not null,
        guestName varchar(255),
        managerComment varchar(255),
        status varchar(255) not null,
        event_id bigint,
        player_id bigint,
        primary key (id)
    );

    create table Role (
        id bigint generated by default as identity,
        authority varchar(255) not null,
        user_id bigint,
        primary key (id),
        unique (user_id, authority)
    );

    create table Team (
        id bigint generated by default as identity,
        name varchar(40) not null,
        url varchar(255),
        club_id bigint not null,
        primary key (id),
        unique (name, club_id)
    );

    create table Users (
        id bigint generated by default as identity,
        username varchar(255) not null unique,
        enabled boolean not null,
        fullname varchar(255) not null,
        password varchar(255),
        status integer,
        club_id bigint not null,
        primary key (id),
        unique (fullname, club_id)
    );

    create table Venue (
        id bigint generated by default as identity,
        address varchar(255),
        lat double,
        lng double,
        name varchar(80) not null,
        club_id bigint not null,
        primary key (id)
    );

    alter table Event 
        add constraint FK403827A5CB6EDDA 
        foreign key (venue_id) 
        references Venue;

    alter table Event 
        add constraint FK403827AA64B977A 
        foreign key (club_id) 
        references Club;

    alter table Event 
        add constraint FK403827ACA4D421A 
        foreign key (opponent_id) 
        references Opponent;

    alter table Event 
        add constraint FK403827A1C96621A 
        foreign key (team_id) 
        references Team;

    alter table Event 
        add constraint FK403827A2B4253A 
        foreign key (jersey_id) 
        references Jersey;

    alter table Invitation 
        add constraint FKBE1153B9D1F985A6 
        foreign key (event_id) 
        references Event;

    alter table Jersey 
        add constraint FK840B72901C96621A 
        foreign key (team_id) 
        references Team;

    alter table Manager 
        add constraint FK9501A78D699BC35A 
        foreign key (user_id) 
        references Users;

    alter table Manager 
        add constraint FK9501A78D1C96621A 
        foreign key (team_id) 
        references Team;

    alter table Player 
        add constraint FK8EA38701699BC35A 
        foreign key (user_id) 
        references Users;

    alter table Player 
        add constraint FK8EA387011C96621A 
        foreign key (team_id) 
        references Team;

    alter table Response 
        add constraint FKEF917861D1F985A6 
        foreign key (event_id) 
        references Event;

    alter table Response 
        add constraint FKEF917861E4FF039A 
        foreign key (player_id) 
        references Player;

    alter table Role 
        add constraint FK26F496699BC35A 
        foreign key (user_id) 
        references Users;

    alter table Team 
        add constraint FK27B67DA64B977A 
        foreign key (club_id) 
        references Club;

    alter table Users 
        add constraint FK4E39DE8A64B977A 
        foreign key (club_id) 
        references Club;

    alter table Venue 
        add constraint FK4EB7A4FA64B977A 
        foreign key (club_id) 
        references Club;
