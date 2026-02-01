# NetStatus - SAE_302

**NetStatus** est une application Android développée en Java permettant de consulter en temps réel l’état de fonctionnement de nombreux services en ligne (GitHub, Discord, Cloudflare, etc) via les API StatusPage.io.

L’objectif est d’avoir une vue centralisée et rapide des incidents, maintenances et performances des services les plus utilisés.

## Fonctionnalités

* 🔍 Consultation du statut en temps réel des services
* ⭐ Mise en favori des services pour un accès rapide depuis l’écran d’accueil
* 📄 Accès aux détails d’un service (incidents, maintenances, état global)
* 🖼️ Affichage des logos officiels des services
* 📱 Interface simple et adaptée au mobile

## Services pris en charge

L’application intègre actuellement les services suivants :

* GitHub
* Discord
* Cloudflare
* Reddit
* Atlassian
* Shopify
* DigitalOcean
* Dropbox
* Twilio
* New Relic
* AWeber
* Duo
* Librato
* Coinbase
* iAdvize
* Searchspring
* OpenText
* Mindbody
* Stitch Data
* New York University – Division of Libraries
* Intuit Developer Group
* Epic Games
* Scaleway

*(La liste peut facilement être étendue)*

## Favoris

* Un service peut être ajouté en favori
* Les favoris sont affichés directement sur l’écran d’accueil
* Permet un accès rapide aux services les plus importants

## Détails d’un service

* Cliquez sur un service pour :

  * Voir son statut global
  * Consulter les incidents en cours
  * Vérifier les maintenances planifiées

## Technologies utilisées

* **Langage** : Java
* **Plateforme** : Android
* **API** : StatusPage.io
* **UI** : Android SDK (Activities / Views)
* **Stockage local** : SQLite

## Améliorations possibles

* Migrer les listes vers `RecyclerView`
  Améliorer les performances et la gestion des grandes listes de services.

* Gestion correcte des insets système
  Remplacer l’attribut `fitsSystemWindows` (ancien et peu fiable) par une gestion en Java via les `WindowInsets`, conformément à la documentation Android officielle.

* Paramètres avancés

  * Choix du thème (clair / sombre)
  * Activation ou désactivation du thème système
  * Possibilité d’ajouter manuellement de nouvelles APIs StatusPage

*  Système de notifications

  * Notifications automatiques lorsqu’un service en favori tombe en panne
  * Alertes lors du retour à la normale

## 📄 Licence

Projet personnel / éducatif.
Les logos et marques appartiennent à leurs propriétaires respectifs.
