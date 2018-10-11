package io.github.yehan2002.Traps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;



import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class Main extends JavaPlugin implements Listener, CommandExecutor {

    private static String prefix = ChatColor.GREEN + "" + ChatColor.MAGIC + "$" + ChatColor.GREEN + " ";
    private static String suffix = " " + ChatColor.MAGIC + "$";
    private boolean debug = false;
    private YamlConfiguration config;
    private File configFile;
    private Economy econ;
    private TrapShop trapShop;

    @Override
    public void onEnable() {
        File file = new File(this.getDataFolder() + "");

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Logger.getGlobal().severe("[Trap] Failed to create plugin directory.");
            }
        }

        configFile = new File(this.getDataFolder() + "/Traps.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!configFile.exists()) {
            config.options().copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!setupEconomy()) {
            this.getLogger().severe("[Trap] Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.registerDefault();

        trapShop = new TrapShop(econ);
        trapShop.initializeItems();
        this.getServer().getPluginManager().registerEvents(trapShop, this);
        this.getServer().getPluginManager().registerEvents(new TrapListener(this),  this);
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    private void registerDefault(){
        TrapManager.registerTrap("Fire Trap", "Sets the enemy on Fire", 1000);
        TrapManager.registerTrap("Fake Diamond Troll", "Spawns a diamond that can't be picked up.", 1000);
        TrapManager.registerTrap("Poison Trap", "Poisons the player", 1500);
        TrapManager.registerTrap("Glow Trap", "Makes the Player Glow like a beacon", 1500);
        TrapManager.registerTrap("Herobrine Troll", "A Herobrine Jump-scare.", 2000);
        TrapManager.registerTrap("Creative Mode Troll", "Fake Creative mode.", 2000);
        TrapManager.registerTrap("Fake Op troll", "Fake Op", 2000);
        TrapManager.registerTrap("Launch Trap", "Sends the player flying!!", 2500);
        TrapManager.registerTrap("TNT Trap", "Summons Primed TNT", 5000);
        TrapManager.registerTrap("Lightning Trap", "Summons a Lightning bolt", 7500);
        TrapManager.registerTrap("Thief Trap", "Steals the item the player is holding", 10000);
        TrapManager.registerTrap("Lava Trap", "Slowly sends the player into lava.", 20000);
        TrapManager.registerTrap("Cage Trap", "Traps the player in a cage", 25000);

    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void saveConfig(){
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

                this.getServer().getPluginManager().callEvent(event);

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

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0 | (args.length == 1 && args[0].equalsIgnoreCase("help"))){
                p.sendMessage(ChatColor.DARK_AQUA + "-----------                -----------");
                p.sendMessage(ChatColor.GOLD + "                Traps & Trolls            ");
                p.sendMessage(ChatColor.DARK_AQUA + "-----------                -----------");
                p.sendMessage("");
                p.sendMessage(ChatColor.DARK_GREEN + "Created by " + ChatColor.GREEN + "yj22k");
                p.sendMessage(ChatColor.GREEN + "Website : http://www.github.com/yehan2002");
                p.sendMessage("");
                p.sendMessage(ChatColor.GREEN + "Commands:");
                p.sendMessage("/trap shop");
                p.sendMessage("/trap help");
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("shop")){
                trapShop.openInventory(p);
            }
            return true;
        }
        return false;
    }

    private String getIdentifier(Block e){
        String identifier = "x" + e.getX() + "y" + e.getY() + "z" + e.getZ();
        identifier = identifier.replace(".", "d");
        identifier = identifier.replace('-', 'm');
        return identifier;
    }

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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent e){
        if(!e.getBlock().isEmpty() && e.getBlock().getType() == Material.TRIPWIRE){
            if (config.get(getIdentifier(e.getBlock())) != null) {
                Map enc = e.getPlayer().getInventory().getItemInMainHand().getEnchantments();
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

    @Override
    public void onDisable() {
        saveConfig();
        Logger.getGlobal().info("Disabled Successfully.");

    }
}
