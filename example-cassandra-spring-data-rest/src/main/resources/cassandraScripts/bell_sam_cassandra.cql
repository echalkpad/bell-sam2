CREATE KEYSPACE IF NOT EXISTS bell_sam WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'} AND durable_writes = true;

USE bell_sam;

CREATE TABLE IF NOT EXISTS subscribers (id text PRIMARY KEY, first_name text, surname text, message text);
