package org.creezo.realweather.feature;

import java.util.HashMap;

/**
 *
 * @author Dodec
 */
public class SharedData {
    private final HashMap<String, Object> data = new HashMap<String, Object>();
    private final HashMap<String, Feature> owner = new HashMap<String, Feature>();
    private final HashMap<String, Boolean> permission = new HashMap<String, Boolean>();
    
    public Object getValue(String key) {
        return data.get(key);
    }
    
    public boolean setValue(String key, Object value, Feature feature, boolean write) {
        if(data.containsKey(key)) {
            if(owner.get(key) == feature) {
                data.put(key, value);
                return true;
            } else {
                if(permission.get(key)) {
                    data.put(key, value);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            data.put(key, value);
            owner.put(key, feature);
            permission.put(key, write);
            return true;
        }
    }
    
    public boolean removeValue(String key, Feature feature) {
        if(owner.get(key) == feature) {
            data.remove(key);
            owner.remove(key);
            permission.remove(key);
            return true;
        } else {
            return false;
        }
    }
}
