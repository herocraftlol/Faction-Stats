package fr.factionstats.models;

import java.util.UUID;

/**
 * Modèle représentant les statistiques d'un joueur
 */
public class PlayerStats {

    private final UUID uuid;
    private String playerName;

    // === Statistiques de combat ===
    private long mobsHostilesTues;      // Total mobs hostiles tués
    private long joueursTues;           // Total joueurs tués
    private long morts;                 // Total de morts
    private long dommagesInfliges;      // Dommages infligés (en demi-cœurs)
    private long dommagesRecus;         // Dommages reçus

    // === Statistiques de progression ===
    private long advancementsAccomplis;  // Nombre d'advancements accomplis

    // === Statistiques de survie ===
    private long blocsCasses;           // Blocs cassés
    private long blocsPlaces;           // Blocs placés
    private long distanceParcourue;     // Distance parcourue (en blocs, arrondi)
    private long tempsJoue;             // Temps joué (en ticks, 20 ticks = 1 seconde)

    // === Statistiques de ressources ===
    private long itemsCraftes;          // Items craftés
    private long itemsRamasees;         // Items ramassés

    // === Métadonnées ===
    private long premiereConnexion;     // Timestamp première connexion
    private long derniereConnexion;     // Timestamp dernière connexion

    public PlayerStats(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.mobsHostilesTues = 0;
        this.joueursTues = 0;
        this.morts = 0;
        this.dommagesInfliges = 0;
        this.dommagesRecus = 0;
        this.advancementsAccomplis = 0;
        this.blocsCasses = 0;
        this.blocsPlaces = 0;
        this.distanceParcourue = 0;
        this.tempsJoue = 0;
        this.itemsCraftes = 0;
        this.itemsRamasees = 0;
        this.premiereConnexion = System.currentTimeMillis();
        this.derniereConnexion = System.currentTimeMillis();
    }

    // === UUID & Nom ===
    public UUID getUuid() { return uuid; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    // === Mobs hostiles ===
    public long getMobsHostilesTues() { return mobsHostilesTues; }
    public void setMobsHostilesTues(long mobsHostilesTues) { this.mobsHostilesTues = mobsHostilesTues; }
    public void incrementMobsHostilesTues() { this.mobsHostilesTues++; }

    // === Joueurs ===
    public long getJoueursTues() { return joueursTues; }
    public void setJoueursTues(long joueursTues) { this.joueursTues = joueursTues; }
    public void incrementJoueursTues() { this.joueursTues++; }

    // === Morts ===
    public long getMorts() { return morts; }
    public void setMorts(long morts) { this.morts = morts; }
    public void incrementMorts() { this.morts++; }

    // === Dommages ===
    public long getDommagesInfliges() { return dommagesInfliges; }
    public void setDommagesInfliges(long dommagesInfliges) { this.dommagesInfliges = dommagesInfliges; }
    public void addDommagesInfliges(long dommages) { this.dommagesInfliges += dommages; }

    public long getDommagesRecus() { return dommagesRecus; }
    public void setDommagesRecus(long dommagesRecus) { this.dommagesRecus = dommagesRecus; }
    public void addDommagesRecus(long dommages) { this.dommagesRecus += dommages; }

    // === Advancements ===
    public long getAdvancementsAccomplis() { return advancementsAccomplis; }
    public void setAdvancementsAccomplis(long advancementsAccomplis) { this.advancementsAccomplis = advancementsAccomplis; }
    public void incrementAdvancementsAccomplis() { this.advancementsAccomplis++; }

    // === Blocs ===
    public long getBlocsCasses() { return blocsCasses; }
    public void setBlocsCasses(long blocsCasses) { this.blocsCasses = blocsCasses; }
    public void incrementBlocsCasses() { this.blocsCasses++; }

    public long getBlocsPlaces() { return blocsPlaces; }
    public void setBlocsPlaces(long blocsPlaces) { this.blocsPlaces = blocsPlaces; }
    public void incrementBlocsPlaces() { this.blocsPlaces++; }

    // === Distance ===
    public long getDistanceParcourue() { return distanceParcourue; }
    public void setDistanceParcourue(long distanceParcourue) { this.distanceParcourue = distanceParcourue; }
    public void addDistanceParcourue(long distance) { this.distanceParcourue += distance; }

    // === Temps joué ===
    public long getTempsJoue() { return tempsJoue; }
    public void setTempsJoue(long tempsJoue) { this.tempsJoue = tempsJoue; }
    public void addTempsJoue(long ticks) { this.tempsJoue += ticks; }

    /**
     * Retourne le temps joué formaté (JJ:HH:MM:SS)
     */
    public String getTempsJoueFormate() {
        long secondes = tempsJoue / 20;
        long minutes = secondes / 60;
        long heures = minutes / 60;
        long jours = heures / 24;

        secondes %= 60;
        minutes %= 60;
        heures %= 24;

        if (jours > 0) {
            return jours + "j " + heures + "h " + minutes + "m " + secondes + "s";
        } else if (heures > 0) {
            return heures + "h " + minutes + "m " + secondes + "s";
        } else if (minutes > 0) {
            return minutes + "m " + secondes + "s";
        } else {
            return secondes + "s";
        }
    }

    // === Items ===
    public long getItemsCraftes() { return itemsCraftes; }
    public void setItemsCraftes(long itemsCraftes) { this.itemsCraftes = itemsCraftes; }
    public void incrementItemsCraftes() { this.itemsCraftes++; }

    public long getItemsRamasees() { return itemsRamasees; }
    public void setItemsRamasees(long itemsRamasees) { this.itemsRamasees = itemsRamasees; }
    public void addItemsRamasees(long items) { this.itemsRamasees += items; }

    // === Connexions ===
    public long getPremiereConnexion() { return premiereConnexion; }
    public void setPremiereConnexion(long premiereConnexion) { this.premiereConnexion = premiereConnexion; }

    public long getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(long derniereConnexion) { this.derniereConnexion = derniereConnexion; }

    /**
     * Calcule le ratio K/D (Kills/Deaths)
     */
    public double getKDRatio() {
        if (morts == 0) return joueursTues;
        return Math.round((double) joueursTues / morts * 100.0) / 100.0;
    }
}
