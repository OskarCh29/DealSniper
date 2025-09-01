CREATE TABLE scheduled_tasks (
user_id UUID NOT NULL,
source_id BIGINT NOT NULL,
active BOOLEAN DEFAULT TRUE,
PRIMARY KEY (user_id, source_id));