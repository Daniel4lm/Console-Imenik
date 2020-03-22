DROP DATABASE IF EXISTS TelefonskiImenik ;
CREATE DATABASE TelefonskiImenik;

USE TelefonskiImenik;

CREATE TABLE osoba(
	ID_Osoba INT primary key auto_increment,
    Ime VARCHAR(20) NOT NULL,
    Prezime VARCHAR(20) NOT NULL
);

CREATE TABLE imenik(
	ID INT primary key auto_increment,
    ID_Osoba INT NOT NULL,
    Br_tel VARCHAR(20) NOT NULL,
    foreign key(ID_Osoba) references osoba(ID_Osoba)
);
