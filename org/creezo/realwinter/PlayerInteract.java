/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Dodec
 */
public class PlayerInteract implements Listener{
    private Configuration Config = RealWinter.Config;
    private ItemStack ItemInHand;
    
    @EventHandler
    public synchronized void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if(Config.WaterBottleEnabled == true) {
            try {
                ItemInHand = event.getItem();
                int itemID = ItemInHand.getTypeId();
            } catch(Exception ex) { 
                ItemInHand = new ItemStack(35, 1);
                ItemInHand.setDurability((short)1);
            }
        }
        if(ItemInHand.getTypeId() == 373 && ItemInHand.getDurability() == 0 && Config.WaterBottleEnabled == true) {
            Thread WaterWait = new Thread(new Runnable() {

                @Override
                public void run() {
                    for(int i = 1; i == 1; i++) {
                        try {
                            Thread.sleep(1400);
                        } catch (InterruptedException ex) {
                            RealWinter.log.log(Level.SEVERE, ex.getLocalizedMessage());
                        }
                        if(player.getItemInHand().getTypeId() != 373) break;
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            RealWinter.log.log(Level.SEVERE, ex.getLocalizedMessage());
                        }
                        if(player.getItemInHand().getTypeId() == 374)
                        player.setSaturation(player.getSaturation() + Config.StaminaReplenish);
                        if(Config.DebugMode) RealWinter.log.log(Level.INFO, "Stamina Replenished to level: " + player.getSaturation());
                    }
                }
            });
            WaterWait.setDaemon(true);
            WaterWait.setName(player.getName());
            WaterWait.start();
        }
    }
    
    private static String ConvertIntToString(int number) {
        return "" + number;
    }
}
