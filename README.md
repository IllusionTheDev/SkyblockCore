This project consists on an advanced Skyblock core made to scale massively.

To start, the plugin requires a SQL connection, used to store the islands and all the player data

SETUP INSTRUCTIONS:
- Load the plugin into an empty server
- Set the SQL database info into the config.yml file
- Restart
- Configure to your liking

Technical Aspects:

Each player currently occupies ~32.5KB max, with each island occupying ~4MB max on the SQL database.
This means that group islands are heavily optimized, as each player stores an Island UUID, which is then referenced to get the Island itself.

All storage operations are done async (Island saving, pasting and regenerating) when possible.

Player inventories are stored via SQL aswell, meaning you can have 20 servers hosting this same plugin connected to the same database and it will seem as one.

Now for RAM / DISK usage on the instance itself, I'd say around 2GB DISK and 2GB RAM per instance

The islands are either saved in .mca (Mojang's format) or in .schematic (FAWE), format set in island-config.yml 

Islands have their own worlds

Worlds are loaded on-demand
