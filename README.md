This project consists on an advanced Skyblock core made to scale massively.

To start, the plugin requires a SQL connection, used to store the islands and all the player data
If you do not have/need a SQL connection, change the storage type to SQLITE on the config.yml file

**SETUP INSTRUCTIONS**:
- Load the plugin into an empty server
- Set the SQL database info into the config.yml file (if not SQLite)
- Restart
- Configure to your liking

**If on a proxy**:
- Put the plugin on the plugins folder
- Set the SQL database info into the bungee-config.yml file (note that instances should also be running on MySQL)
- Restart

**If you're running multiple proxies (Requires Redis)**:
- Put the plugin on each proxy and instance
- Set them all to use the same MySQL database
- On each proxy, also set the Redis database details
- Restart all proxies (If you don't have jedis, you're going to have to restart twice)

**Technical Aspects**:

Each player currently occupies ~32.5KB max, with each island occupying ~4MB max on the SQL database.
This means that group islands are heavily optimized, as each player stores an Island UUID, which is then referenced to get the Island itself.

All storage operations are done async (Island saving, pasting and regenerating) when possible.

Player inventories are stored via SQL aswell, meaning you can have 20 servers hosting this same plugin connected to the same database and it will seem as one.

Now for RAM / DISK usage on the instance itself, I'd say around 2GB DISK and 2GB RAM per instance

The islands are either saved in .mca (Mojang's format) or in .schematic (FAWE), format set in island-config.yml 

Islands have their own worlds, which are loaded on demand

**Technologies Used**
- SpigotAPI
- MySQL
- SQLite
- Gradle
- Bungeecord
- Redis

**Plugin Hooks**
- Vault
- FastAsyncWorldEdit
