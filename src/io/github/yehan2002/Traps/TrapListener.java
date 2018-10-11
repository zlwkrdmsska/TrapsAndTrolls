package io.github.yehan2002.Traps;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class TrapListener implements Listener {
    private JavaPlugin plugin;

    TrapListener(JavaPlugin p) {
        plugin = p;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTrap(TrapTriggeredEvent e) {

        if (e.getTrap() == TrapManager.Fire) {
            e.getPlayer().setFireTicks(100);
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.Lightning) {

            e.getPlayer().getWorld().strikeLightning(e.getPlayer().getLocation());
            e.setCancelled(true);

        } else if (e.getTrap() == TrapManager.Glow) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 1));
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.Poison) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.Launch) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 10), false);
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.TNT) {
            e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.PRIMED_TNT);
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.Thief) {

            this.ThiefTrap(e);
            e.setRemove(false);
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.Lava) {
            this.LavaTrap(e);
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.Cage) {
            this.CageTrap(e);
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.Diamond) {
            this.DiamondTroll(e);
            e.setCancelled(true);
        } else if (e.getTrap() == TrapManager.Creative){
            this.CreativeTroll(e);
            e.setCancelled(true);
        } else  if (e.getTrap() == TrapManager.Op){
            this.OpTroll(e);
            e.setCancelled(true);
        } else  if (e.getTrap() == TrapManager.Herobrine){
            this.HerobrineTroll(e);
            e.setCancelled(true);
        }


    }

    private void HerobrineTroll(TrapTriggeredEvent e){
        Location l = e.getPlayer().getLocation();

        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDERDRAGON_AMBIENT, 1, 5);


        l.add(2, 0, 0);
        Creeper c = (Creeper) e.getPlayer().getWorld().spawnEntity(l, EntityType.CREEPER);
        c.setExplosionRadius(0);
        c.setPowered(true);
        c.setTarget(e.getPlayer());
        c.setMaxFuseTicks(40);
        c.setVelocity(e.getPlayer().getVelocity());

        l.subtract(4, 0, 0);
        c = (Creeper) e.getPlayer().getWorld().spawnEntity(l, EntityType.CREEPER);
        c.setExplosionRadius(0);
        c.setPowered(true);
        c.setTarget(e.getPlayer());
        c.setMaxFuseTicks(40);
        c.setVelocity(e.getPlayer().getVelocity());

        l.add(2, 0, 2);
        c = (Creeper) e.getPlayer().getWorld().spawnEntity(l, EntityType.CREEPER);
        c.setExplosionRadius(0);
        c.setPowered(true);
        c.setTarget(e.getPlayer());
        c.setMaxFuseTicks(40);
        c.setVelocity(e.getPlayer().getVelocity());

        l.subtract(0, 0, 4);
        c = (Creeper) e.getPlayer().getWorld().spawnEntity(l, EntityType.CREEPER);
        c.setExplosionRadius(0);
        c.setPowered(true);
        c.setTarget(e.getPlayer());
        c.setMaxFuseTicks(40);
        c.setVelocity(e.getPlayer().getVelocity());

    }

    private void ThiefTrap(TrapTriggeredEvent e) {
        Player p = e.getPlayer();
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR | p.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK) {
            return;
        }
        Location l = p.getLocation();
        l.setY(l.getBlockY() - 2);
        Block block = l.getBlock();
        block.setType(Material.CHEST);

        Chest c = (Chest) block.getState();

        c.getBlockInventory().addItem(p.getInventory().getItemInMainHand());

        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle("Stolen Item");
        bookMeta.setAuthor("Unknown");
        bookMeta.setPages("X: " + l.getBlockX() + "\nY: " + l.getBlockY() + "\nZ: " + l.getBlockZ());
        writtenBook.setItemMeta(bookMeta);

        p.getInventory().setItemInMainHand(writtenBook);
    }

    private void LavaTrap(TrapTriggeredEvent e) {
        Player p = e.getPlayer();
        Location l = p.getLocation();
        Location lava;
        HashMap<Location, Material> resetList = new HashMap<>();
        l.setY(l.getY() - 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.AIR);
        l.setY(l.getY() - 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.WEB);
        l.setY(l.getY() - 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.WEB);
        l.setY(l.getY() - 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.LAVA);
        lava = l.clone();
        Runnable noDeath = new BukkitRunnable() {
            @Override
            public void run() {
                if (p.getHealth() < 5) {
                    lava.getBlock().setType(Material.MAGMA);
                    p.setFireTicks(0);
                }
            }
        };
        BukkitTask noDeathRunner = Bukkit.getServer().getScheduler().runTaskTimer(plugin, noDeath, 200, 20);

        Runnable reset = new BukkitRunnable() {
            @Override
            public void run() {
                if (l.getChunk().isLoaded()) {
                    for (Location loc : resetList.keySet()) {
                        loc.getBlock().setType(resetList.get(loc));

                    }
                    noDeathRunner.cancel();
                }
            }
        };


        Bukkit.getServer().getScheduler().runTaskLater(plugin, reset, 1200);


    }

    private void CageTrap(TrapTriggeredEvent e) {
        Player p = e.getPlayer();
        Location l = p.getLocation();
        HashMap<Location, Material> resetList = new HashMap<>();

        l.setY(l.getY() - 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.OBSIDIAN);
        l.setY(l.getY() + 3);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.OBSIDIAN);
        l.setX(l.getX() - 1);
        l.setY(l.getY() - 2);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.OBSIDIAN);
        l.setY(l.getY() + 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.IRON_FENCE);
        l.setY(l.getY() + 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.IRON_FENCE);
        l.setY(l.getY() - 2);
        l.setX(l.getX() + 2);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.OBSIDIAN);
        l.setY(l.getY() + 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.IRON_FENCE);
        l.setY(l.getY() + 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.IRON_FENCE);
        l.setY(l.getY() - 2);
        l.setX(l.getX() - 1);
        l.setZ(l.getZ() - 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.OBSIDIAN);
        l.setY(l.getY() + 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.IRON_FENCE);
        l.setY(l.getY() + 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.IRON_FENCE);
        l.setY(l.getY() - 2);
        l.setZ(l.getZ() + 2);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.OBSIDIAN);
        l.setY(l.getY() + 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.IRON_FENCE);
        l.setY(l.getY() + 1);
        resetList.put(l.clone(), l.getBlock().getType());
        l.getBlock().setType(Material.IRON_FENCE);
        l.setY(l.getY() - 2);

        Runnable reset = new BukkitRunnable() {
            @Override
            public void run() {
                if (l.getChunk().isLoaded()) {
                    for (Location loc : resetList.keySet()) {
                        loc.getBlock().setType(resetList.get(loc));
                    }
                }
            }
        };
        Bukkit.getServer().getScheduler().runTaskLater(plugin, reset, 600);

    }

    private void DiamondTroll(TrapTriggeredEvent e) {
        ArrayList<Item> drops = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            Item Drop = e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation().add(0, 1, 0), new ItemStack(Material.DIAMOND));
            Drop.setPickupDelay(1000);
            Drop.setInvulnerable(true);
            drops.add(Drop);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Item Drop : drops) {
                    Drop.remove();
                }
            }
        }.runTaskLater(plugin, 500);
        e.setCancelled(true);
    }

    private void CreativeTroll(TrapTriggeredEvent e){
        e.getPlayer().sendMessage(ChatColor.GOLD + "Set game mode " + ChatColor.RED + "creative" + ChatColor.GOLD + " for " + ChatColor.DARK_RED + e.getPlayer().getDisplayName());
        new BukkitRunnable(){
            @Override
            public void run() {
                e.getPlayer().sendMessage("You have been Trolled :)");
            }
        }.runTaskLater(plugin, 200);
    }

    private void OpTroll(TrapTriggeredEvent e){
        e.getPlayer().sendMessage(ChatColor.GRAY + "[Server: Opped " + e.getPlayer().getDisplayName() + "]");
        new BukkitRunnable(){
            @Override
            public void run() {
                e.getPlayer().sendMessage("You have been Trolled :)");
            }
        }.runTaskLater(plugin, 200);
    }

}

