package de.rayzs.wearanything.plugin.loader;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import de.rayzs.wearanything.plugin.utils.stacks.BukkitWearArmorStack;
import de.rayzs.wearanything.plugin.listener.PlayerListener;
import de.rayzs.wearanything.plugin.WearAnything;
import de.rayzs.wearanything.plugin.utils.*;
import org.bukkit.plugin.java.JavaPlugin;
import de.rayzs.wearanything.api.*;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.command.*;
import java.util.*;

public class WearAnythingBukkitLoader extends JavaPlugin {

    @Override
    public void onEnable() {

        ConfigurationSerialization.registerClass(BukkitWearArmorStack.class);
        WearAnything.getInstance().initialize(getLogger(), BukkitWearArmorConverter.getInstance(), new BukkitConfigurationBuilder("items"));

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        /*
        Yes, I am registering the commands here because I was too lazy to implement them in separate classes.
        Let's just keep them this way at first and maybe change it in future. ;)
         */

        getCommand("wearanythingreload").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
                sender.sendMessage("§eReloading...");
                WearAnything.getInstance().loadAll();
                sender.sendMessage("§aDone!");
                return true;
            }
        });


        PluginCommand deleteCommand = getCommand("wearanythingdelete");
        PluginCommand createCommand = getCommand("wearanythingcreate");

        deleteCommand.setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                ArrayList<String> suggestions = new ArrayList<>();
                int length = args.length;

                if (length == 1) {
                    suggestions.addAll(WearAnything.getInstance().getArmorNames());
                }

                return suggestions.stream().filter(suggestion -> suggestion.toLowerCase().startsWith(args[length - 1].toLowerCase())).collect(Collectors.toList());
            }
        });

        deleteCommand.setExecutor((sender, command, label, args) -> {

            if (args.length != 1) {
                sender.sendMessage("§cUsage: /wearanythingdelete <name>");
                return true;
            }

            String name = args[0].toLowerCase();
            try {
                if (WearAnything.getInstance().exist(name)) {
                    WearAnything.getInstance().remove(name);
                    sender.sendMessage("§aDeleted armor slot named " + name);
                } else
                    sender.sendMessage("§cNot found!");
            } catch (Exception exception) {
                sender.sendMessage("§cSomething went wrong!");
            }

            return true;
        });


        createCommand.setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                ArrayList<String> suggestions = new ArrayList<>();
                int length = args.length;

                if (length == 2) {
                    suggestions.addAll(Arrays.stream(ArmorEnum.values()).map(type -> type.name().toLowerCase()).toList());
                }

                return suggestions.stream().filter(suggestion -> suggestion.toLowerCase().startsWith(args[length - 1].toLowerCase())).collect(Collectors.toList());
            }
        });

        createCommand.setExecutor((sender, command, label, args) -> {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cYou must be a player!");
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage("§cUsage: /wearanythingcreate <name> <armor-slot>");
                return true;
            }

            ItemStack stack = player.getItemInHand();
            if (stack.getType().isAir()) {
                sender.sendMessage("§cNo item in hand!");
                return true;
            }

            try {
                String name = args[0].toLowerCase();
                ArmorEnum armorType = ArmorEnum.valueOf(args[1].toUpperCase());
                WearArmor wearArmor = WearAnything.getInstance().getConverter().convert(
                        name, armorType, stack,
                        WearAnything.getInstance().getDefaultSound(),
                        (float) WearAnything.getInstance().getDefaultSoundVolume(),
                        (float) WearAnything.getInstance().getDefaultSoundPitch()
                );

                WearAnything.getInstance().save(wearArmor);
                sender.sendMessage("§aDone!");

            } catch (Exception exception) {
                sender.sendMessage("§cSomething went wrong. Possible armor slots are: (BOOT, LEGGING, CHESTPLATE, HELMET)");
            }

            return true;
        });
    }

    @Override
    public void onDisable() {

    }
}
