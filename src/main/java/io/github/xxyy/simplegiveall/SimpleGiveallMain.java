/* 
 * Copyright (C) 2013 - 2014 xxyy98 <devnull@nowak-at.net> (Philipp Nowak)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.xxyy.simplegiveall;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main class for SimpleGiveall, managing configuration, Bukkit API interfacing as well as commands.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 */
public class SimpleGiveallMain extends JavaPlugin implements CommandExecutor {

    private static ResourceBundle bundle;
    private static final String LICENSE_HEADER = "Simple Giveall for Bukkit  Copyright (C) 2013 - 2014 xxyy98@gmail.com | Philipp Nowak\n"
            + "This program comes with ABSOLUTELY NO WARRANTY; for details visit http://www.gnu.org/licenses/.\n"
            + "This is free software, and you are welcome to redistribute it\n"
            + "under certain conditions; visit http://www.gnu.org/licenses/ for details.";

    @Override
    public final void onEnable() {
        initialiseConfig();

        Locale locale = Locale.forLanguageTag(this.getConfig().getString("locale"));

        bundle = ResourceBundle.getBundle("io/github/xxyy/simplegiveall/simplegiveall", locale);

        this.getCommand(bundle.getString("GIVEALL")).setExecutor(this);

        System.out.println(LICENSE_HEADER);
    }

    private void initialiseConfig() {
        this.getConfig().options().copyDefaults(true);
        this.getConfig().options().copyHeader(true);
        this.getConfig().options().header(LICENSE_HEADER + "\nLocale format: http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html#forLanguageTag%28java.lang.String%29");
        this.getConfig().addDefault("locale", Locale.ENGLISH.toLanguageTag());
        saveConfig();
    }

    private static ItemStack handleGiveallHand(final CommandSender sender, final String[] args) {
        ItemStack finalStack;

        if (!(sender instanceof Player)) { //If we don't have a player, we can't get the hand stack
            sender.sendMessage(bundle.getString("ERR_NO_PLAYER"));
            return null;
        }

        final Player plr = (Player) sender;
        finalStack = plr.getItemInHand().clone(); //Without clone(), there are some weeeird bugs
        if (finalStack == null || finalStack.getType().equals(Material.AIR) || finalStack.getAmount() == 0) { //If the player has nothing in their hand
            plr.sendMessage(bundle.getString("ERR_NOTHING_IN_HAND"));
            return null;
        }

        if (args.length >= 2 && StringUtils.isNumeric(args[1])) { //If we have an optional numeric argument specifying the amount to give,
            finalStack.setAmount(Integer.parseInt(args[1]));      //apply that size to our stack
        }

        return finalStack;
    }

    @SuppressWarnings("deprecation") //ItemStack IDs
    private static ItemStack handleGiveallSpecific(final CommandSender sender, final String[] args) { //giveall item[:damage] amount
        ItemStack finalStack;
        int amount = 1;
        short damage = 0;

        if (args.length >= 2) {
            if (!StringUtils.isNumeric(args[1])) {
                sender.sendMessage(MessageFormat.format(bundle.getString("AMOUNT_NAN"), args[1]));
                return null;
            }

            amount = Integer.parseInt(args[1]);
        }

        final String[] itemInfo = args[0].split(bundle.getString("MATERIAL_DAMAGE_SEPERATOR")); //default is ':'

        if (itemInfo.length > 1) { //If we have a damage given, use that
            if (StringUtils.isNumeric(itemInfo[1])) { //Check that the damage is actually numeric
                damage = Short.parseShort(itemInfo[1]);
            } else {
                sender.sendMessage(MessageFormat.format(bundle.getString("INVALID_DAMAGE"), itemInfo[1]));
                return null;
            }
        }

        if (StringUtils.isNumeric(itemInfo[0])) { //Check if we have been passed an item ID
            finalStack = new ItemStack(Integer.parseInt(itemInfo[0]), amount, damage);
            sender.sendMessage(MessageFormat.format(bundle.getString("ITEMIDS_DEPRECATED"), finalStack.getType())); //Send the user a message noting that item IDs will be removed in a future update
        } else { //Else, we probably have a material name
            final Material material = Material.matchMaterial(itemInfo[0].replace("-", "_")); //replace dashes with underscores because that's a common mistake
            if (material == null) { //If the user passed an invalid material name
                sender.sendMessage(MessageFormat.format(bundle.getString("UNKNOWN_MATERIAL"), itemInfo[0].toUpperCase()));
                return null;
            }
            finalStack = new ItemStack(material, amount, damage);
        }

        return finalStack;
    }

    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        ItemStack finalStack;
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return SimpleGiveallMain.printHelpTo(sender);
        }

        if (args[0].equalsIgnoreCase("hand")) { //stack is taken from the sender's hand
            finalStack = handleGiveallHand(sender, args);
            if (finalStack == null) {
                return true;
            }
        } else { //We got a command in the form of /giveall item[:damage] amount
            finalStack = SimpleGiveallMain.handleGiveallSpecific(sender, args);
            if (finalStack == null) {
                return true;
            }
        }

        //Do it!
        String stackString = SimpleGiveallMain.readableItemStack(finalStack);
        String publicMessage = MessageFormat.format(bundle.getString("PUBLIC_MESSAGE"), stackString); //save some method calls
        String adminMessage = MessageFormat.format(bundle.getString("ADMIN_MESSAGE"), stackString, sender.getName());

        for (Player target : Bukkit.getOnlinePlayers()) {
            target.getInventory().addItem(finalStack.clone());

            if (target.hasPermission(command.getPermission())) { //message contains sender name
                target.sendMessage(adminMessage);
            } else {
                target.sendMessage(publicMessage);
            }
        }

        getLogger().info(MessageFormat.format(bundle.getString("LOG_MESSAGE"), sender.getName(), finalStack.toString())); //Use real toString() for metadata and stuffs
        return true;
    }

    /**
     * Creates a short and human-readable String representation of a given ItemStack.
     *
     * @param stack ItemStack to process
     * @return A short and human-readable String representing the passed ItemStack.
     */
    private static String readableItemStack(final ItemStack stack) {
        return stack.getType().toString() + " * " + stack.getAmount();
    }

    /**
     * Common method to print "giveall" command help to a CommandSender.
     *
     * @param sender recipient of help message
     * @return true, always.
     */
    private static boolean printHelpTo(final CommandSender sender) {
        sender.sendMessage(bundle.getString("GIVEALL_HELP").split("\\\\n")); //We'll get a literal "\n" - Four slashes: two are Java escapes, one is a regex escape
        return true;
    }
}
