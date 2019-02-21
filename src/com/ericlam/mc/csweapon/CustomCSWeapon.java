package com.ericlam.mc.csweapon;

import com.shampaggon.crackshot.events.WeaponExplodeEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomCSWeapon extends JavaPlugin implements Listener, CommandExecutor {
    private static Plugin plugin;

    static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        new ConfigManager(this).loadConfig();
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        MolotovManager.getInstance().resetFires();
        MolotovManager.getInstance().resetLavaBlocks();
    }


    @EventHandler
    public void onMolotovExplode(WeaponExplodeEvent e){
        if (!ConfigManager.getMolotovs().contains(e.getWeaponTitle())) return;
        MolotovManager.getInstance().spawnFires(e.getLocation().getBlock());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("csw-reload")) {
            if (!sender.hasPermission("hypernite.admin")) {
                sender.sendMessage(com.hypernite.config.ConfigManager.getInstance().getNoPerm());
                return false;
            }
            ConfigManager.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reload completed.");
        }
        return true;
    }
}
