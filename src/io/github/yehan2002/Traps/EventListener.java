package io.github.yehan2002.Traps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;


public class EventListener implements Listener{

    private static String prefix = ChatColor.GREEN + "" + ChatColor.MAGIC + "$" + ChatColor.GREEN + " ";
    private static String suffix = " " + ChatColor.MAGIC + "$";
    private boolean debug = false;
    private YamlConfiguration config;
    private File configFile;


    EventListener(JavaPlugin plugin){
        File file = new File(plugin.getDataFolder() + "");

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Logger.getGlobal().severe("[Trap] Failed to create plugin directory.");
            }
        }

        configFile = new File(plugin.getDataFolder() + "/Traps.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!configFile.exists()) {
            config.options().copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveConfig(){
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void PlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(!p.getLocation().getBlock().isEmpty() && p.getLocation().getBlock().getType() == Material.TRIPWIRE){

            if (config.get(getIdentifier(p.getLocation().getBlock())) != null){

                String trap = (String) config.get(getIdentifier(p.getLocation().getBlock()));

                String Custom = null;


                if (trap.contains("#")){
                    Custom = trap.split("#")[1];
                    Custom = Custom.substring(1, Custom.length() - 1);
                    trap = trap.split("#")[0];
                }

                TrapTriggeredEvent event = new TrapTriggeredEvent(p, TrapManager.valueOf(trap), Custom);

                Bukkit.getServer().getPluginManager().callEvent(event);

                if (! event.isCancelled()){

                    Logger.getGlobal().severe("[Trap] Unhandled Trap " + trap + "@" + getIdentifier(p.getLocation().getBlock()));

                } else {
                    if (event.isRemove()) {
                        p.getLocation().getBlock().setType(Material.AIR);
                        config.set(getIdentifier(p.getLocation().getBlock()),  null);
                        saveConfig();
                    }
                    if (debug) {
                        Logger.getGlobal().info("[Trap] Triggered  " + trap + "@" + getIdentifier(p.getLocation().getBlock()) + ".");
                    }

                }

            }

        }
    }

    private ItemStack newTrap(Object name){
        if (name instanceof String | name instanceof TrapManager) {
            if (name instanceof TrapManager) {
                name = name.toString() + " Trap";
            }
            ItemStack drop = new ItemStack(Material.STRING);
            ItemMeta meta = drop.getItemMeta();
            meta.setDisplayName(prefix + " " + name + " " + suffix);
            drop.setItemMeta(meta);
            return drop;
        } else {
            Logger.getGlobal().severe("[Trap] Invalid Trap Type.");
        }

        return null;
    }

    private String Unwrap(String name){
        name = name.substring(prefix.length());
        name = name.substring(0, name.length() - suffix.length());
        return name;
    }



    private String getIdentifier(Block e){
        String identifier = "x" + e.getX() + "y" + e.getY() + "z" + e.getZ();
        identifier = identifier.replace(".", "d");
        identifier = identifier.replace('-', 'm');
        return identifier;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void trapPlace(BlockPlaceEvent e){
        if (e.getItemInHand().getType() == Material.STRING && e.getItemInHand().getItemMeta().getDisplayName() != null) {
            TrapManager trapManager = TrapManager.getTrap(e.getItemInHand().getItemMeta().getDisplayName());

            if (trapManager != null) {
                String type = trapManager.toString();
                if (trapManager == TrapManager.Custom){
                    type = type + "#" + Unwrap(e.getItemInHand().getItemMeta().getDisplayName());
                }
                config.set(this.getIdentifier(e.getBlock()), type);
                if (debug) {
                    Logger.getGlobal().info("[Trap] Placed " + type + " trap @ " + this.getIdentifier(e.getBlock()) + ".");
                }
                saveConfig();
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent e){
        if(!e.getBlock().isEmpty() && e.getBlock().getType() == Material.TRIPWIRE){
            if (config.get(getIdentifier(e.getBlock())) != null) {
                Map enc;
                try {
                    enc = e.getPlayer().getInventory().getItemInMainHand().getEnchantments();
                }catch (NoSuchMethodError methodError){
                    enc = e.getPlayer().getInventory().getItemInHand().getEnchantments();
                }
                Player p = e.getPlayer();
                if (enc.containsKey(Enchantment.SILK_TOUCH) | enc.containsKey(Enchantment.LOOT_BONUS_BLOCKS) | enc.containsKey(Enchantment.LOOT_BONUS_MOBS)) {
                    String trap = (String) config.get(getIdentifier(e.getBlock()));

                    String Custom = null;


                    if (trap.contains("#")){
                        Custom = trap.split("#")[1];
                        Custom = Custom.substring(1, Custom.length() - 1);
                        trap = trap.split("#")[0];
                    }



                    if (Custom != null){
                        p.getWorld().dropItemNaturally(e.getBlock().getLocation(), newTrap(Custom));
                    } else {
                        p.getWorld().dropItemNaturally(e.getBlock().getLocation(), newTrap(TrapManager.valueOf(trap)));

                    }

                } else {
                    e.setExpToDrop(50);
                }
                config.set(getIdentifier(e.getBlock()),null);
                saveConfig();
            }
        }
    }
}
