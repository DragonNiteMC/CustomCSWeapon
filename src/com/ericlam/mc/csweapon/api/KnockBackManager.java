package com.ericlam.mc.csweapon.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface KnockBackManager {

    void createKnockBack(Entity damager, Entity victim, double value);

    void setCustomKnockBack(Player player, boolean enable);
}
