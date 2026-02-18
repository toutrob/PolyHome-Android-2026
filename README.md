# PolyHome Android 2026

Application Android native permettant le pilotage d’une maison connectée.

Ce projet a été réalisé dans le cadre du module Programmation Mobile Android à Polytech Dijon.

## Présentation

PolyHome est une application mobile permettant de contrôler différents équipements domotiques via une API REST centralisée.

L’utilisateur peut gérer ses maisons, piloter ses équipements à distance et partager l’accès avec d’autres utilisateurs.

## Fonctionnalités

### Authentification sécurisée
* Création de compte
* Connexion utilisateur
* Récupération et stockage sécurisé du Token

### Multi-maisons
* Sélection de la maison à piloter via un menu déroulant

### Persistance de session
* Gestion du token via TokenManager
* Reconnexion automatique au lancement

## Contrôle des équipements
### Pilotage individuel
* Allumer / Éteindre les lumières
* Ouvrir / Fermer les volets roulants
* Ouvrir / Fermer la porte de garage

### Commandes globales
* Bouton Tout allumer / Tout éteindre
* Bouton Tout ouvrir / Tout fermer

### Retour d’état en temps réel
* Icônes dynamiques

### Personnalisation
* Renommage des appareils via appui long

## Gestion des invités
* Ajout d’invités via leur identifiant
* Liste des utilisateurs ayant accès à la maison
* Suppression / Révocation des droits d’accès

## Auteurs
* Robin RUSSIER
* Baptiste SALVIO
