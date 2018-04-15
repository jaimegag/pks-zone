CREATE KEYSPACE pks WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};
CREATE TABLE pks.species (
        id int PRIMARY KEY,
        county text,
        category text,
        taxonomy_g text,
        taxonomy_sg text,
        sci_name text,
        common_name text
       );
COPY pks.species (id,county,category,taxonomy_g,taxonomy_sg,sci_name,common_name) FROM 'species_nyc.csv' WITH DELIMITER=',' AND HEADER=TRUE;
