-- liquibase formatted sql

-- changeset Pood16:004-insert-sample-fournisseurs
INSERT INTO fournisseurs (raison_sociale, adresse, ville, personne_contact, email, telephone, ice, created_at, updated_at) VALUES
('TechSupply Morocco', '123 Rue Hassan II', 'Casablanca', 'Ahmed Benali', 'ahmed.benali@techsupply.ma', '+212522123456', 'ICE001234567890123', NOW(), NOW()),
('ElectroDistrib', '45 Avenue Mohammed V', 'Rabat', 'Fatima Alaoui', 'fatima.alaoui@electrodistrib.ma', '+212537987654', 'ICE001234567890124', NOW(), NOW()),
('IndustrialParts SA', '78 Boulevard Zerktouni', 'Marrakech', 'Omar Tazi', 'omar.tazi@industrialparts.ma', '+212524456789', 'ICE001234567890125', NOW(), NOW());

-- changeset Pood16:004-insert-sample-produits
INSERT INTO produits (reference, nom, description, prix_unitaire, categorie, stock_actuel, point_commande, unite_mesure, created_at, updated_at) VALUES
('PROC001', 'Processeur Intel i7', 'Processeur Intel Core i7 dernière génération pour ordinateurs haute performance', 2500.00, 'Informatique', 50.00, 10, 'Unité', NOW(), NOW()),
('RAM002', 'Mémoire RAM 16GB DDR4', 'Barrette mémoire RAM 16GB DDR4 3200MHz pour serveurs et workstations', 800.00, 'Informatique', 75.00, 15, 'Unité', NOW(), NOW()),
('CABLE003', 'Câble Ethernet Cat6', 'Câble réseau Ethernet catégorie 6 blindé pour installations professionnelles', 25.00, 'Réseau', 200.00, 50, 'Mètre', NOW(), NOW()),
('SWITCH004', 'Switch 24 ports Gigabit', 'Commutateur réseau 24 ports Gigabit Ethernet manageable', 1200.00, 'Réseau', 30.00, 5, 'Unité', NOW(), NOW()),
('DISK005', 'Disque SSD 1TB', 'Disque dur SSD 1TB SATA III haute vitesse pour stockage professionnel', 600.00, 'Stockage', 40.00, 8, 'Unité', NOW(), NOW());

# -- changeset Pood16:004-insert-sample-commandes
# INSERT INTO commande_fournisseur (numero_commande, date_commande, date_livraison_prevue, statut, montant_total, fournisseur_id, updated_at) VALUES
# ('CMD-2024-001', '2024-01-15 10:30:00', '2024-01-25', 'EN_ATTENTE', 15000.00, 1, NOW()),
# ('CMD-2024-002', '2024-02-10 14:15:00', '2024-02-20', 'EN_ATTENTE', 8500.00, 2, NOW()),
# ('CMD-2024-003', '2024-03-05 09:45:00', '2024-03-15', 'EN_ATTENTE', 12000.00, 3, NOW());

