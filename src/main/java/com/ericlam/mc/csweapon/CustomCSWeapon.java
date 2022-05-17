package com.ericlam.mc.csweapon;

import com.ericlam.mc.csweapon.api.CCSWeaponAPI;
import com.ericlam.mc.csweapon.api.KnockBackManager;
import com.ericlam.mc.csweapon.api.MolotovManager;
import com.dragonite.mc.dnmc.core.main.DragoniteMC;
import com.dragonite.mc.dnmc.core.managers.YamlManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public class CustomCSWeapon extends JavaPlugin implements Listener, CCSWeaponAPI {
    private static CCSWeaponAPI api;
    private YamlManager yamlManager;
    private MolotovManager molotovManager;
    private MechanicListener knockBackManager;

    public static CCSWeaponAPI getApi() {
        return api;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEnable() {
        api = this;
        yamlManager = DragoniteMC.getAPI().getFactory().getConfigFactory(this).register("config.yml", CWSConfig.class).dump();
        CWSConfig cwsConfig = yamlManager.getConfigAs(CWSConfig.class);
        molotovManager = new MolotovManagerImpl(cwsConfig);
        knockBackManager = new MechanicListener(cwsConfig);
        getServer().getPluginManager().registerEvents(new WeaponListeners(this, cwsConfig), this);
        getServer().getPluginManager().registerEvents(knockBackManager, this);
    }

    @Override
    public void onDisable() {
        molotovManager.resetFires();
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, Command command, @Nonnull String label, @Nonnull String[] args) {
        if (command.getName().equals("csw-reload")) {
            if (!sender.hasPermission("dragonite.admin")) {
                sender.sendMessage(DragoniteMC.getAPI().getCoreConfig().getPrefix() + DragoniteMC.getAPI().getCoreConfig().getNoPerm());
                return false;
            }
            yamlManager.reloadConfigs();
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

    @Override
    public KnockBackManager getKnockBackManager() {
        return knockBackManager;
    }
}
