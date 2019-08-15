package com.ericlam.mc.csweapon;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
    private FileConfiguration config;
    private File configFile;
    static int flash_radius, molo_duration;
    private static ConfigManager configManager;
    static String helmetSound, noHelmetSound;
    static boolean customSound, noKnockBack, useDamagePercent;
    static double customKnockBack;
    private static List<String> molotovs = new ArrayList<>();
    private static List<String> scopes = new ArrayList<>();
    private static List<String> flashbangs = new ArrayList<>();
    private static HashMap<String, ItemStack> scopeSkin;
    private static List<String> flashBypass = new ArrayList<>();


    public static List<String> getFlashBypass() {
        return flashBypass;
    }

    public static HashMap<String, ItemStack> getScopeSkin() {
        return scopeSkin;
    }

    public static List<String> getFlashbangs() {
        return flashbangs;
    }

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
        molo_duration = config.getInt("molotov-duration");
        molotovs = config.getStringList("molotov");
        scopes = config.getStringList("scope");
        flashbangs = config.getStringList("flashbangs");
        flash_radius = config.getInt("flash-radius");
        flashBypass = config.getStringList("flash-bypass-blacklist");
        helmetSound = config.getString("headshot.helmet-sound");
        noHelmetSound = config.getString("headshot.no-helmet-sound");
        customSound = config.getBoolean("headshot.custom-sound");
        noKnockBack = config.getBoolean("knockback.disable");
        useDamagePercent = config.getBoolean("knockback.custom.damage-percent");
        customKnockBack = config.getDouble("knockback.custom.value");
        HashMap<String, ItemStack> scopeSkin = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("scope-skin");
        for (String weaponTitle : section.getKeys(false)) {
            String[] value = section.getString(weaponTitle).split(":");
            String material = value[0];
            ItemStack skinStack = new ItemStack(Material.valueOf(material));
            if (value.length > 1) {
                ItemMeta meta = skinStack.getItemMeta();
                ((Damageable) meta).setDamage(Integer.parseInt(value[1]));
                meta.setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                skinStack.setItemMeta(meta);
            }
            scopeSkin.put(weaponTitle, skinStack);
        }
        ConfigManager.scopeSkin = scopeSkin;
    }

    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfig();
    }
}
