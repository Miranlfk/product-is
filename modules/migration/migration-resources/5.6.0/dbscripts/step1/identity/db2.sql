ALTER TABLE IDN_OAUTH_CONSUMER_APPS ADD ID_TOKEN_EXPIRE_TIME BIGINT DEFAULT 3600
/
CREATE TABLE IDN_AUTH_TEMP_SESSION_STORE (
            SESSION_ID VARCHAR (100) NOT NULL,
            SESSION_TYPE VARCHAR(100) NOT NULL,
            OPERATION VARCHAR(10) NOT NULL,
            SESSION_OBJECT BLOB,
            TIME_CREATED BIGINT NOT NULL,
            TENANT_ID INTEGER DEFAULT -1,
            EXPIRY_TIME BIGINT,
            PRIMARY KEY (SESSION_ID, SESSION_TYPE, TIME_CREATED, OPERATION)
)
/
ALTER TABLE IDN_AUTH_SESSION_STORE ADD EXPIRY_TIME BIGINT
/
