package com.ericlam.mc.csweapon;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.ericlam.mc.csweapon.api.CCSWeaponAPI;
import com.ericlam.mc.csweapon.api.MolotovManager;
import com.hypernite.mc.hnmc.core.main.HyperNiteMC;
import com.shampaggon.crackshot.MaterialManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Map;

public class CustomCSWeapon extends JavaPlugin implements Listener, CCSWeaponAPI {
    private static CCSWeaponAPI api;
    private final MolotovManager molotovManager = new MolotovManagerImpl();

    public static CCSWeaponAPI getApi() {
        return api;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEnable() {
        api = this;
        new ConfigManager(this).loadConfig();
        getServer().getPluginManager().registerEvents(new WeaponListeners(this), this);
        getServer().getPluginManager().registerEvents(new FlashBangListeners(), this);

        // only fix in 1.14
        if (!ProtocolLibrary.getProtocolManager().getMinecraftVersion().isAtLeast(MinecraftVersion.VILLAGE_UPDATE))
            return;

        // Fix CrackShot 351~11 problem in 1.14.*
        try {
            Class<?> cls = MaterialManager.class;
            Field materialMapField = cls.getDeclaredField("map");
            materialMapField.setAccessible(true);
            Map<String, String> materialMap = (Map<String, String>) materialMapField.get(null);
            materialMap.replace("351~11", "YELLOW_DYE");
            this.getLogger().warning("CrackShot doesn't support 1.14.*, but we have fixed the material type problem.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        molotovManager.resetFires();
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, Command command, @Nonnull String label, @Nonnull String[] args) {
        if (command.getName().equals("csw-reload")) {
            if (!sender.hasPermission("hypernite.admin")) {
                sender.sendMessage(HyperNiteMC.getAPI().getCoreConfig().getPrefix() + HyperNiteMC.getAPI().getCoreConfig().getNoPerm());
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
            boolean contain = WeaponListeners.leftScopes.contains(player.getUniqueId());
            if (contain) WeaponListeners.leftScopes.remove(player.getUniqueId());
            else WeaponListeners.leftScopes.add(player.getUniqueId());
            player.sendMessage(!contain ? ChatColor.GREEN + "已切為左鍵開鏡。" : ChatColor.RED + "已切為蹲下開鏡。");
        }
        return true;
    }

    @Override
    public MolotovManager getMolotovManager() {
        return molotovManager;
    }
}
