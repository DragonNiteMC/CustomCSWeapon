package com.ericlam.mc.csweapon;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.WeaponExplodeEvent;
import com.shampaggon.crackshot.events.WeaponPreShootEvent;
import com.shampaggon.crackshot.events.WeaponScopeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
            int zoomAmount = csDirector.getInt(weaponTitle + ".Scope.Zoom_Amount");
            CustomCSWeapon.getPlugin().getServer().getPluginManager().callEvent(new WeaponScopeEvent(player, weaponTitle, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999 * 20, -zoomAmount));
            //CSPapi.changeSkin(item,player,weaponTitle,"Scope");
        } else {
            if (!scoping.containsKey(player)) return;
            String weaponTitle = scoping.get(player);
            scoping.remove(player);
            CustomCSWeapon.getPlugin().getServer().getPluginManager().callEvent(new WeaponScopeEvent(player, weaponTitle, false));
            player.removePotionEffect(PotionEffectType.SPEED);
            //CSPapi.removeSkin(item,player,weaponTitle);
        }
    }

    @EventHandler
    public void blockNormalScope(WeaponScopeEvent e) {
        if (!ConfigManager.getScopes().contains(e.getWeaponTitle())) return;
        if (e.isZoomIn() && !scoping.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            csDirector.plugin.getServer().getPluginManager().callEvent(new WeaponScopeEvent(e.getPlayer(), e.getWeaponTitle(), false));
        } else if (!e.isZoomIn() && scoping.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            csDirector.plugin.getServer().getPluginManager().callEvent(new WeaponScopeEvent(e.getPlayer(), e.getWeaponTitle(), true));
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        String weaponTitle = csUtility.getWeaponTitle(item);
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (scoping.containsKey(e.getPlayer())) return;
        if (weaponTitle == null) return;
        if (!ConfigManager.getScopes().contains(weaponTitle)) return;
        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);
    }

    @EventHandler
    public void arrarcute(WeaponPreShootEvent e) {
        if (!ConfigManager.getScopes().contains(e.getWeaponTitle())) return;
        if (!scoping.containsKey(e.getPlayer())) return;
        double spread = csDirector.getDouble(e.getWeaponTitle() + ".Scope.Zoom_Bullet_Spread");
        e.setBulletSpread(spread);
    }
}
