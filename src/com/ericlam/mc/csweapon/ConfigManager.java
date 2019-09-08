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
import java.util.*;

class ConfigManager {
    private FileConfiguration config;
    private File configFile;
    static int flash_radius, molo_duration;
    private static ConfigManager configManager;
    static String helmetSound, noHelmetSound;
    static boolean customSound, noKnockBack, useDamagePercent;
    static double customKnockBack;
    private static List<String> molotovs = new ArrayList<>();
    private static Set<String> scopes = new HashSet<>();
    private static List<String> flashbangs = new ArrayList<>();
    private static HashMap<String, ItemStack> scopeSkin = new HashMap<>();
    private static List<String> flashBypass = new ArrayList<>();
    private static Map<String, Double> shotguns = new HashMap<>();


    ConfigManager(Plugin plugin) {
        configManager = this;
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) plugin.saveResource("config.yml", true);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    static List<String> getFlashBypass() {
        return flashBypass;
    }

    static HashMap<String, ItemStack> getScopeSkin() {
        return scopeSkin;
    }

    static List<String> getFlashbangs() {
        return flashbangs;
    }

    static ConfigManager getInstance() {
        return configManager;
    }

    static List<String> getMolotovs() {
        return molotovs;
    }

    static Set<String> getScopes() {
        return scopes;
    }

    static Map<String, Double> getShotguns() {
        return shotguns;
    }

    private ConfigManager(){

    }

    void loadConfig() {
        molo_duration = config.getInt("molotov-duration");
        molotovs = config.getStringList("molotov");
        flashbangs = config.getStringList("flashbangs");
        flash_radius = config.getInt("flash-radius");
        flashBypass = config.getStringList("flash-bypass-blacklist");
        Optional.ofNullable(config.getConfigurationSection("shotguns")).ifPresent(sec -> {
            sec.getValues(false).forEach((k, v) -> {
                double damage;
                if (v instanceof Integer) {
                    damage = (int) v;
                } else if (v instanceof Double) {
                    damage = (double) v;
                } else if (v instanceof Float) {
                    damage = (float) v;
                } else {
                    return;
                }
                shotguns.put(k, damage);
            });
        });
        helmetSound = config.getString("headshot.helmet-sound");
        noHelmetSound = config.getString("headshot.no-helmet-sound");
        customSound = config.getBoolean("headshot.custom-sound");
        noKnockBack = config.getBoolean("knockback.disable");
        useDamagePercent = config.getBoolean("knockback.custom.damage-percent");
        customKnockBack = config.getDouble("knockback.custom.value");
        ConfigurationSection section = config.getConfigurationSection("scope-skin");
        if (section == null) return;
        scopes = section.getKeys(false);
        HashMap<String, ItemStack> scopeSkin = new HashMap<>();
        for (String weaponTitle : section.getKeys(false)) {
            String wea = section.getString(weaponTitle);
            if (wea == null) continue;
            String[] value = wea.split(":");
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

    void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfig();
    }
}
