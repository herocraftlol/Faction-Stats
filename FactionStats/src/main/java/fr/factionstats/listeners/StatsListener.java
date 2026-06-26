package fr.factionstats.listeners;

import fr.factionstats.FactionStats;
import fr.factionstats.managers.StatsManager;
import fr.factionstats.models.PlayerStats;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;

/**
 * Écouteur principal — capte tous les événements de jeu pour mettre à jour les stats
 */
public class StatsListener implements Listener {

    private final FactionStats plugin;
    private final StatsManager statsManager;

    public StatsListener(FactionStats plugin) {
        this.plugin = plugin;
        this.statsManager = plugin.getStatsManager();
    }

    // =============================================
    //  CONNEXION / DÉCONNEXION
    // =============================================

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());

        // Mise à jour du nom (peut avoir changé)
        stats.setPlayerName(player.getName());
        stats.setDerniereConnexion(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
        stats.setDerniereConnexion(System.currentTimeMillis());
    }

    // =============================================
    //  MORTS ET KILLS
    // =============================================

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null) return;

        PlayerStats stats = statsManager.getOrCreateStats(killer.getUniqueId(), killer.getName());

        // Le joueur a tué un autre joueur
        if (entity instanceof Player victimPlayer) {
            stats.incrementJoueursTues();

            // La victime reçoit une mort
            PlayerStats victimStats = statsManager.getOrCreateStats(
                    victimPlayer.getUniqueId(), victimPlayer.getName()
            );
            victimStats.incrementMorts();
            return;
        }

        // Le joueur a tué un mob hostile
        if (isHostileMob(entity)) {
            stats.incrementMobsHostilesTues();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Si le tueur n'est pas un joueur (mort par mob, chute, etc.)
        if (player.getKiller() == null) {
            PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
            stats.incrementMorts();
        }
        // Si le tueur est un joueur, la mort est déjà comptée dans onEntityDeath
    }

    /**
     * Vérifie si une entité est un mob hostile
     */
    private boolean isHostileMob(LivingEntity entity) {
        return entity instanceof Monster
                || entity instanceof Ghast
                || entity instanceof Slime
                || entity instanceof Phantom
                || entity instanceof Shulker
                || entity instanceof ElderGuardian
                || entity instanceof Warden
                || entity instanceof EnderDragon
                || entity instanceof WitherSkeleton
                || entity instanceof Wither
                || entity instanceof PiglinBrute
                || entity instanceof Hoglin
                || entity instanceof Zoglin;
    }

    // =============================================
    //  DOMMAGES
    // =============================================

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Dommages infligés par un joueur
        if (event.getDamager() instanceof Player attacker) {
            PlayerStats stats = statsManager.getOrCreateStats(attacker.getUniqueId(), attacker.getName());
            stats.addDommagesInfliges((long) event.getFinalDamage());
        }

        // Dommages reçus par un joueur
        if (event.getEntity() instanceof Player victim) {
            PlayerStats stats = statsManager.getOrCreateStats(victim.getUniqueId(), victim.getName());
            stats.addDommagesRecus((long) event.getFinalDamage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) return; // Déjà géré ci-dessus

        if (event.getEntity() instanceof Player victim) {
            PlayerStats stats = statsManager.getOrCreateStats(victim.getUniqueId(), victim.getName());
            stats.addDommagesRecus((long) event.getFinalDamage());
        }
    }

    // =============================================
    //  ADVANCEMENTS (PROGRÈS)
    // =============================================

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        // Ignorer les advancements de recettes (trop nombreux, peu significatifs)
        String key = event.getAdvancement().getKey().getKey();
        if (key.startsWith("recipes/")) return;

        Player player = event.getPlayer();
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
        stats.incrementAdvancementsAccomplis();
    }

    // =============================================
    //  BLOCS
    // =============================================

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
        stats.incrementBlocsCasses();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
        stats.incrementBlocsPlaces();
    }

    // =============================================
    //  DÉPLACEMENTS
    // =============================================

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // On ne compte que si le joueur s'est déplacé d'au moins 1 bloc entier
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        Player player = event.getPlayer();
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());

        double distance = event.getFrom().distance(event.getTo());
        stats.addDistanceParcourue((long) distance);
    }

    // =============================================
    //  ITEMS
    // =============================================

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
        stats.incrementItemsCraftes();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
        stats.addItemsRamasees(event.getItem().getItemStack().getAmount());
    }

    // =============================================
    //  TEMPS DE JEU
    // =============================================

    /**
     * Mise à jour du temps de jeu — appelée périodiquement par la tâche du plugin
     * (pas d'événement direct disponible pour ça en Bukkit)
     */
    public void updatePlaytime(Player player, long ticks) {
        PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
        stats.addTempsJoue(ticks);
    }
}
