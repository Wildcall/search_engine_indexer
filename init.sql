CREATE USER indexer_user WITH PASSWORD 'indexer_password';

GRANT ALL PRIVILEGES ON DATABASE se_indexer_data to indexer_user;