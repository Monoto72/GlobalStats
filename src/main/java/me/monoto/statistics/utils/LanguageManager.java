package me.monoto.statistics.utils;

import me.monoto.statistics.Statistics;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LanguageManager {
    private final Statistics plugin;

    private YamlConfiguration langFile;

    public LanguageManager(Statistics plugin) {
        this.plugin = plugin;
        createLangFile();
    }

    public File getFile() {
        return new File(this.plugin.getDataFolder() + "/lang.yml");
    }

    public YamlConfiguration getFileConfig() {
        return langFile;
    }

    private void createLangFile() {
        if (!getFile().exists()) {
            this.plugin.saveResource("lang.yml", false);
        }
        createLangYaml();
    }

    private void createLangYaml() {
        this.langFile = YamlConfiguration.loadConfiguration(getFile());
        saveLanguageConfig();
    }

    private void saveLanguageConfig() {
        try {
            langFile.save(getFile());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
