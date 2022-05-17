package com.ericlam.mc.csweapon.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface KnockBackManager {

    void createKnockBack(Vector vector, Entity victim, double value);

    void setCustomKnockBack(Player player, boolean enable);
}
