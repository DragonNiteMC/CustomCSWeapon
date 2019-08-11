package com.ericlam.mc.csweapon;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.*;
import me.DeeCaaD.CrackShotPlus.Events.WeaponPreReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class WeaponListeners implements Listener {
    public static HashSet<UUID> leftScopes = new HashSet<>(); //later change permission
    private final CSUtility csUtility;
    private final CSDirector csDirector;
    private HashMap<Player, String> scoping = new HashMap<>();
    private HashMap<Player, ItemStack> originalOffhandItem = new HashMap<>();
    private final CustomCSWeapon csWeapon;

    public WeaponListeners(CustomCSWeapon csWeapon) {
        this.csWeapon = csWeapon;
        csUtility = new CSUtility();
        csDirector = csUtility.getHandle();
    }

    @EventHandler
    public void onMolotovExplode(WeaponExplodeEvent e) {
        if (!ConfigManager.getMolotovs().contains(e.getWeaponTitle())) return;
        csWeapon.getMolotovManager().spawnFires(e.getLocation().getBlock());
    }

    @EventHandler
    public void onGunDamage(WeaponDamageEntityEvent e) {
        if (!(e.getDamager() instanceof Projectile)) return;
        if (!(e.getVictim() instanceof Player)) return;
        Player player = (Player) e.getVictim();
        if (!e.isHeadshot()) return;
        boolean helmet = player.getInventory().getHelmet() != null;
        String sound = helmet ? ConfigManager.helmetSound : ConfigManager.noHelmetSound;
        if (ConfigManager.customSound) {
            player.getWorld().playSound(player.getLocation(), Sound.valueOf(sound), 5, 1);
        } else {
            player.getWorld().playSound(player.getLocation(), sound, 5, 1);
        }


    }


    @EventHandler
    public void onPlayerLeftScope(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        Player player = e.getPlayer();
        if (e.getAction() != Action.LEFT_CLICK_AIR) return;
        if (!leftScopes.contains(player.getUniqueId())) return;
        if (!scoping.containsKey(player)) {
            String weaponTitle = csUtility.getWeaponTitle(item);
            if (weaponTitle == null) return;
            if (!ConfigManager.getScopes().contains(weaponTitle)) return;
            scoping.put(player, weaponTitle);
            scope(weaponTitle, player, true);
        } else {
            unscope(player, true);
        }

    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        Player player = e.getPlayer();
        if (leftScopes.contains(player.getUniqueId())) return;
        if (e.isSneaking()) {
            String weaponTitle = csUtility.getWeaponTitle(item);
            if (weaponTitle == null) return;
            if (!ConfigManager.getScopes().contains(weaponTitle)) return;
            scoping.put(player, weaponTitle);
            scope(weaponTitle, player, true);
        } else {
            unscope(player, true);
        }
    }

    @EventHandler
    public void onPlayerSwitch(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        unscope(player, true);
    }

    private void unscope(Player player, boolean remove) {
        if (!scoping.containsKey(player)) return;
        String weaponTitle = scoping.get(player);
        if (remove) scoping.remove(player);
        HashMap<String, ItemStack> skinScope = ConfigManager.getScopeSkin();
        if (player.getInventory().getItemInOffHand().isSimilar(skinScope.get(weaponTitle))) {
            ItemStack stack;
            if (originalOffhandItem.containsKey(player)) stack = originalOffhandItem.get(player);
            else stack = new ItemStack(Material.AIR);
            player.getInventory().setItemInOffHand(stack);
            originalOffhandItem.remove(player);
        }
        csWeapon.getServer().getPluginManager().callEvent(new WeaponScopeEvent(player, weaponTitle, false));
        player.removePotionEffect(PotionEffectType.SPEED);
    }

    @EventHandler
    public void onScopeShoot(WeaponShootEvent e) {
        String weaponTitle = e.getWeaponTitle();
        if (!ConfigManager.getScopes().contains(weaponTitle)) return;
        if (!csDirector.getString(weaponTitle + ".Firearm_Action.Type").equalsIgnoreCase("bolt")) return;
        Player player = e.getPlayer();
        unscope(player, false);
        int openTime = csDirector.getInt(e.getWeaponTitle() + ".Firearm_Action.Open_Duration");
        int closeShootDelay = csDirector.getInt(e.getWeaponTitle() + ".Firearm_Action.Close_Shoot_Delay");
        Bukkit.getScheduler().scheduleSyncDelayedTask(csWeapon, () -> scope(e.getWeaponTitle(), e.getPlayer(), false), closeShootDelay + openTime);
    }

    private void scope(String weaponTitle, Player player, boolean put) {
        if (!scoping.containsKey(player)) return;
        int zoomAmount = csDirector.getInt(weaponTitle + ".Scope.Zoom_Amount");
        csWeapon.getServer().getPluginManager().callEvent(new WeaponScopeEvent(player, weaponTitle, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999 * 20, -zoomAmount));
        if (!ConfigManager.getScopeSkin().containsKey(weaponTitle)) return;
        PlayerInventory playerInventory = player.getInventory();
        final ItemStack original_item = playerInventory.getItemInOffHand();
        if (put && original_item != null && original_item.getType() != Material.AIR)
            originalOffhandItem.put(player, original_item);
        ItemStack stack = ConfigManager.getScopeSkin().get(weaponTitle);
        playerInventory.setItemInOffHand(stack);
    }

    @EventHandler
    public void onReload(WeaponPreReloadEvent e) {
        if (originalOffhandItem.containsKey(e.getPlayer())) unscope(e.getPlayer(), true);
    }

    @EventHandler
    public void arrarcute(WeaponPreShootEvent e) {
        if (!ConfigManager.getScopes().contains(e.getWeaponTitle())) return;
        if (!scoping.containsKey(e.getPlayer())) return;
        double spread = csDirector.getDouble(e.getWeaponTitle() + ".Scope.Zoom_Bullet_Spread");
        e.setBulletSpread(spread);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        if (e.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if (!e.getClickedInventory().equals(player.getInventory())) return;
        if (e.getSlot() != 40) return;
        e.setCancelled(true);
    }
}
