This is prob NOT professional at all xd
Here I will take notes and some ideas, etc. about this plugin for a better organization

Here are all the commands we have, with their description, algorithms, etc., things that I may consider important to note:
# A comprenhensive list of all commands (/)
* spawn
* fly
* giverandomitem
* home
* homes
* sethome
* delhome
* broadcast
* clearbroadcast
* starter
* help
* giveci
* profile

## Commands

**spawn**: Teleports the user to the world spawn
**fly**: Toggles the player flight mode
**giverandomitem**: Gives a random item (ignoring legacy or OP items or other specified ones in config) to the player or the target player(s) specified (admin)
**home**: Teleports the player to their home or specified one
**homes**: Lists all of player's homes with coordinates
**sethome**: Sets a home with a given name (default to home) in the player locations (up to 5 homes)
**delhome**: Deletes the specified home from the player's homes
**broadcast**: Broadcasts a message to the entire server with a preffix (in config) (color codes &) (admin)
**clearbroadcast**: Broadcast a message without preffix to the entire server (color codes &) (admin)
**starter**: Gives the starting set of leather armor, stone tools and 16 steak to the player, cooldown of 30 seconds
**help**: Lists all of these commands and the server ip, website, and other things that I may add (config)
**givci**: Gives the player or specified player target a Custom Item (plugin defined), I have like CA_Wand that opens the ChunkAnalyzer menu for the moment its a plugin that shows the load of a chunk and allows to tp to check lag etc (admin)
**profile**: Opens a GUI that shows the player head in the center with lore of: level (no function, just default to 1 level, 0 xp, and 50xp goal or smth, to the db, no actual event or smth to change it), kills (dynamic and cache and on db), deaths (dynamic and cache on db)

## Profile
This represents a player's profile, it has the following stats:
Xp, Xp goal, Level, Balance (Money), Kills, Deaths, 

## Profile GUI
This opens when a player runs /profile, with an optional player name argument, async check to cache else to db.

Shows a big gui, just the player head in the center for the moment that has:
Name: Player name
Lore: 
- Kills
- Deaths
- Level \#Nmbr: PROGRESSBAR prcnt%
- Experience remaining for next level

## Scoreboard
For the moment shows:
- (date in d/m/y)
- 
- Playtime: Dd Hh Mm Ss # Updates every second
- Level: level (xp xp/ xpgoal xp) # Updates on chage
- Kills: kills # Updates on change
- Deaths: deaths # Updates on chage
- 
- www.desertuo.com

Simple methods and atttributes used, not implementing the logic in the doc xd:

public class ScoreboardCO implements Runnable {}
private final static DesertUo plugin = DesertUo.getPlugin();  
private final static ScoreboardCO instance = new ScoreboardCO();  

LuckPerms lpApi = plugin.getLpApi();

public final HashMap<UUID, Scoreboard> playerScoreboards = new HashMap<>();  
public Map<UUID, PlayerProfileCO> playerCache = new HashMap<>();  
private ScoreboardCO() {}  
  
@Override  
public void run() {  
for(Player player : Bukkit.getOnlinePlayers()) { ... }
public Scoreboard getPlayerScoreboard(UUID uuid) {}
public void createNewScoreboard(Player player) {}
public void setPlayerScoreboardSidebarLine(UUID uuid, Component s, int line) {}
public Scoreboard getAndCreateIfNullPlayerScoreboard(UUID uuid) {}
public void updateScoreboard(Player player) {}
public static ScoreboardCO getInstance() { return instance; }
## MongoDB database
DB: desertuo
Cluester: desertuo_servers (as in the mc server)
Collections:
- desertuo (idk, empty)
- player_data:
	- Doc:
		- id
		- uuid
		- name
		- level
		- level-xp
		- level-xp-goal (im planning to remove this for a runtime xp goal function that depends on the level)
		- kills
		- deaths
		- homes:
			- nmbr: # Functionally in the plugin it lets 5 maximum
				- name
				- x
				- y
				- z
		- last_login
- player_messages (no function, will prob add later)
- player_reports (no function, will prob add later)

## Website
At www.desertuo.com or desertuo.com:
\<h1\>DesertUo\<\/h1\>
\<center\>Landing page\<\/center>
\<center\>Work in progress\<\/center>
Doesn't have much setup apart from the pay of the hosting and domain, and the ftp for me to connect with my filezilla on my laptop, just a simple website it appears default to PHP for some reason, only file on www folder: index.html

## Additions
**Chat messages**: Using luckperms the messages is like "\[Level\] PreffixUserSuffix: message"
\[...\] IDK, Custom on AsyncChatEvent, it cancels it and sends a custom Component message to every player and console

## Levels
Changed it to make it "functional"
Know there's a simple level and xp system that I feel it's the base block of the gradious thing:
Xp goal is calculated at runtime (uses and exponential curve), start defaul level at 1.
For the moment you gain xp by killing other players, breaking blocks (diamonds 200, and other ores bellow, etc, the rest of blocks just 1 xp), or by passing time (25xp every minute).
The addXp\(\) function checks if you have more xp than the required for the next level in a while loop and runs the leveling up thing of message, sound, profile cache, etc.
In profile it shows a level, progress bar, and xp remaining for next level