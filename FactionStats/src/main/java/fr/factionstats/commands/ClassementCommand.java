package fr.factionstats.commands;

import fr.factionstats.FactionStats;
import fr.factionstats.managers.MessageManager;
import fr.factionstats.managers.StatsManager;
import fr.factionstats.models.PlayerStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

/**
 * Commande /classement [categorie] — Affiche les classements Top 10
 *
 * Catégories disponibles :
 *   mobs        → Mobs hostiles tués
 *   pvp         → Joueurs tués
 *   advancements→ Progrès accomplis
 *   morts       → Nombre de morts
 *   blocs       → Blocs cassés
 *   temps       → Temps de jeu
 *   dommages    → Dommages infligés
 *   kd          → Ratio K/D
 */
public class ClassementCommand implements CommandExecutor, TabCompleter {

    private final FactionStats plugin;
    private final StatsManager statsManager;

    private static final List<String> CATEGORIES = Arrays.asList(
            "mobs", "pvp", "advancements", "morts", "blocs", "temps", "dommages", "kd"
    );

    public ClassementCommand(FactionStats plugin) {
        this.plugin = plugin;
        this.statsManager = plugin.getStatsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("factionstats.classement")) {
            MessageManager.sendError(sender, "Vous n'avez pas la permission.");
            return true;
        }

        // Sans argument → afficher le menu des catégories
        if (args.length == 0) {
            afficherMenu(sender);
            return true;
        }

        String categorie = args[0].toLowerCase();

        switch (categorie) {
            case "mobs" -> afficherClassement(sender,
                    "Mobs Hostiles Tués", "⚔", "§c",
                    statsManager.getClassementMobsHostiles(10),
                    stats -> MessageManager.formatNumber(stats.getMobsHostilesTues()) + " mobs");

            case "pvp", "joueurs" -> afficherClassement(sender,
                    "Joueurs Tués (PvP)", "☠", "§c",
                    statsManager.getClassementJoueursTues(10),
                    stats -> MessageManager.formatNumber(stats.getJoueursTues()) + " kills");

            case "advancements", "progres" -> afficherClassement(sender,
                    "Progrès Accomplis", "★", "§a",
                    statsManager.getClassementAdvancements(10),
                    stats -> MessageManager.formatNumber(stats.getAdvancementsAccomplis()) + " progrès");

            case "morts" -> afficherClassement(sender,
                    "Nombre de Morts", "💀", "§7",
                    statsManager.getClassementMorts(10),
                    stats -> MessageManager.formatNumber(stats.getMorts()) + " morts");

            case "blocs" -> afficherClassement(sender,
                    "Blocs Cassés", "⛏", "§e",
                    statsManager.getClassementBlocsCasses(10),
                    stats -> MessageManager.formatNumber(stats.getBlocsCasses()) + " blocs");

            case "temps" -> afficherClassement(sender,
                    "Temps de Jeu", "⏱", "§b",
                    statsManager.getClassementTempsJoue(10),
                    stats -> stats.getTempsJoueFormate());

            case "dommages" -> afficherClassement(sender,
                    "Dommages Infligés", "❤", "§c",
                    statsManager.getClassementDommages(10),
                    stats -> MessageManager.formatNumber(stats.getDommagesInfliges()) + " ❤");

            case "kd" -> afficherClassement(sender,
                    "Ratio K/D", "⚖", "§e",
                    statsManager.getClassementKD(10),
                    stats -> "K/D: §e" + stats.getKDRatio());

            default -> {
                MessageManager.sendError(sender, "Catégorie inconnue : §e" + categorie);
                afficherMenu(sender);
            }
        }

        return true;
    }

    /**
     * Affiche le menu des catégories disponibles
     */
    private void afficherMenu(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));
        sender.sendMessage(MessageManager.colorize(
                "  §6§l⚔ §r§6CLASSEMENTS §8— §7Faction Survie"
        ));
        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));
        sender.sendMessage(MessageManager.colorize("  §7Choisissez une catégorie :"));
        sender.sendMessage("");
        sender.sendMessage(MessageManager.colorize("  §c» §f/classement mobs        §8─ §7Mobs hostiles tués"));
        sender.sendMessage(MessageManager.colorize("  §c» §f/classement pvp         §8─ §7Joueurs tués (PvP)"));
        sender.sendMessage(MessageManager.colorize("  §c» §f/classement kd          §8─ §7Ratio Kills/Deaths"));
        sender.sendMessage(MessageManager.colorize("  §a» §f/classement advancements§8─ §7Progrès accomplis"));
        sender.sendMessage(MessageManager.colorize("  §e» §f/classement blocs       §8─ §7Blocs cassés"));
        sender.sendMessage(MessageManager.colorize("  §7» §f/classement morts       §8─ §7Nombre de morts"));
        sender.sendMessage(MessageManager.colorize("  §b» §f/classement temps       §8─ §7Temps de jeu"));
        sender.sendMessage(MessageManager.colorize("  §c» §f/classement dommages    §8─ §7Dommages infligés"));
        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));
        sender.sendMessage("");
    }

    /**
     * Interface fonctionnelle pour extraire la valeur à afficher par stats
     */
    @FunctionalInterface
    interface StatExtractor {
        String extract(PlayerStats stats);
    }

    /**
     * Affiche un classement générique Top 10
     */
    private void afficherClassement(CommandSender sender, String titre, String icone, String couleur,
                                     List<PlayerStats> classement, StatExtractor extractor) {
        sender.sendMessage("");
        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));
        sender.sendMessage(MessageManager.colorize(
                "  " + couleur + "§l" + icone + " §r" + couleur + "CLASSEMENT — " + titre.toUpperCase()
        ));
        sender.sendMessage(MessageManager.colorize(
                "  §8Top " + Math.min(10, classement.size()) + " joueurs depuis le début du serveur"
        ));
        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));

        if (classement.isEmpty()) {
            sender.sendMessage(MessageManager.colorize("  §7Aucune donnée disponible pour le moment."));
        } else {
            for (int i = 0; i < classement.size(); i++) {
                PlayerStats stats = classement.get(i);
                int rang = i + 1;

                String ligne = "  " + MessageManager.getMedaille(rang) +
                        "§f" + stats.getPlayerName() +
                        " §8— " + couleur + extractor.extract(stats);

                sender.sendMessage(MessageManager.colorize(ligne));
            }
        }

        sender.sendMessage(MessageManager.colorize(MessageManager.separator()));
        sender.sendMessage(MessageManager.colorize(
                "  §8Utilisez §7/stats <joueur> §8pour voir les détails."
        ));
        sender.sendMessage("");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return CATEGORIES.stream()
                    .filter(c -> c.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
