This project consists on an advanced Skyblock core made to scale massively.

To start, the plugin requires a SQL connection, used to store the islands and all the player data

SETUP INSTRUCTIONS:
- Load the plugin into an empty server
- Add FastAsyncWorldEdit
- Set the SQL database info into the config.yml file
- Restart
- Configure to your liking


Technical Aspects:

Each player currently occupies ~32.5KB, with each island occupying ~4MB max on the SQL database.
This means that group islands are heavily optimized, as each player stores an Island UUID, which is then referenced to get the Island itself.

All storage operations are done async (Island saving, pasting and regenerating) when possible.

Player inventories are stored via SQL aswell, meaning you can have 20 servers hosting this same plugin connected to the same database and it will seem as one.

Now for RAM / DISK usage on the instance itself, I'd say around 2GB DISK and 2GB RAM

The 2GB DISK is due to the loaded blocks / lobbies etc that you might have, aswell as plugins that you might want to run, since all the islands
are unloaded and saved to SQL once all the players leave the server.

The 2GB RAM is enough to hold all your players / islands, and is a vague measurement, you should increase if you're having issues, but generally it is fine.
