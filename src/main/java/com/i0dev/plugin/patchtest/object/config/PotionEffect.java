package com.i0dev.plugin.patchtest.object.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@Setter
public class PotionEffect implements SerializableConfig {
    String potionEffect;
    int level;
    int duration;


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("potionEffect", potionEffect);
        data.put("level", level);
        data.put("duration", duration);
        return data;
    }

    public PotionEffect(Map<String, Object> map) {
        this.potionEffect = (String) map.get("potionEffect");
        this.level = (int) map.get("level");
        this.duration = (int) map.get("duration");
    }


}
