# ⚔ FactionStats — Plugin Minecraft 1.21

Plugin de **statistiques complètes** pour serveur **Faction Survie** Minecraft 1.21 (Spigot/Paper).

---

## 📦 Installation

### Prérequis
- Java 21+
- Spigot ou Paper **1.21**
- Maven (pour compiler depuis les sources)

### Compiler le plugin
```bash
cd FactionStats
mvn clean package
```
Le fichier `.jar` se trouve dans `target/FactionStats-1.0.0.jar`.

### Installer
1. Copiez `FactionStats-1.0.0.jar` dans le dossier `plugins/` de votre serveur.
2. Redémarrez le serveur.
3. Les fichiers de configuration sont créés automatiquement dans `plugins/FactionStats/`.

---

## 🎮 Commandes

### `/stats [joueur]`
Affiche les statistiques complètes d'un joueur.

| Usage | Description |
|-------|-------------|
| `/stats` | Vos propres statistiques |
| `/stats Steve` | Statistiques de Steve |

**Aliases :** `/statistiques`, `/stat`

---

### `/classement [categorie]`
Affiche le classement **Top 10** dans une catégorie.

| Commande | Classement |
|----------|------------|
| `/classement` | Menu des catégories |
| `/classement mobs` | Mobs hostiles tués |
| `/classement pvp` | Joueurs tués (PvP) |
| `/classement kd` | Ratio Kills/Deaths |
| `/classement advancements` | Progrès accomplis |
| `/classement blocs` | Blocs cassés |
| `/classement morts` | Nombre de morts |
| `/classement temps` | Temps de jeu |
| `/classement dommages` | Dommages infligés |

**Aliases :** `/top`, `/leaderboard`, `/lb`

---

## 📊 Statistiques suivies

### ⚔ Combat
- **Mobs hostiles tués** — Zombies, Squelettes, Creepers, Phantoms, Wardens, Ender Dragon, Wither, etc.
- **Joueurs tués (PvP)** — Total des kills PvP
- **Morts** — Total des morts (PvP, mobs, chutes, feu, etc.)
- **Ratio K/D** — Ratio kills/deaths calculé automatiquement
- **Dommages infligés** — En demi-cœurs
- **Dommages reçus** — En demi-cœurs

### ★ Progression
- **Advancements accomplis** — Progrès complétés (recettes exclues)

### ⛏ Survie & Construction
- **Blocs cassés** — Total des blocs détruits
- **Blocs placés** — Total des blocs posés
- **Distance parcourue** — En blocs (à pied, à cheval, en bateau, etc.)
- **Items craftés** — Total des fabrications
- **Items ramassés** — Total des objets collectés

### ⏱ Général
- **Temps de jeu** — Depuis la première connexion (affiché en jours/heures/minutes/secondes)
- **Première connexion** — Date de la première venue sur le serveur
- **Dernière connexion** — Date de la dernière session

---

## 🔑 Permissions

| Permission | Description | Défaut |
|------------|-------------|--------|
| `factionstats.stats` | Voir ses propres statistiques | Tous |
| `factionstats.stats.other` | Voir les stats des autres joueurs | OP |
| `factionstats.classement` | Accéder aux classements | Tous |
| `factionstats.admin` | Commandes administrateur | OP |

---

## ⚙ Configuration (`config.yml`)

```yaml
# Préfixe dans les messages
prefix: "&8[&6FactionStats&8] &r"

# Nombre de joueurs dans le classement (max 10)
classement-taille: 10

# Sauvegarde automatique en secondes (0 = désactivé)
auto-save-interval: 300
```

---

## 💾 Données

Les statistiques sont sauvegardées dans `plugins/FactionStats/playerdata.yml`.

- **Sauvegarde automatique** toutes les 5 minutes (configurable)
- **Sauvegarde à l'arrêt** du serveur (onDisable)
- **Chargement au démarrage** — toutes les données sont gardées depuis la première connexion

---

## 🏗 Structure du projet

```
FactionStats/
├── pom.xml
└── src/main/
    ├── java/fr/factionstats/
    │   ├── FactionStats.java              ← Classe principale
    │   ├── models/
    │   │   └── PlayerStats.java           ← Modèle de données joueur
    │   ├── managers/
    │   │   ├── StatsManager.java          ← Gestion des stats & sauvegarde
    │   │   └── MessageManager.java        ← Messages & formatage
    │   ├── commands/
    │   │   ├── StatsCommand.java          ← /stats
    │   │   └── ClassementCommand.java     ← /classement
    │   └── listeners/
    │       └── StatsListener.java         ← Écoute de tous les événements
    └── resources/
        ├── plugin.yml
        └── config.yml
```

---

## 📋 Exemple d'affichage

### `/stats Steve`
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ⚔ Statistiques de Steve  ✦ Faction Survie
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  » Première connexion : 01/01/2025
  » Dernière connexion  : 26/06/2026
  » Temps de jeu total  : 12j 4h 32m 10s

  ⚔ COMBAT (classement PvP: #3)
  ──────────────────────────────────────
  › Mobs hostiles tués    : 4 821  (#2)
  › Joueurs tués (PvP)    : 127    (#3)
  › Morts                 : 43
  › Ratio K/D             : 2.95
  › Dommages infligés      : 98 432 ❤
  › Dommages reçus         : 21 876 ❤

  ★ ADVANCEMENTS (classement: #1)
  ──────────────────────────────────────
  › Progrès accomplis     : 89  (#1)

  ⛏ SURVIE & CONSTRUCTION
  ──────────────────────────────────────
  › Blocs cassés          : 102 847
  › Blocs placés          : 56 231
  › Distance parcourue    : 1 243 567 blocs
  › Items craftés         : 3 412
  › Items ramassés        : 18 943
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### `/classement mobs`
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ⚔ CLASSEMENT — MOBS HOSTILES TUÉS
  Top 10 joueurs depuis le début du serveur
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ✦ Herobrine    — 12 543 mobs
  ✦ Steve        — 4 821 mobs
  ✦ Notch        — 3 210 mobs
  #4 Alex        — 2 987 mobs
  #5 Dinnerbone  — 1 654 mobs
  ...
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 🐛 Compatibilité

| Serveur | Version | Statut |
|---------|---------|--------|
| Spigot  | 1.21    | ✅ Compatible |
| Paper   | 1.21    | ✅ Compatible |
| Purpur  | 1.21    | ✅ Compatible |
