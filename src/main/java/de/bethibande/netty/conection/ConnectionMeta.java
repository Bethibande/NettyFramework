package de.bethibande.netty.conection;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ConnectionMeta {

    private final HashMap<String, Object> meta = new HashMap<>();

    public void put(String key, Object value) {
        meta.put(key, value);
    }

    public Object get(String key) {
        return meta.get(key);
    }

    public void remove(String key) {
        meta.remove(key);
    }

    public boolean containsKey(String key) {
        return meta.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return meta.containsValue(value);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) meta.get(key);
    }

    public String getString(String key) {
        return (String) meta.get(key);
    }

    public Byte getByte(String key) {
        return (Byte) meta.get(key);
    }

    public Short getShort(String key) {
        return (Short) meta.get(key);
    }

    public Integer getInt(String key) {
        return (Integer) meta.get(key);
    }

    public Long getLong(String key) {
        return (Long) meta.get(key);
    }

    public Float getFloat(String key) {
        return (Float) meta.get(key);
    }

    public Double getDouble(String key) {
        return (Double) meta.get(key);
    }

    public UUID getUUID(String key) {
        return (UUID) meta.get(key);
    }

    public Collection<String> keySet() {
        return meta.keySet();
    }

    public Collection<Object> values() {
        return meta.values();
    }

    public void clear() {
        meta.clear();
    }

}
