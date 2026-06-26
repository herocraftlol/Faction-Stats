package fr.factionstats.managers;

import fr.factionstats.FactionStats;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Gestionnaire des messages du plugin
 */
public class MessageManager {

    private final FactionStats plugin;

    // Codes couleur pré-définis
    public static final String PREFIX;
    public static final String C_PRIMARY   = "§6";  // Or
    public static final String C_SECONDARY = "§e";  // Jaune
    public static final String C_ACCENT    = "§a";  // Vert
    public static final String C_ERROR     = "§c";  // Rouge
    public static final String C_INFO      = "§7";  // Gris
    public static final String C_WHITE     = "§f";  // Blanc
    public static final String C_DARK      = "§8";  // Gris foncé
    public static final String C_GOLD      = "§6";  // Or
    public static final String C_AQUA      = "§b";  // Cyan
    public static final String C_PINK      = "§d";  // Rose
    public static final String BOLD        = "§l";
    public static final String ITALIC      = "§o";
    public static final String RESET       = "§r";

    static {
        PREFIX = C_DARK + "[" + C_PRIMARY + BOLD + "FactionStats" + RESET + C_DARK + "] " + RESET;
    }

    public MessageManager(FactionStats plugin) {
        this.plugin = plugin;
    }

    /**
     * Envoie un message préfixé
     */
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(colorize(PREFIX + message));
    }

    /**
     * Envoie un message d'erreur
     */
    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(colorize(PREFIX + C_ERROR + message));
    }

    /**
     * Envoie un message de succès
     */
    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(colorize(PREFIX + C_ACCENT + message));
    }

    /**
     * Colorise un message (& -> §)
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Ligne de séparation stylée
     */
    public static String separator() {
        return C_DARK + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    }

    /**
     * Ligne de séparation courte
     */
    public static String separatorShort() {
        return C_DARK + "──────────────────────────────────────";
    }

    /**
     * Formate un grand nombre avec séparateurs (ex: 1 234 567)
     */
    public static String formatNumber(long number) {
        return String.format("%,d", number).replace(",", " ");
    }

    /**
     * Retourne la médaille pour un rang (1=Or, 2=Argent, 3=Bronze)
     */
    public static String getMedaille(int rang) {
        return switch (rang) {
            case 1 -> "§6§l✦ §r§6";
            case 2 -> "§7§l✦ §r§7";
            case 3 -> "§c§l✦ §r§c";
            default -> C_DARK + "#" + rang + " " + C_INFO;
        };
    }
}
