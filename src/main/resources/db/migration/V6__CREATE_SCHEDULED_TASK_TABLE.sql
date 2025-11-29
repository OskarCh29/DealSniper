CREATE TABLE scheduled_tasks (
task_name VARCHAR(100) NOT NULL UNIQUE,
user_id UUID NOT NULL,
source_id BIGINT NOT NULL,
active BOOLEAN DEFAULT TRUE,
PRIMARY KEY (user_id, source_id),
CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
CONSTRAINT fk_task_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE CASCADE);