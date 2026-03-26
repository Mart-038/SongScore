DROP DATABASE IF EXISTS SongScore;
CREATE DATABASE SongScore;

CREATE USER IF NOT EXISTS 'userSongScore'@'localhost'
       IDENTIFIED BY 'userSongScorePW';
GRANT CREATE, SELECT, INSERT, UPDATE, DELETE ON SongScore.*
      TO 'userSongScore'@'localhost';
FLUSH privileges;