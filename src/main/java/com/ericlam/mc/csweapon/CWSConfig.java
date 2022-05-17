package com.ericlam.mc.csweapon;

import com.dragonite.mc.dnmc.core.config.yaml.Configuration;
import com.dragonite.mc.dnmc.core.config.yaml.Resource;

import java.util.List;
import java.util.Map;

@Resource(locate = "config.yml")
public class CWSConfig extends Configuration {

    public int molotov_duration;

    public List<String> molotov;

    public List<String> flashbangs;

    public int flash_radius;

    public List<String> flash_bypass_blacklist;

    public Map<String, Double> shotguns;

    public HeadShot headshot;

    public KnockBack knockback;


    public static class HeadShot {
        boolean custom_sound;
        String helmet_sound;
        String no_helmet_sound;
    }

    public static class KnockBack {
        boolean disable;
        Custom custom;

        static class Custom {
            boolean damage_percent;
            double value;
            double increase_rate;
        }

    }
}
