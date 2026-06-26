package fr.factionstats.commands;

import fr.factionstats.FactionStats;
import fr.factionstats.managers.MessageManager;
import fr.factionstats.managers.StatsManager;
import fr.factionstats.models.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Commande /stats [joueur] — Affiche les statistiques d'un joueur
 */
public class StatsCommand implements CommandExecutor, TabCompleter {

    private final FactionStats plugin;
    private final StatsManager statsManager;

    public StatsCommand(FactionStats plugin) {
        this.plugin = plugin;
        this.statsManager = plugin.getStatsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // === Cas 1 : /stats (ses propres stats) ===
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                MessageManager.sendError(sender, "La console doit spécifier un joueur : /stats <joueur>");
                return true;
            }
            if (!player.hasPermission("factionstats.stats")) {
                MessageManager.sendError(sender, "Vous n'avez pas la permission.");
                return true;
            }
            PlayerStats stats = statsManager.getOrCreateStats(player.getUniqueId(), player.getName());
            afficherStats(sender, stats);
            return true;
        }

        // === Cas 2 : /stats <joueur> (stats d'un autre joueur) ===
        if (!sender.hasPermission("factionstats.stats.other") && !sender.hasPermission("factionstats.stats")) {
            MessageManager.sendError(sender, "Vous n'avez pas la permission.");
            return true;
        }

        String nomCible = args[0];

        // Chercher d'abord dans le cache
        PlayerStats stats = statsManager.getStatsByName(nomCible);

        // Chercher en joueur en ligne
        if (stats == null) {
            Player cibleEnLigne = Bukkit.getPlayer(nomCible);
            if (cibleEnLigne != null) {
                stats = statsManager.getOrCreateStats(cibleEnLigne.getUniqueId(), cibleEnLigne.getName());
            }
        }

        // Chercher dans les OfflinePlayers
        if (stats == null) {
            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(nomCible);
            if (offlinePlayer.hasPlayedBefore()) {
                stats = statsManager.getStats(offlinePlayer.getUniqueId());
                if (stats == null) {
                    stats = statsManager.getOrCreateStats(offlinePlayer.getUniqueId(),
                            offlinePlayer.getName() != null ? offlinePlayer.getName() : nomCible);
                }
            }
        }

        if (stats == null) {
            MessageManager.sendError(sender, "Joueur introuvable ou jamais connecté : §e" + nomCible);
            return true;
        }

        afficherStats(sender, stats);
        return true;
    }

    private void afficherStats(CommandSender sender, PlayerStats stats) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Calcul du rang global (basé sur mobs tués)
        int rangMobs = statsManager.getRangJoueur(stats.getUuid(), "mobs");
        int rangPvP = statsManager.getRangJoueur(stats.getUuid(), "pvp");
        int rangAdv = statsManager.getRangJoueur(stats.getUuid(), "advancements");

        sender.sendMessage("");
        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));
        sender.sendMessage(MessageManager.colorize(
                "  §6§l⚔ §r§eStatistiques de §6§l" + stats.getPlayerName() + "§r§8  ✦ Faction Survie"
        ));
        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));

        // Informations générales
        sender.sendMessage(MessageManager.colorize("  §8» §7Première connexion : §f" +
                sdf.format(new Date(stats.getPremiereConnexion()))));
        sender.sendMessage(MessageManager.colorize("  §8» §7Dernière connexion  : §f" +
                sdf.format(new Date(stats.getDerniereConnexion()))));
        sender.sendMessage(MessageManager.colorize("  §8» §7Temps de jeu total  : §b" +
                stats.getTempsJoueFormate()));
        sender.sendMessage("");

        // Statistiques de combat
        sender.sendMessage(MessageManager.colorize(
                "  §c§l⚔ §r§cCOMBAT §8" + (rangPvP > 0 ? "§8(classement PvP: §6#" + rangPvP + "§8)" : "")
        ));
        sender.sendMessage(MessageManager.colorize(MessageManager.separatorShort()));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Mobs hostiles tués    : §c" + MessageManager.formatNumber(stats.getMobsHostilesTues()) +
                        "  §8(#" + statsManager.getRangJoueur(stats.getUuid(), "mobs") + ")"
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Joueurs tués (PvP)    : §c" + MessageManager.formatNumber(stats.getJoueursTues()) +
                        "  §8(#" + rangPvP + ")"
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Morts                 : §c" + MessageManager.formatNumber(stats.getMorts())
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Ratio K/D             : §e" + stats.getKDRatio()
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Dommages infligés      : §c" + MessageManager.formatNumber(stats.getDommagesInfliges()) + " ❤"
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Dommages reçus         : §c" + MessageManager.formatNumber(stats.getDommagesRecus()) + " ❤"
        ));
        sender.sendMessage("");

        // Progression
        sender.sendMessage(MessageManager.colorize(
                "  §a§l★ §r§aAVANCEMENTS §8" + (rangAdv > 0 ? "§8(classement: §6#" + rangAdv + "§8)" : "")
        ));
        sender.sendMessage(MessageManager.colorize(MessageManager.separatorShort()));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Progrès accomplis     : §a" + MessageManager.formatNumber(stats.getAdvancementsAccomplis()) +
                        "  §8(#" + rangAdv + ")"
        ));
        sender.sendMessage("");

        // Survie / Construction
        sender.sendMessage(MessageManager.colorize("  §e§l⛏ §r§eSURVIE & CONSTRUCTION"));
        sender.sendMessage(MessageManager.colorize(MessageManager.separatorShort()));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Blocs cassés          : §e" + MessageManager.formatNumber(stats.getBlocsCasses())
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Blocs placés          : §e" + MessageManager.formatNumber(stats.getBlocsPlaces())
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Distance parcourue    : §e" + MessageManager.formatNumber(stats.getDistanceParcourue()) + " blocs"
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Items craftés         : §e" + MessageManager.formatNumber(stats.getItemsCraftes())
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8› §7Items ramassés        : §e" + MessageManager.formatNumber(stats.getItemsRamasees())
        ));

        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));
        sender.sendMessage("");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            // Joueurs en ligne
            List<String> suggestions = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
            // Joueurs connus (hors ligne)
            for (PlayerStats stats : plugin.getStatsManager().getAllStats()) {
                if (stats.getPlayerName().toLowerCase().startsWith(input)
                        && !suggestions.contains(stats.getPlayerName())) {
                    suggestions.add(stats.getPlayerName());
                }
            }
            return suggestions;
        }
        return Collections.emptyList();
    }
}
