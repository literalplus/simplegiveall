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

/**
 *
 * @author xxyy98 < xxyy98@gmail.com >
 */
public class SimpleGiveallMain extends JavaPlugin implements CommandExecutor {

    @Override
    public final void onLoad() {
        this.getCommand("giveall").setExecutor(this);
    }

    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        ItemStack finalStack;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("hand")) {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("§cDas kannst du nur als Spieler ausführen. Hättest du etwas anderes erwartet?");
                    return true;
                }
                final Player plr = (Player) sender;
                finalStack = plr.getItemInHand().clone(); //Without clone(), there are some weeeird bugs
                if (finalStack == null || finalStack.getType().equals(Material.AIR) || finalStack.getAmount() == 0) {
                    plr.sendMessage("§cDu hast nichts in der Hand!");
                    return true;
                }
                if (args.length >= 2 && StringUtils.isNumeric(args[1])) {
                    finalStack.setAmount(Integer.parseInt(args[1]));
                }
            } //argument 0 is not "hand"
            else if (args[0].equalsIgnoreCase("help")) {
                return SimpleGiveallMain.printHelpTo(sender);
            } //argument 0 is not "help"
            else {
                if (args.length >= 2) {
                    String[] itemInfo = args[0].split(":");
                    short damage = 0;
                    if (!StringUtils.isNumeric(args[1])) {
                        sender.sendMessage("§cDie An§lzahl§c ist keine Zahl: §4" + args[1]);
                        return true;
                    }
                    if (itemInfo.length > 1) {
                        if (StringUtils.isNumeric(itemInfo[1])) {
                            damage = Short.parseShort(itemInfo[1]);
                        }
                        else {
                            sender.sendMessage("§cInvalide Damagevalue/Invalide Metadata: " + itemInfo[1]);
                            return true;
                        }
                    }
                    if (StringUtils.isNumeric(itemInfo[0])) {
                        finalStack = new ItemStack(Integer.parseInt(itemInfo[0]), Integer.parseInt(args[1]), damage); //I KNOW it's deprecated but users would want this
                    }
                    else {
                        final Material material = Material.matchMaterial(itemInfo[0].replace("-", "_"));
                        if (material == null) {
                            sender.sendMessage("§cDieses Material ist unbekannt: §4" + itemInfo[0].toUpperCase());
                            return true;
                        }
                        finalStack = new ItemStack(material, Integer.parseInt(args[1]), damage);
                    }
                }
                else {
                    return SimpleGiveallMain.printHelpTo(sender);
                }
            }

            //Let's do it, shall we?

            final String publicMessage = "§3Alle Spieler haben §6" + SimpleGiveallMain.getISString(finalStack) + " §3erhalten."; //save some method calls
            final String adminMessage = "§3Alle Spieler haben §6" + SimpleGiveallMain.getISString(finalStack) + " §3erhalten. §7§o(von " + sender.getName() + ")";
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.getInventory().addItem(finalStack);

                if (target.hasPermission(command.getPermission())) { //message contains sender name
                    target.sendMessage(adminMessage);
                }
                else {
                    target.sendMessage(publicMessage);
                }
            }
            Bukkit.getConsoleSender().sendMessage(adminMessage); //logging and stuff
        }
        else {
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
        sender.sendMessage(new String[] {"§e/giveall hand <Anzahl>\n§e/giveall [ITEM_NAME]:<Damage> [Anzahl]",
                                         "§e/giveall [Item-ID]:<Damage>"});
        return true;
    }
}
