<h1 align="center"><img height="35" src="https://emoji.gg/assets/emoji/7333-parrotdance.gif"> SkyblockCore</h1>
<div align="center">

![GitHub Repo stars](https://img.shields.io/github/stars/IllusionTheDev/SkyblockCore?style=for-the-badge) 
![GitHub watchers](https://img.shields.io/github/watchers/IllusionTheDev/SkyblockCore?style=for-the-badge) 
![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/IllusionTheDev/SkyblockCore?include_prereleases&style=for-the-badge) 
![GitHub all releases](https://img.shields.io/github/downloads/IllusionTheDev/SkyblockCore/total?style=for-the-badge) 
![GitHub issues](https://img.shields.io/github/issues/IllusionTheDev/SkyblockCore?style=for-the-badge)

</div>

Hey, this project has been archived. I might be considering a project rewrite soon, as I've learned a lot about sharding during the past 2 years. Feel free to contact me on Discord (my tag is available on my profile) if you'd like to discuss sharding for any network, as it is something I still have fun doing.

#### This project consists on an advanced Skyblock core made to scale massively.

To start, the plugin requires a SQL connection, used to store the islands and all the player data If you do not have/need a SQL connection, change the storage type to SQLITE on the config.yml file

Best paired with my [DataSync Plugin](https://github.com/IllusionTheDev/DataSync), which handles inventory, food, enderchest, potion synchronization and more!

------------

### Known issues:
- Default .mca file is 1.17.1+ (Possible fix: Start the server on 1.17.1, replace start-schematic/r.0.0.mca file with a new island schematic, delete all worlds and change versions)
- FAWE doesn't work (Has to do with versioning)

I am aware of both issues, and aim to release fixes soon, please do not annoy me about them.

------------

### Setup instructions:
- Load the plugin into an empty server
- Set the storage database info into the config.yml file (Available types: Amazon S3, MySQL, SQLite, MongoDB, Flat-file (creates multiple files on a folder))
- Restart
- Configure to your liking

#### If you're running SlimeWorldManager:
- Ensure that SlimeWorldManager saves to `file`, as SkyblockCore handles world serialization automatically
- Set the pasting type to SLIME, otherwise things will break
- Restart
- Test thoroughly

#### If on a proxy:
- Put the plugin on the proxy's plugins folder, as well as set-up each instance with the steps above.
- Set the storage info into the bungee-config.yml file (note that instances should also be running on the same database, file-based databases are naturally unsupported in bungee mode)
- Restart

#### If you're running multiple proxies (Requires Redis):
- Put the plugin on each proxy and instance
- Set them all to use the same database
- On each proxy, also set the communication database details (Redis by default, RabbitMQ and a native Socket-based system coming soon)
- Restart all proxies (If you don't have database drivers, you're going to have to restart twice)

------------

## Technical Aspects:

Now available on the [Wiki](https://github.com/IllusionTheDev/SkyblockCore/wiki#technical-aspects)!


------------

### Technologies Used:
- SpigotAPI
- MySQL
- SQLite
- Gradle
- Bungeecord
- Redis
- MongoDB

#### Plugin Hooks
- Vault
- FastAsyncWorldEdit
- SlimeWorldManager


------------

### Some thoughts:
SkyblockCore is incredibly difficult to develop, unlike a regular core, there is a lot of thought put into every line of code, the plugin is supposed to expect players switching across servers, proxies, players leaving mid-loading, different proxies for the same team members, all of it while handling all of the commands on the spigot side, so players on a survival server can't run skyblock commands.

It might not seem like it, but this project is absolutely massive on its current state, and I'd say it's about 20% done.

If you'd like to help with the project, even as someone without any code experience, here are some things that would greatly help me out:

- Testing
- Helping with the wiki
- Writing issues
- Planning
- Donating (purely your choice, there's no benefit other than making me happy)
