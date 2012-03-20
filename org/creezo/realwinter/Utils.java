/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.util.logging.Level;

import org.bukkit.entity.Player;

/**
 *
 * @author Dodec
 */
public class Utils {
    public boolean SendMessage(Player player, String message) {
        try {
            player.sendMessage(message);
            return true;
        } catch (Exception e) {
            RealWinter.log.log(Level.INFO, message);
            return false;
        }
    }
    
    public boolean SendHelp(Player player) {
        try {
            player.sendMessage("Help message");
            return true;
        } catch (Exception e) {
            RealWinter.log.log(Level.INFO, "Help message");
            return false;
        }
    }
}
