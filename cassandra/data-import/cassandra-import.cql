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
COPY pks.species FROM 'animals_and_plants_per_county_clean_id.csv' WITH DELIMITER=',' AND HEADER=TRUE;
