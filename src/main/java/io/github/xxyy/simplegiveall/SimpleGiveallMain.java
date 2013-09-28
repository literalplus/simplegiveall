/* 
 * Copyright (C) 2013 xxyy98 < xxyy98@gmail.com > (Philipp Nowak)
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

import java.text.MessageFormat;
import java.util.ResourceBundle;
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

//Let's do it, shall we?
/**
 *
 * @author xxyy98 < xxyy98@gmail.com >
 */
public class SimpleGiveallMain extends JavaPlugin implements CommandExecutor {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("io/github/xxyy/simplegiveall/simplegiveall");

    @Override
    public final void onLoad() {
        this.getCommand(BUNDLE.getString("GIVEALL")).setExecutor(this);

        System.out.println("Simple Giveall for Bukkit  Copyright (C) 2013 xxyy98@gmail.com | Philipp Nowak\n"
                + "This program comes with ABSOLUTELY NO WARRANTY; for details visit http://www.gnu.org/licenses/.\n"
                + "This is free software, and you are welcome to redistribute it\n"
                + "under certain conditions; visit http://www.gnu.org/licenses/ for details.");
    }

    private static ItemStack handleGiveallHand(final CommandSender sender, final String[] args) {
        ItemStack finalStack;
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(BUNDLE.getString("ERR_NO_PLAYER"));
            return null;
        }
        final Player plr = (Player) sender;
        finalStack = plr.getItemInHand().clone(); //Without clone(), there are some weeeird bugs
        if (finalStack == null || finalStack.getType().equals(Material.AIR) || finalStack.getAmount() == 0) {
            plr.sendMessage(BUNDLE.getString("ERR_NOTHING_IN_HAND"));
            return null;
        }
        if (args.length >= 2 && StringUtils.isNumeric(args[1])) {
            finalStack.setAmount(Integer.parseInt(args[1]));
        }
        return finalStack;
    }

    @SuppressWarnings("deprecation") //I know it's bad, but users will want it (ItemStack w/ id)
    private static ItemStack handleGiveallSpecific(final CommandSender sender, final String[] args) {
        ItemStack finalStack;
        if (args.length >= 2) {
            final String[] itemInfo = args[0].split(BUNDLE.getString("MATERIAL_DAMAGE_SEPERATOR"));
            short damage = 0;
            if (!StringUtils.isNumeric(args[1])) {
                sender.sendMessage(MessageFormat.format(BUNDLE.getString("AMOUNT_NAN"), new Object[] {args[1]}));
                return null;
            }
            if (itemInfo.length > 1) {
                if (StringUtils.isNumeric(itemInfo[1])) {
                    damage = Short.parseShort(itemInfo[1]);
                } else {
                    sender.sendMessage(MessageFormat.format(BUNDLE.getString("INVALID_DAMAGE"), new Object[] {itemInfo[1]}));
                    return null;
                }
            }
            if (StringUtils.isNumeric(itemInfo[0])) {
                finalStack = new ItemStack(Integer.parseInt(itemInfo[0]), Integer.parseInt(args[1]), damage); //Should we just print the message and then leave? ->usability
                sender.sendMessage(MessageFormat.format(BUNDLE.getString("ITEMIDS_DEPRECATED"), finalStack.getType()));
            } else {
                final Material material = Material.matchMaterial(itemInfo[0].replace("-", "_"));
                if (material == null) {
                    sender.sendMessage(MessageFormat.format(BUNDLE.getString("UNKNOWN_MATERIAL"), new Object[] {itemInfo[0].toUpperCase()}));
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
            final String publicMessage = MessageFormat.format(BUNDLE.getString("PUBLIC_MESSAGE"), new Object[] {SimpleGiveallMain.getISString(finalStack)}); //save some method calls
            final String adminMessage = MessageFormat.format(BUNDLE.getString("ADMIN_MESSAGE"), new Object[] {SimpleGiveallMain.getISString(finalStack), sender.getName()});

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
     * @param stack ItemStack to stringify
     *
     * @return A short String representation of stack in the form of MATERIAL * amount
     */
    private static String getISString(final ItemStack stack) {
        return stack.getType().toString() + " * " + stack.getAmount();
    }

    /**
     * Common method to print "giveall" command help to a CommandSender.
     *
     * @param sender Who needs help?
     *
     * @return Always true, for directly returing in COmmandExecutors.
     */
    private static boolean printHelpTo(final CommandSender sender) {
        sender.sendMessage(new String[] {BUNDLE.getString("HELP_LINE_1"),
                                         BUNDLE.getString("HELP_LINE_2")});
        return true;
    }
}
