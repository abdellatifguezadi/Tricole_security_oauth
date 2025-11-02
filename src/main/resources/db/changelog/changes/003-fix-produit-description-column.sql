-- liquibase formatted sql

-- changeset Pood16:003-fix-produit-description-column
ALTER TABLE produits MODIFY COLUMN description LONGTEXT;

-- rollback ALTER TABLE produits MODIFY COLUMN description TEXT;