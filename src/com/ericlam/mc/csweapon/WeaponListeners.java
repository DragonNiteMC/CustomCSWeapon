package com.ericlam.mc.csweapon;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.WeaponExplodeEvent;
import com.shampaggon.crackshot.events.WeaponPreShootEvent;
import com.shampaggon.crackshot.events.WeaponScopeEvent;
import com.shampaggon.crackshot.events.WeaponShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class WeaponListeners implements Listener {
    private CSUtility csUtility;
    private CSDirector csDirector;
    private HashMap<Player, String> scoping = new HashMap<>();

    public WeaponListeners() {
        csUtility = new CSUtility();
        csDirector = csUtility.getHandle();
    }

    @EventHandler
    public void onMolotovExplode(WeaponExplodeEvent e) {
        if (!ConfigManager.getMolotovs().contains(e.getWeaponTitle())) return;
        MolotovManager.getInstance().spawnFires(e.getLocation().getBlock());
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        Player player = e.getPlayer();
        if (e.isSneaking()) {
            String weaponTitle = csUtility.getWeaponTitle(item);
            if (weaponTitle == null) return;
            if (!ConfigManager.getScopes().contains(weaponTitle)) return;
            scoping.put(player, weaponTitle);
            scope(weaponTitle, player);
        } else {
            unscope(player);
        }
    }

    @EventHandler
    public void onPlayerSwitch(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        unscope(player);
    }

    private void unscope(Player player) {
        if (!scoping.containsKey(player)) return;
        String weaponTitle = scoping.get(player);
        scoping.remove(player);
        CustomCSWeapon.getPlugin().getServer().getPluginManager().callEvent(new WeaponScopeEvent(player, weaponTitle, false));
        player.removePotionEffect(PotionEffectType.SPEED);
    }

    @EventHandler
    public void onScopeShoot(WeaponShootEvent e) {
        String weaponTitle = e.getWeaponTitle();
        if (!ConfigManager.getScopes().contains(weaponTitle)) return;
        if (!csDirector.getString(weaponTitle + ".Firearm_Action.Type").equalsIgnoreCase("bolt")) return;
        Player player = e.getPlayer();
        if (!scoping.containsKey(player)) return;
        CustomCSWeapon.getPlugin().getServer().getPluginManager().callEvent(new WeaponScopeEvent(player, weaponTitle, false));
        player.removePotionEffect(PotionEffectType.SPEED);
        int openTime = csDirector.getInt(e.getWeaponTitle() + ".Firearm_Action.Open_Duration");
        int closeShootDelay = csDirector.getInt(e.getWeaponTitle() + ".Firearm_Action.Close_Shoot_Delay");
        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomCSWeapon.getPlugin(), () -> scope(e.getWeaponTitle(), e.getPlayer()), closeShootDelay + openTime);
    }

    private void scope(String weaponTitle, Player player) {
        if (!scoping.containsKey(player)) return;
        int zoomAmount = csDirector.getInt(weaponTitle + ".Scope.Zoom_Amount");
        CustomCSWeapon.getPlugin().getServer().getPluginManager().callEvent(new WeaponScopeEvent(player, weaponTitle, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999 * 20, -zoomAmount));
    }

    @EventHandler
    public void arrarcute(WeaponPreShootEvent e) {
        if (!ConfigManager.getScopes().contains(e.getWeaponTitle())) return;
        if (!scoping.containsKey(e.getPlayer())) return;
        double spread = csDirector.getDouble(e.getWeaponTitle() + ".Scope.Zoom_Bullet_Spread");
        e.setBulletSpread(spread);
    }
}
