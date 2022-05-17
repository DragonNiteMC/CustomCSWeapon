package com.ericlam.mc.csweapon;

import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.weapon.scope.ScopeHandler;
import me.deecaad.weaponmechanics.wrappers.EntityWrapper;
import me.deecaad.weaponmechanics.wrappers.ZoomData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class ReflectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeHandler.class);

    private final Method zoomIn;

    public ReflectionManager() {
        try {
            zoomIn = ScopeHandler.class.getDeclaredMethod("zoomIn", ItemStack.class, String.class, EntityWrapper.class, ZoomData.class, EquipmentSlot.class);
            zoomIn.setAccessible(true);
        }catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void zoomIn(ScopeHandler handler, Player player, String weaponTitle, ZoomData data, ItemStack itemStack) {
        var playerWrapper = WeaponMechanics.getPlayerWrapper(player);
        try {
            zoomIn.invoke(handler, itemStack, weaponTitle, playerWrapper, data, EquipmentSlot.HAND);
        }catch (Exception e){
            LOGGER.warn("Failed to zoom in for player {} using {}", player.getName(), weaponTitle);
            e.printStackTrace();
        }
    }
}
