package me.monoto.statistics.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    Plugin plugin;
    private final int resourceID;

    public UpdateChecker(Plugin plugin, int resourceID) {
        this.plugin = plugin;
        this.resourceID = resourceID;
    }

    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String url = "https://api.spigotmc.org/legacy/update.php?resource=" + resourceID;
                Scanner scanner = new Scanner(new URL(url).openStream());
                consumer.accept(scanner.next());
                scanner.close();
            } catch (IOException e) {
                this.plugin.getLogger().warning("Update checker is broken, can't find an update! Exception: ");
                e.printStackTrace();
            }
        });
    }
}
