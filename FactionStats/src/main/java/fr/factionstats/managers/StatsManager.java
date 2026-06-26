package fr.factionstats.managers;

import fr.factionstats.FactionStats;
import fr.factionstats.models.PlayerStats;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Gestionnaire des statistiques joueurs — chargement, sauvegarde, accès
 */
public class StatsManager {

    private final FactionStats plugin;
    private final Map<UUID, PlayerStats> statsCache = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public StatsManager(FactionStats plugin) {
        this.plugin = plugin;
        setupDataFile();
        loadAllStats();
    }

    // =============================================
    //  INITIALISATION FICHIER
    // =============================================

    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Impossible de créer playerdata.yml", e);
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    // =============================================
    //  CHARGEMENT
    // =============================================

    private void loadAllStats() {
        if (!dataConfig.isConfigurationSection("players")) return;

        for (String uuidStr : dataConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                String path = "players." + uuidStr;

                String name = dataConfig.getString(path + ".name", "Inconnu");
                PlayerStats stats = new PlayerStats(uuid, name);

                stats.setMobsHostilesTues(dataConfig.getLong(path + ".mobs_hostiles_tues", 0));
                stats.setJoueursTues(dataConfig.getLong(path + ".joueurs_tues", 0));
                stats.setMorts(dataConfig.getLong(path + ".morts", 0));
                stats.setDommagesInfliges(dataConfig.getLong(path + ".dommages_infliges", 0));
                stats.setDommagesRecus(dataConfig.getLong(path + ".dommages_recus", 0));
                stats.setAdvancementsAccomplis(dataConfig.getLong(path + ".advancements_accomplis", 0));
                stats.setBlocsCasses(dataConfig.getLong(path + ".blocs_casses", 0));
                stats.setBlocsPlaces(dataConfig.getLong(path + ".blocs_places", 0));
                stats.setDistanceParcourue(dataConfig.getLong(path + ".distance_parcourue", 0));
                stats.setTempsJoue(dataConfig.getLong(path + ".temps_joue", 0));
                stats.setItemsCraftes(dataConfig.getLong(path + ".items_craftes", 0));
                stats.setItemsRamasees(dataConfig.getLong(path + ".items_ramasees", 0));
                stats.setPremiereConnexion(dataConfig.getLong(path + ".premiere_connexion", System.currentTimeMillis()));
                stats.setDerniereConnexion(dataConfig.getLong(path + ".derniere_connexion", System.currentTimeMillis()));

                statsCache.put(uuid, stats);

            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("UUID invalide dans playerdata.yml : " + uuidStr);
            }
        }

        plugin.getLogger().info("Stats chargées pour " + statsCache.size() + " joueur(s).");
    }

    // =============================================
    //  SAUVEGARDE
    // =============================================

    public void saveAllStats() {
        for (PlayerStats stats : statsCache.values()) {
            savePlayerStats(stats);
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde de playerdata.yml", e);
        }
    }

    private void savePlayerStats(PlayerStats stats) {
        String path = "players." + stats.getUuid().toString();

        dataConfig.set(path + ".name", stats.getPlayerName());
        dataConfig.set(path + ".mobs_hostiles_tues", stats.getMobsHostilesTues());
        dataConfig.set(path + ".joueurs_tues", stats.getJoueursTues());
        dataConfig.set(path + ".morts", stats.getMorts());
        dataConfig.set(path + ".dommages_infliges", stats.getDommagesInfliges());
        dataConfig.set(path + ".dommages_recus", stats.getDommagesRecus());
        dataConfig.set(path + ".advancements_accomplis", stats.getAdvancementsAccomplis());
        dataConfig.set(path + ".blocs_casses", stats.getBlocsCasses());
        dataConfig.set(path + ".blocs_places", stats.getBlocsPlaces());
        dataConfig.set(path + ".distance_parcourue", stats.getDistanceParcourue());
        dataConfig.set(path + ".temps_joue", stats.getTempsJoue());
        dataConfig.set(path + ".items_craftes", stats.getItemsCraftes());
        dataConfig.set(path + ".items_ramasees", stats.getItemsRamasees());
        dataConfig.set(path + ".premiere_connexion", stats.getPremiereConnexion());
        dataConfig.set(path + ".derniere_connexion", stats.getDerniereConnexion());
    }

    // =============================================
    //  ACCÈS AUX STATS
    // =============================================

    /**
     * Récupère ou crée les stats d'un joueur
     */
    public PlayerStats getOrCreateStats(UUID uuid, String playerName) {
        return statsCache.computeIfAbsent(uuid, k -> new PlayerStats(k, playerName));
    }

    /**
     * Récupère les stats d'un joueur (null si inexistant)
     */
    public PlayerStats getStats(UUID uuid) {
        return statsCache.get(uuid);
    }

    /**
     * Recherche un joueur par son nom (insensible à la casse)
     */
    public PlayerStats getStatsByName(String name) {
        for (PlayerStats stats : statsCache.values()) {
            if (stats.getPlayerName().equalsIgnoreCase(name)) {
                return stats;
            }
        }
        return null;
    }

    /**
     * Retourne tous les joueurs enregistrés
     */
    public Collection<PlayerStats> getAllStats() {
        return Collections.unmodifiableCollection(statsCache.values());
    }

    // =============================================
    //  CLASSEMENTS
    // =============================================

    public List<PlayerStats> getClassementMobsHostiles(int limit) {
        return getTopPlayers(limit, Comparator.comparingLong(PlayerStats::getMobsHostilesTues).reversed());
    }

    public List<PlayerStats> getClassementJoueursTues(int limit) {
        return getTopPlayers(limit, Comparator.comparingLong(PlayerStats::getJoueursTues).reversed());
    }

    public List<PlayerStats> getClassementAdvancements(int limit) {
        return getTopPlayers(limit, Comparator.comparingLong(PlayerStats::getAdvancementsAccomplis).reversed());
    }

    public List<PlayerStats> getClassementMorts(int limit) {
        return getTopPlayers(limit, Comparator.comparingLong(PlayerStats::getMorts).reversed());
    }

    public List<PlayerStats> getClassementBlocsCasses(int limit) {
        return getTopPlayers(limit, Comparator.comparingLong(PlayerStats::getBlocsCasses).reversed());
    }

    public List<PlayerStats> getClassementTempsJoue(int limit) {
        return getTopPlayers(limit, Comparator.comparingLong(PlayerStats::getTempsJoue).reversed());
    }

    public List<PlayerStats> getClassementDommages(int limit) {
        return getTopPlayers(limit, Comparator.comparingLong(PlayerStats::getDommagesInfliges).reversed());
    }

    public List<PlayerStats> getClassementKD(int limit) {
        return getTopPlayers(limit, Comparator.comparingDouble(PlayerStats::getKDRatio).reversed());
    }

    private List<PlayerStats> getTopPlayers(int limit, Comparator<PlayerStats> comparator) {
        List<PlayerStats> list = new ArrayList<>(statsCache.values());
        list.sort(comparator);
        return list.subList(0, Math.min(limit, list.size()));
    }

    /**
     * Retourne le rang d'un joueur dans une catégorie
     */
    public int getRangJoueur(UUID uuid, String categorie) {
        List<PlayerStats> classement = switch (categorie.toLowerCase()) {
            case "mobs" -> getClassementMobsHostiles(Integer.MAX_VALUE);
            case "joueurs", "pvp" -> getClassementJoueursTues(Integer.MAX_VALUE);
            case "advancements", "progres" -> getClassementAdvancements(Integer.MAX_VALUE);
            case "morts" -> getClassementMorts(Integer.MAX_VALUE);
            case "blocs" -> getClassementBlocsCasses(Integer.MAX_VALUE);
            case "temps" -> getClassementTempsJoue(Integer.MAX_VALUE);
            case "dommages" -> getClassementDommages(Integer.MAX_VALUE);
            case "kd" -> getClassementKD(Integer.MAX_VALUE);
            default -> getClassementMobsHostiles(Integer.MAX_VALUE);
        };

        for (int i = 0; i < classement.size(); i++) {
            if (classement.get(i).getUuid().equals(uuid)) return i + 1;
        }
        return -1;
    }
}
