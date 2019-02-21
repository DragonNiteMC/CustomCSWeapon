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

    public static ConfigManager getInstance() {
        return configManager;
    }

    public static List<String> getMolotovs() {
        return molotovs;
    }

    public ConfigManager(Plugin plugin){
        configFile = new File(plugin.getDataFolder(),"config.yml");
        if (!configFile.exists()) plugin.saveResource("config.yml",true);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private ConfigManager(){

    }

    public void loadConfig(){
        molotovs = config.getStringList("molotov");
    }

    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfig();
    }
}
