package com.ericlam.mc.csweapon;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private FileConfiguration config;
    private File configFile;
    private static ConfigManager configManager;
    private static List<String> molotovs = new ArrayList<>();
    private static List<String> scopes = new ArrayList<>();

    public static ConfigManager getInstance() {
        return configManager;
    }

    public static List<String> getMolotovs() {
        return molotovs;
    }

    public ConfigManager(Plugin plugin){
        configManager = this;
        configFile = new File(plugin.getDataFolder(),"config.yml");
        if (!configFile.exists()) plugin.saveResource("config.yml",true);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static List<String> getScopes() {
        return scopes;
    }

    private ConfigManager(){

    }

    public void loadConfig(){
        molotovs = config.getStringList("molotov");
        scopes = config.getStringList("scope");
    }

    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfig();
    }
}
