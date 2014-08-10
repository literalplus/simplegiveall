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
import org.bukkit.command.ConsoleCommandSender;
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

    private static ResourceBundle bundle = null;
    private static final String LICENSE_HEADER = "Simple Giveall for Bukkit  Copyright (C) 2013 - 2014 xxyy98@gmail.com | Philipp Nowak\n"
            + "This program comes with ABSOLUTELY NO WARRANTY; for details visit http://www.gnu.org/licenses/.\n"
            + "This is free software, and you are welcome to redistribute it\n"
            + "under certain conditions; visit http://www.gnu.org/licenses/ for details.";

    @Override
    public final void onEnable() {
        initialiseConfig();

        Locale locale = Locale.forLanguageTag(this.getConfig().getString("locale"));

        if (bundle == null) {
            bundle = ResourceBundle.getBundle("io/github/xxyy/simplegiveall/simplegiveall", locale);
        }
        this.getCommand(bundle.getString("GIVEALL")).setExecutor(this);

        System.out.println(LICENSE_HEADER);
    }

    private void initialiseConfig() {
        this.getConfig().options().copyDefaults(true);
        this.getConfig().options().copyHeader(true);
        this.getConfig().options().header(LICENSE_HEADER + "\nLocale format: http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html#forLanguageTag%28java.lang.String%29");
        this.getConfig().addDefault("locale", Locale.ENGLISH.toLanguageTag());
    }

    private static ItemStack handleGiveallHand(final CommandSender sender, final String[] args) {
        ItemStack finalStack;
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(bundle.getString("ERR_NO_PLAYER"));
            return null;
        }
        final Player plr = (Player) sender;
        finalStack = plr.getItemInHand().clone(); //Without clone(), there are some weeeird bugs
        if (finalStack == null || finalStack.getType().equals(Material.AIR) || finalStack.getAmount() == 0) {
            plr.sendMessage(bundle.getString("ERR_NOTHING_IN_HAND"));
            return null;
        }
        if (args.length >= 2 && StringUtils.isNumeric(args[1])) {
            finalStack.setAmount(Integer.parseInt(args[1]));
        }
        return finalStack;
    }

    @SuppressWarnings("deprecation") //ItemStack IDs
    private static ItemStack handleGiveallSpecific(final CommandSender sender, final String[] args) {
        ItemStack finalStack;
        if (args.length >= 2) {
            final String[] itemInfo = args[0].split(bundle.getString("MATERIAL_DAMAGE_SEPERATOR"));
            short damage = 0;
            if (!StringUtils.isNumeric(args[1])) {
                sender.sendMessage(MessageFormat.format(bundle.getString("AMOUNT_NAN"), new Object[]{args[1]}));
                return null;
            }
            if (itemInfo.length > 1) {
                if (StringUtils.isNumeric(itemInfo[1])) {
                    damage = Short.parseShort(itemInfo[1]);
                } else {
                    sender.sendMessage(MessageFormat.format(bundle.getString("INVALID_DAMAGE"), new Object[]{itemInfo[1]}));
                    return null;
                }
            }
            if (StringUtils.isNumeric(itemInfo[0])) {
                finalStack = new ItemStack(Integer.parseInt(itemInfo[0]), Integer.parseInt(args[1]), damage); //Should we just print the message and then leave? ->usability
                sender.sendMessage(MessageFormat.format(bundle.getString("ITEMIDS_DEPRECATED"), finalStack.getType()));
            } else {
                final Material material = Material.matchMaterial(itemInfo[0].replace("-", "_"));
                if (material == null) {
                    sender.sendMessage(MessageFormat.format(bundle.getString("UNKNOWN_MATERIAL"), new Object[]{itemInfo[0].toUpperCase()}));
                    return null;
                }
                finalStack = new ItemStack(material, Integer.parseInt(args[1]), damage);
            }
        } else {
            SimpleGiveallMain.printHelpTo(sender);
            return null;
        }
        return finalStack;
    }

    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        ItemStack finalStack;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("hand")) {
                finalStack = handleGiveallHand(sender, args);
                if (finalStack == null) {
                    return true;
                }
            } else {  //argument 0 is not "hand"
                if (args[0].equalsIgnoreCase("help")) {
                    return SimpleGiveallMain.printHelpTo(sender);
                } else { //argument 0 is not "help"
                    finalStack = SimpleGiveallMain.handleGiveallSpecific(sender, args);
                    if (finalStack == null) {
                        return true;
                    }
                }
            } // not hand

            //Do it!
            final String publicMessage = MessageFormat.format(bundle.getString("PUBLIC_MESSAGE"), SimpleGiveallMain.getISString(finalStack)); //save some method calls
            final String adminMessage = MessageFormat.format(bundle.getString("ADMIN_MESSAGE"), SimpleGiveallMain.getISString(finalStack), sender.getName());

            for (Player target : Bukkit.getOnlinePlayers()) {
                target.getInventory().addItem(finalStack);

                if (target.hasPermission(command.getPermission())) { //message contains sender name
                    target.sendMessage(adminMessage);
                } else {
                    target.sendMessage(publicMessage);
                }
            }

            Bukkit.getConsoleSender().sendMessage(adminMessage); //logging and stuff

        } else {
            SimpleGiveallMain.printHelpTo(sender);
        }
        return true;
    }

    /**
     * Creates a short and human-readable String representation of a given ItemStack.
     *
     * @param stack ItemStack to process
     * @return A short and human-readable String representing the passed ItemStack.
     */
    private static String getISString(final ItemStack stack) {
        return stack.getType().toString() + " * " + stack.getAmount();
    }

    /**
     * Common method to print "giveall" command help to a CommandSender.
     *
     * @param sender recipient of help message
     * @return true, always.
     */
    private static boolean printHelpTo(final CommandSender sender) {
        sender.sendMessage(new String[]{bundle.getString("HELP_LINE_1"),
                bundle.getString("HELP_LINE_2")});
        return true;
    }
}
