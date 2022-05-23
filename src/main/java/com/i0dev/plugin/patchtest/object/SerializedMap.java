package com.i0dev.plugin.patchtest.object;

import com.boydti.fawe.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;

public class SerializedMap<K, V> extends HashMap<K, V> {

    @Override
    public V put(K key, V value) {
        if (value instanceof ConfigurationSerializable) {
            super.put(key, (V) ((ConfigurationSerializable) value).serialize());
        }
        return super.put(key, value);
    }
}
