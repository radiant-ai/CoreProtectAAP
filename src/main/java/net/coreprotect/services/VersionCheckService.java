package net.coreprotect.services;

import org.bukkit.Bukkit;

import net.coreprotect.bukkit.BukkitAdapter;
import net.coreprotect.config.ConfigHandler;
import net.coreprotect.language.Phrase;
import net.coreprotect.utility.Chat;
import net.coreprotect.utility.Color;
import net.coreprotect.utility.VersionUtils;
import net.coreprotect.utility.ErrorReporter;

/**
 * Service responsible for checking compatibility of Minecraft, Java versions,
 * and plugin branch validation.
 */
public class VersionCheckService {

    private VersionCheckService() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Performs all necessary version checks during plugin startup
     *
     * @return true if all version checks pass, false otherwise
     */
    public static boolean performVersionChecks() {
        try {
            // Check Minecraft version compatibility
            String numericVersion = Bukkit.getServer().getBukkitVersion().split("-", 2)[0];
            String[] bukkitVersion = numericVersion.split("\\.");
            if (bukkitVersion.length < 2 || !bukkitVersion[0].matches("\\d+") || !bukkitVersion[1].matches("\\d+")) {
                Chat.console(Phrase.build(Phrase.VERSION_INCOMPATIBLE, "Minecraft", numericVersion));
                return false;
            }

            String currentVersion = bukkitVersion[0] + "." + bukkitVersion[1] + (bukkitVersion.length > 2 && bukkitVersion[2].matches("\\d+") ? "." + bukkitVersion[2] : "");

            if (VersionUtils.newVersion(currentVersion, ConfigHandler.MINECRAFT_VERSION)) {
                Chat.console(Phrase.build(Phrase.VERSION_REQUIRED, "Minecraft", ConfigHandler.MINECRAFT_VERSION));
                return false;
            }

            // Fork: upper Minecraft version cap removed so newer MC releases never block startup.

            // Check Java version compatibility
            String[] javaVersion = (System.getProperty("java.version").replaceAll("[^0-9.]", "") + ".0").split("\\.");
            if (VersionUtils.newVersion(javaVersion[0] + "." + javaVersion[1], ConfigHandler.JAVA_VERSION)) {
                Chat.console(Phrase.build(Phrase.VERSION_REQUIRED, "Java", ConfigHandler.JAVA_VERSION));
                return false;
            }

            // Store Minecraft server version for later use
            int major = Integer.parseInt(bukkitVersion[0]);
            int minor = Integer.parseInt(bukkitVersion[1]);
            ConfigHandler.SERVER_VERSION = BukkitAdapter.getAdapterVersion(major, minor);
        }
        catch (Exception e) {
            ErrorReporter.report(e);
            return false;
        }

        return true;
    }
}
