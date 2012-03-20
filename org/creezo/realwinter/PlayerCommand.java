/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

/**
 *
 * @author creezo
 */
public class PlayerCommand implements Listener {
    private final RealWinter plugin;
    public PlayerCommand(RealWinter plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerCommand(ServerCommandEvent event) {
        plugin.log.log(Level.INFO, "Command");
        Player player = null;
        String[] command = event.getCommand().toLowerCase().split(" ");
        for(int once = 1;once <= 1;once++) {
            if(event.getSender() instanceof Player) {
                plugin.log.log(Level.INFO, "Instance of player");
                player = (Player) event.getSender();
                if(!player.isOp()) {
                    break;
                }
            }
            
            if("rw".equals(command[0])) {
                if(command.length == 1) {
                    SendMessage(player, "No arguments set. Try /rw help");
                } else {
                    if("help".equals(command[1])) {
                        SendHelp(player, "Help broadcast");
                    } else if("version".equals(command[1])) {
                        SendMessage(player, "RealWinter version: " + plugin.getDescription().getVersion());
                    }
                }
            }
        }
        
    }
    
    private void SendMessage(Player player, String message) {
        try {
            player.sendMessage(message);
        } catch (Exception e) {
            plugin.log.log(Level.INFO, message);
        }
    }
    
    private void SendHelp(Player player, String message) {
        try {
            player.sendMessage(message);
        } catch (Exception e) {
            plugin.log.log(Level.INFO, message);
        }
    }
}
