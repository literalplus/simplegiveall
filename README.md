Simple Giveall [![Build Status](http://ci.nowak-at.net/job/public~Simple%20Giveall/badge/icon)](http://ci.nowak-at.net/job/public~Simple%20Giveall/)
=============

A very simple Bukkit plugin that gives all online players one or more item(s).

Commands
---------
Currently, there is only one command: <br>
__/giveall__: _Gives all online players a specified item stack._<br>
  Aliases: _xyga, ga_<br>
  Usage 1: __/giveall hand &lt;amount&gt;__ Gives all online players the item in your hand, with specified amount.<br>
  Usage 2: __/giveall &lt;ITEM_NAME&gt; &lt;amount&gt;__ Gives all online players an item by name (see Bukkit's Material) and amount. <br>
  Usage 3: __/giveall &lt;item id&gt; &lt;amount&gt;__ Gives all online players an item by id (do not rely on this, may be removed in future _Minecraft_ releases) and amount. <br>

Permissions
------------
Well, this one is quite simple, too.
*xy.giveall.use*: Allows to use /giveall and also see who executed it instead of just what has been given.<br>

Contributing
-------------
If you have enhancements, pull requests are always appreciated! :)

Building
---------
For Building, use Apache Maven:
````
mvn clean package
````
If you want a pre-built copy, visit my [CI Server](http://server.nowak-at.net/jenkins/job/public~Simple%20Giveall/) or the GitHub releases.

Help
------
If you wish to receive support for this plugin or just want to talk, you can try [joining my IRC channel](http://irc.spi.gt/iris/?channels=lit)

License
--------
    Simple Giveall for Bukkit  Copyright (C) 2013 - 2014 xxyy98@gmail.com | Philipp Nowak
    This program comes with ABSOLUTELY NO WARRANTY; for details visit http://www.gnu.org/licenses/.
    This is free software, and you are welcome to redistribute it
    under certain conditions; visit http://www.gnu.org/licenses/ for details.
