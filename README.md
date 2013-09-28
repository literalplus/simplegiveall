Simple Giveall
=============

A very simple Bukkit plugin that gives all online players one or more item(s).

Commands
---------
Currently, there is only one command:
*/giveall*: _Gives all online players a specified item stack.
  _Aliases:_ xyga, ga
  _Usage 1:_ /giveall hand [amount] *Gives all online players the item in your hand, with specified amount.*
  _Usage 2:_ /giveall [ITEM_NAME] [amount] *Gives all online players an item by name (see Bukkit's Material) and amount.
  _Usage 3:_ /giveall [item id] [amount] *Gives all online players an item by id (do not rely on this, may be removed in future _Minecraft_ releases) and amount.

Permissions
------------
Well, this one is quite simnple, too.
*xy.giveall.use*: Allows to use /giveall and also see who executed it instead of just what has been given.

Contributing
-------------
If you have some enhancements, feel free to submit a pull request.

Building
---------
For Building, use Apache Maven.

License
--------
    Simple Giveall for Bukkit  Copyright (C) 2013 xxyy98@gmail.com | Philipp Nowak
    This program comes with ABSOLUTELY NO WARRANTY; for details visit http://www.gnu.org/licenses/.
    This is free software, and you are welcome to redistribute it
    under certain conditions; visit http://www.gnu.org/licenses/ for details.