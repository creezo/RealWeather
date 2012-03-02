/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

/**
 *
 * @author creezo
 */
public class Configuration {
    private boolean enabled;
    
    public boolean getEnabled() {
        return enabled;
    }
    
    public boolean setEnabled(boolean state) {
        enabled = state;
        return enabled;
    }
    
    public int StartDelay() {
        int StartDelay = 20;
        return StartDelay;
    }
    
    public int CheckDelay() {
        int CheckDelay = 10;
        return CheckDelay;
    }
    
    public boolean DebugMode() {
        boolean DebugMode = true;
        return DebugMode;
    }
}
