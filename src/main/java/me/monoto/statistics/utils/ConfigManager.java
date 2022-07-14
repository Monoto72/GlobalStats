package me.monoto.statistics.utils;

import me.monoto.statistics.Statistics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    Statistics plugin;
    private FileConfiguration config;

    public ConfigManager(Statistics main) {
        this.plugin = main;
        createConfig();
    }

    private File getConfigFile() {
        return new File(plugin.getDataFolder(), "config.yml");
    }

    private void createConfig() {
        config = YamlConfiguration.loadConfiguration(getConfigFile());
        generateConfig();
        saveConfig();
    }

    private void generateConfig() {
        config.options().copyDefaults(true);
        //config.addDefault("Statistic", "stat");

        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(getConfigFile());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
