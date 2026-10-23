CREATE TABLE accounts (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          email VARCHAR(255) NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT uk_accounts_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE account_roles (
                               account_id BIGINT NOT NULL,
                               role ENUM ('ADMIN','PROFESSIONAL','USER'),
                               CONSTRAINT uk_account_roles_account_role UNIQUE (account_id, role),
                               CONSTRAINT fk_account_roles_account FOREIGN KEY (account_id) REFERENCES accounts (id)
) ENGINE=InnoDB;

CREATE TABLE user (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      account_id BIGINT NOT NULL,
                      first_name VARCHAR(255),
                      last_name VARCHAR(255),
                      phone VARCHAR(255),
                      PRIMARY KEY (id),
                      CONSTRAINT uk_user_account UNIQUE (account_id),
                      CONSTRAINT fk_user_account FOREIGN KEY (account_id) REFERENCES accounts (id)
) ENGINE=InnoDB;

CREATE TABLE professional (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              account_id BIGINT NOT NULL,
                              first_name VARCHAR(255),
                              last_name VARCHAR(255),
                              phone VARCHAR(255),
                              specialty VARCHAR(255),
                              years_of_experience INTEGER,
                              available BIT,
                              PRIMARY KEY (id),
                              CONSTRAINT uk_professional_account UNIQUE (account_id),
                              CONSTRAINT fk_professional_account FOREIGN KEY (account_id) REFERENCES accounts (id)
) ENGINE=InnoDB;

CREATE TABLE appointment (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             date_time DATETIME(6),
                             status ENUM ('CANCELLED','CONFIRMED','PENDING'),
                             user_id BIGINT,
                             professional_id BIGINT,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_appointment_user FOREIGN KEY (user_id) REFERENCES user (id),
                             CONSTRAINT fk_appointment_professional FOREIGN KEY (professional_id) REFERENCES professional (id)
) ENGINE=InnoDB;