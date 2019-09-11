package com.ericlam.mc.csweapon;

import com.hypernite.mc.hnmc.core.config.Prop;
import com.hypernite.mc.hnmc.core.config.yaml.Configuration;
import com.hypernite.mc.hnmc.core.config.yaml.Resource;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Resource(locate = "config.yml")
public class CWSConfig implements Configuration {

    @Prop(path = "flash-radius")
    int flash_radius;

    @Prop(path = "molotov-duration")
    int molo_duration;

    @Prop(path = "headshot.helmet-sound")
    String helmetSound;

    @Prop(path = "headshot.no-helmet-sound")
    String noHelmetSound;

    @Prop(path = "headshot.custom-sound")
    boolean customSound;

    @Prop(path = "knockback.disable")
    boolean noKnockBack;

    @Prop(path = "knockback.custom.damage-percent")
    boolean useDamagePercent;

    @Prop(path = "knockback.custom.value")
    double customKnockBack;

    @Prop(path = "molotov")
    List<String> molotovs;

    @Prop(path = "scope-skin")
    Set<String> scopes;

    @Prop(path = "flashbangs")
    List<String> flashbangs;

    @Prop(path = "scope-skin")
    Map<String, String> scopeSkin;

    @Prop(path = "flash-bypass-blacklist")
    List<String> flashBypass;

    @Prop
    Map<String, Double> shotguns;
}
