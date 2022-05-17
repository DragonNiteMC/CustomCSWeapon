package com.ericlam.mc.csweapon;

import me.deecaad.core.file.Configuration;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import me.deecaad.weaponmechanics.weapon.damage.DamagePoint;
import me.deecaad.weaponmechanics.weapon.weaponevents.*;
import me.deecaad.weaponmechanics.wrappers.ZoomData;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.UUID;

public class WeaponListeners implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaponListeners.class);
    static HashSet<UUID> leftScopes = new HashSet<>(); //later change permission
    private final CustomCSWeapon csWeapon;
    private final CWSConfig cwsConfig;
    private final Configuration wmConfig;

    private final ReflectionManager reflectionManager;

    WeaponListeners(CustomCSWeapon csWeapon, CWSConfig cwsConfig) {
        this.csWeapon = csWeapon;
        this.cwsConfig = cwsConfig;
        this.wmConfig = WeaponMechanics.getConfigurations();
        this.reflectionManager = new ReflectionManager();
    }

    @EventHandler
    public void onMolotovExplode(ProjectileExplodeEvent e) {
        if (!cwsConfig.molotov.contains(e.getWeaponTitle())) return;
        csWeapon.getMolotovManager().spawnFires(e.getLocation().getBlock());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGunDamage(WeaponDamageEntityEvent e) {
        if (!(e.getVictim() instanceof Player player)) return;
        if (!(e.getShooter() instanceof Player attacker)) return;
        this.checkFriendlyFire(e, attacker, player);
        this.shotGunDamage(e, attacker, player);
    }

    @EventHandler
    public void onHeadShot(ProjectileHitEntityEvent e) {
        if (e.getPoint() != DamagePoint.HEAD) return;
        if (!(e.getShooter() instanceof Player player)) return;
        boolean helmet = player.getInventory().getHelmet() != null;
        String sound = helmet ? cwsConfig.headshot.helmet_sound : cwsConfig.headshot.no_helmet_sound;
        if (!cwsConfig.headshot.custom_sound) {
            player.getWorld().playSound(player.getLocation(), Sound.valueOf(sound), 3, 1);
        } else {
            player.getWorld().playSound(player.getLocation(), sound, 3, 1);
        }
    }

    private void checkFriendlyFire(WeaponDamageEntityEvent e, Player attacker, Player player) {
        Team VTteam = player.getScoreboard().getEntryTeam(player.getName());
        Team ATteam = attacker.getScoreboard().getEntryTeam(attacker.getName());
        if (VTteam == null || ATteam == null) {
            return;
        }
        if (ATteam.getName().equals(VTteam.getName())) {
            if (!ATteam.allowFriendlyFire()) {
                e.setCancelled(true);
            }
        }
    }

    private void shotGunDamage(WeaponDamageEntityEvent e, Player attacker, Player victim) {
        if (!cwsConfig.shotguns.containsKey(e.getWeaponTitle())) return;
        double distance = attacker.getLocation().distance(victim.getLocation());
        final double origDamage = e.getFinalDamage();
        final double finalDamage = origDamage - (distance * cwsConfig.shotguns.get(e.getWeaponTitle()));
        e.setFinalDamage(finalDamage <= 5 ? 5 : finalDamage);
    }

    @EventHandler
    public void onPlayerSwitch(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        unscope(player);
    }

    private void unscope(Player player) {
        LOGGER.info("unscoping " + player.getName());
        var pWrap = WeaponMechanics.getPlayerWrapper(player);
        pWrap.getMainHandData().ifZoomingForceZoomOut();
    }

    @EventHandler
    public void onScopeShoot(WeaponShootEvent e) {
        if (!(e.getShooter() instanceof Player player)) return;
        String weaponTitle = e.getWeaponTitle();
        var fireArmType = wmConfig.getString(weaponTitle + ".Firearm_Action.Type");
        if (fireArmType == null) {
            LOGGER.info("fireArmType is null for " + weaponTitle);
            return;
        }
        if (!fireArmType.equalsIgnoreCase("LEVER")) {
            LOGGER.info("fireArmType is not lever for " + weaponTitle);
            return;
        }
        var mainHand = player.getInventory().getItemInMainHand();
        if (WeaponMechanicsAPI.getWeaponTitle(mainHand) == null) {
            LOGGER.info("mainHand is null for " + weaponTitle);
            return;
        }
        unscope(player);
        int openTime = wmConfig.getInt(e.getWeaponTitle() + ".Firearm_Action.Open.Time");
        int closeShootDelay = wmConfig.getInt(e.getWeaponTitle() + ".Firearm_Action.Close.Time");
        var zoomData = WeaponMechanics.getPlayerWrapper(player).getMainHandData().getZoomData();
        Bukkit.getScheduler().scheduleSyncDelayedTask(csWeapon, () -> scope(e.getWeaponTitle(), player, zoomData, mainHand), closeShootDelay + openTime);
    }

    private void scope(String weaponTitle, Player player, ZoomData zoomData, ItemStack stack) {
        if (WeaponMechanicsAPI.isScoping(player)) return;
        LOGGER.info("scoping " + player.getName());
        reflectionManager.zoomIn(WeaponMechanics.getWeaponHandler().getScopeHandler(), player, weaponTitle, zoomData, stack);
    }

    @EventHandler
    public void onReload(WeaponPreReloadEvent e) {
        if (!(e.getShooter() instanceof Player player)) return;
        if (leftScopes.contains(player.getUniqueId())) {
            unscope(player);
        }
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

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventorySlots().contains(40)) e.setCancelled(true);
    }
}
