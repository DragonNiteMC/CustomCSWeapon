package com.ericlam.mc.csweapon;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        getServer().getPluginManager().registerEvents(new WeaponListeners(), this);
        getServer().getPluginManager().registerEvents(new FlashBangListeners(), this);
    }

    @Override
    public void onDisable() {
        MolotovManager.getInstance().resetFires();
        MolotovManager.getInstance().resetLavaBlocks();
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
        if (command.getName().equalsIgnoreCase("csw-scope")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("not player !");
                return false;
            }
            Player player = (Player) sender;
            boolean contain = WeaponListeners.leftscopes.contains(player.getUniqueId());
            if (contain) WeaponListeners.leftscopes.remove(player.getUniqueId());
            else WeaponListeners.leftscopes.add(player.getUniqueId());
            player.sendMessage(!contain ? ChatColor.GREEN + "已切為左鍵開鏡。" : ChatColor.RED + "已切為蹲下開鏡。");
        }
        return true;
    }
}
