This project consists on an advanced Skyblock core made to scale massively.

To start, the plugin requires a MySQL connection, used to store the islands and all the player data

SETUP INSTRUCTIONS:
- Load the plugin into an empty server
- Add FastAsyncWorldEdit
- Set the MySQL database info into the config.yml file
- Restart
- Configure to your liking


Technical Aspects:

Each player currently occupies 8mb of disk space on the MySQL update.
I am currently developing an update that will shrink the size to 4mb per island + 32kb per player.
This is done to shrink disk space usage, as 1M unique players would previously consume 8TB, but now it is anywhere from 500gb - 4TB

All storage operations are done async (Island saving, pasting and regenerating) when possible.

Player inventories are stored via SQL aswell, meaning you can have 20 servers hosting this same plugin connected to the same database and it will seem as one.
