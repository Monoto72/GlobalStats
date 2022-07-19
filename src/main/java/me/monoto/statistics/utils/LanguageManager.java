package me.monoto.statistics.utils;

import me.monoto.statistics.Statistics;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageManager {
    private final String language;
    private final Statistics plugin;

    private YamlConfiguration langFile;

    private final ArrayList<String> languages = new ArrayList<>(List.of("en_GB", "en_FR", "en_US"));

    public LanguageManager(String language, Statistics plugin) {
        this.language = language;
        this.plugin = plugin;

        createLangFolder();
    }

    public File getFile() {
        return new File(this.plugin.getDataFolder() + "/lang/" + this.language + ".yml");
    }

    public YamlConfiguration getFileConfig() {
        return langFile;
    }

    private void createLangFolder() {
        File langFolder = new File(this.plugin.getDataFolder() + "/lang");

        if (!langFolder.exists()) {
            if (!langFolder.mkdir()) {
                this.plugin.getLogger().warning("Error creating Lang dir");
                return;
            }
        }

        File[] langContents = langFolder.listFiles();

        languages.forEach(lang -> {
            if (langContents != null) {
                if (!Arrays.asList(langContents).contains(new File(this.plugin.getDataFolder() + "/lang/" + lang + ".yml"))) {
                    this.plugin.saveResource("lang/" + lang + ".yml", false);
                }
            }
        });
        createLangFile();
    }

    private void createLangFile() {
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
