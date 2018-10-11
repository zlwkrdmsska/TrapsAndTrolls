package io.github.yehan2002.Traps;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public enum TrapManager {
    Fire, Lightning, Glow,TNT,Launch,Poison,Thief,Cage,Lava,Diamond,Herobrine,Creative,Op,Custom;

    public static String prefix = ChatColor.GREEN + "" + ChatColor.MAGIC + "$" + ChatColor.GREEN + " ";
    public static String suffix = " " + ChatColor.MAGIC + "$";
    public static ArrayList<String> trapList = new ArrayList<>();
    public static HashMap<String, String> trapDescription = new HashMap<>();
    public static HashMap<String, Integer> trapPrices = new HashMap<>();

    public static void registerTrap(String name, String desc, Integer price){
        name = prefix + name + suffix;
        trapList.add(name);
        trapDescription.put(name, desc);
        trapPrices.put(name, price);
    }


    public static TrapManager getTrap(String name){
        if (name.startsWith(prefix) && name.endsWith(suffix)){
            name = name.substring(prefix.length());
            name = name.substring(0, name.length() - suffix.length());
        } else {
            return null;
        }

        name = name.trim();

        switch (name){
            case "Fire Trap":
                return TrapManager.Fire;
            case "Lightning Trap":
                return TrapManager.Lightning;
            case "Glow Trap":
                return TrapManager.Glow;
            case "TNT Trap":
                return TrapManager.TNT;
            case "Launch Trap":
                return TrapManager.Launch;
            case "Poison Trap":
                return TrapManager.Poison;
            case "Thief Trap":
                return TrapManager.Thief;
            case "Cage Trap":
                return TrapManager.Cage;
            case "Lava Trap":
                return TrapManager.Lava;
            case "Fake Diamond Troll":
                return TrapManager.Diamond;
            case "Herobrine Troll":
                return TrapManager.Herobrine;
            case "Creative Mode Troll":
                return TrapManager.Creative;
            case "Fake Op troll":
                return TrapManager.Op;



        }

        return TrapManager.Custom;
    }

}
