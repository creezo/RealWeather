/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realweather;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Dodec
 */
public class DamageEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private int dmg;
    private Player plr;
    private int Health;
    
    public DamageEvent(Player player, int damage, int health) {
        dmg = damage;
        plr = player;
        Health = health;
    }
    
    public Player getPlayer() {
        return plr;
    }
    
    public int getDamage() {
        return dmg;
    }
    
    public int getHealth() {
        return Health;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
