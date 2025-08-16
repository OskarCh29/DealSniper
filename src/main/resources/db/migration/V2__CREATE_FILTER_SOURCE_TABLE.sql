CREATE TABLE sources (
id UUID NOT NULL,
user_id UUID NOT NULL,
filtered_url TEXT NOT NULL,
PRIMARY KEY (id),
CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id),
UNIQUE(user_id, filtered_url));