ALTER TABLE audit_logs
    ADD COLUMN actor_name VARCHAR(150);

UPDATE audit_logs AS log
SET actor_name = actor.name,
    actor_email = COALESCE(log.actor_email, actor.email)
FROM users AS actor
WHERE log.actor_id = actor.id;
