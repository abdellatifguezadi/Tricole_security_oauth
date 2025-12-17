-- liquibase formatted sql

-- changeset abdellatif:006-insert-permissions
INSERT INTO permissions (name, description, resource, action) VALUES
-- FOURNISSEURS
('FOURNISSEUR_CREATE', 'Créer un fournisseur', 'FOURNISSEUR', 'CREATE'),
('FOURNISSEUR_UPDATE', 'Modifier un fournisseur', 'FOURNISSEUR', 'UPDATE'),
('FOURNISSEUR_DELETE', 'Supprimer un fournisseur', 'FOURNISSEUR', 'DELETE'),
('FOURNISSEUR_READ', 'Consulter les fournisseurs', 'FOURNISSEUR', 'READ'),

-- PRODUITS
('PRODUIT_CREATE', 'Créer un produit', 'PRODUIT', 'CREATE'),
('PRODUIT_UPDATE', 'Modifier un produit', 'PRODUIT', 'UPDATE'),
('PRODUIT_DELETE', 'Supprimer un produit', 'PRODUIT', 'DELETE'),
('PRODUIT_READ', 'Consulter les produits', 'PRODUIT', 'READ'),
('PRODUIT_CONFIGURE_SEUIL', 'Configurer seuils alerte', 'PRODUIT', 'CONFIGURE'),

-- COMMANDES
('COMMANDE_CREATE', 'Créer une commande', 'COMMANDE', 'CREATE'),
('COMMANDE_UPDATE', 'Modifier une commande', 'COMMANDE', 'UPDATE'),
('COMMANDE_VALIDATE', 'Valider une commande', 'COMMANDE', 'VALIDATE'),
('COMMANDE_CANCEL', 'Annuler une commande', 'COMMANDE', 'CANCEL'),
('COMMANDE_RECEIVE', 'Réceptionner une commande', 'COMMANDE', 'RECEIVE'),
('COMMANDE_READ', 'Consulter les commandes', 'COMMANDE', 'READ'),

-- STOCK
('STOCK_READ', 'Consulter stock/lots', 'STOCK', 'READ'),
('STOCK_VALORISATION', 'Voir valorisation FIFO', 'STOCK', 'VALORISATION'),
('STOCK_HISTORIQUE', 'Consulter historique mouvements', 'STOCK', 'HISTORIQUE'),

-- BONS DE SORTIE
('BON_SORTIE_CREATE', 'Créer bon de sortie', 'BON_SORTIE', 'CREATE'),
('BON_SORTIE_VALIDATE', 'Valider bon de sortie', 'BON_SORTIE', 'VALIDATE'),
('BON_SORTIE_CANCEL', 'Annuler bon de sortie', 'BON_SORTIE', 'CANCEL'),
('BON_SORTIE_READ', 'Consulter bons de sortie', 'BON_SORTIE', 'READ'),

-- ADMINISTRATION
('USER_MANAGE', 'Gérer les utilisateurs', 'USER', 'MANAGE'),
('AUDIT_READ', 'Voir logs audit', 'AUDIT', 'READ');

-- changeset abdellatif:006-insert-roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrateur - Accès complet'),
('RESPONSABLE_ACHATS', 'Responsable Achats - Gestion approvisionnements'),
('MAGASINIER', 'Magasinier - Gestion stock et réceptions'),
('CHEF_ATELIER', 'Chef Atelier - Consultation et bons de sortie');

-- changeset abdellatif:006-assign-permissions-admin
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- changeset abdellatif:006-assign-permissions-responsable-achats
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'RESPONSABLE_ACHATS'
AND p.name IN (
    'FOURNISSEUR_CREATE', 'FOURNISSEUR_UPDATE', 'FOURNISSEUR_DELETE', 'FOURNISSEUR_READ',
    'PRODUIT_CREATE', 'PRODUIT_UPDATE', 'PRODUIT_DELETE', 'PRODUIT_READ', 'PRODUIT_CONFIGURE_SEUIL',
    'COMMANDE_CREATE', 'COMMANDE_UPDATE', 'COMMANDE_VALIDATE', 'COMMANDE_CANCEL', 'COMMANDE_READ',
    'STOCK_READ', 'STOCK_VALORISATION', 'STOCK_HISTORIQUE',
    'BON_SORTIE_READ'
);

-- changeset abdellatif:006-assign-permissions-magasinier
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MAGASINIER'
AND p.name IN (
    'FOURNISSEUR_READ',
    'PRODUIT_READ',
    'COMMANDE_RECEIVE', 'COMMANDE_READ',
    'STOCK_READ', 'STOCK_VALORISATION', 'STOCK_HISTORIQUE',
    'BON_SORTIE_CREATE', 'BON_SORTIE_VALIDATE', 'BON_SORTIE_CANCEL', 'BON_SORTIE_READ'
);

-- changeset abdellatif:006-assign-permissions-chef-atelier
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'CHEF_ATELIER'
AND p.name IN (
    'PRODUIT_READ',
    'STOCK_READ', 'STOCK_HISTORIQUE',
    'BON_SORTIE_CREATE', 'BON_SORTIE_READ'
);

-- rollback DELETE FROM role_permissions;
-- rollback DELETE FROM roles;
-- rollback DELETE FROM permissions;
