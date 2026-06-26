package fr.factionstats;

import fr.factionstats.commands.ClassementCommand;
import fr.factionstats.commands.StatsCommand;
import fr.factionstats.listeners.StatsListener;
import fr.factionstats.managers.MessageManager;
import fr.factionstats.managers.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Classe principale du plugin FactionStats
 */
public class FactionStats extends JavaPlugin {

    private StatsManager statsManager;
    private StatsListener statsListener;
    private BukkitTask autoSaveTask;
    private BukkitTask playtimeTask;

    @Override
    public void onEnable() {
        // Sauvegarde de la config par défaut
        saveDefaultConfig();

        // Initialisation des managers
        statsManager = new StatsManager(this);

        // Enregistrement des listeners
        statsListener = new StatsListener(this);
        Bukkit.getPluginManager().registerEvents(statsListener, this);

        // Enregistrement des commandes
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("stats").setTabCompleter(new StatsCommand(this));

        getCommand("classement").setExecutor(new ClassementCommand(this));
        getCommand("classement").setTabCompleter(new ClassementCommand(this));

        // Tâche de sauvegarde automatique
        int saveInterval = getConfig().getInt("auto-save-interval", 300);
        if (saveInterval > 0) {
            long saveTicks = saveInterval * 20L;
            autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                statsManager.saveAllStats();
                getLogger().info("Sauvegarde automatique des statistiques effectuée.");
            }, saveTicks, saveTicks);
        }

        // Tâche de suivi du temps de jeu (toutes les 20 ticks = 1 seconde)
        playtimeTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                statsListener.updatePlaytime(player, 20L);
            }
        }, 20L, 20L);

        getLogger().info("╔══════════════════════════════════╗");
        getLogger().info("║   FactionStats v" + getDescription().getVersion() + " activé !     ║");
        getLogger().info("║   Statistiques Faction Survie    ║");
        getLogger().info("╚══════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        // Annulation des tâches planifiées
        if (autoSaveTask != null) autoSaveTask.cancel();
        if (playtimeTask != null) playtimeTask.cancel();

        // Sauvegarde finale de toutes les stats
        if (statsManager != null) {
            statsManager.saveAllStats();
            getLogger().info("Statistiques sauvegardées avec succès.");
        }

        getLogger().info("FactionStats désactivé.");
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public StatsListener getStatsListener() {
        return statsListener;
    }
}
