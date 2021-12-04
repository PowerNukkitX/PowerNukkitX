# Thank you
Thank your for your interest in contributing with PowerNukkit.

We really need your help by testing, finding and reporting bugs and compatibility issues,
suggesting features, and submitting pull requests with enhancements and bug-fixes.

## How to upgrade PowerNukkit to the next Minecraft protocol version
First thing first, we need to know what exactly has changed to the protocol.

### Finding what changed
- Check https://wiki.vg/Bedrock_Protocol, it's usually outdated, but maybe will be up-to-date in future.
- Check https://bedrock.dev/docs/stable/Addons, it has useful information about ids.
  You can get the block id out of an item id. If the item id is less than `255`, that item represents a block.
  If the id is between `0` and `255`, you can use that id as block id. If the id is **negative**, you can
  get the real block id with this formula: `255 - itemId`, for example, if the item id is `-4`,
  then we do `255 - (-4)` which is equals to `255 + 4` = `259`, another valid formula and easier for humans
  is `255 + |itemId|`, so we would do `255 + |-4|` = `255 + 4` = `259`.
- Check for changes in https://github.com/CloudburstMC/Protocol/commits/develop
- Check for changes in https://github.com/CloudburstMC/Network
- Check for changes in https://github.com/CloudburstMC/ProxyPass
- Check for changes in https://github.com/pmmp/PocketMine-MP/commits/stable
- Check for changes in https://github.com/JSPrismarine/JSPrismarine/commits/master
- The links above can help you to determine what have changed to the protocol, we are not racing or disputing
  with other server implementations, when the protocol changes everybody needs to do the same update, so
  we can replicate what others have done. Remember only privileged people have access to the official
  bedrock edition protocol documentation provided by Microsoft, unfortunately, nobody in our team have this privilege,
  so we need to gather information around the internet.

### Update Bedrock-ProxyPass, Bedrock-Protocol, and Bedrock-Network
Now that we know what have change, we need to update:
- https://github.com/PowerNukkit/Bedrock-Network
- https://github.com/PowerNukkit/Bedrock-Protocol
- https://github.com/PowerNukkit/Bedrock-ProxyPass

#### Setting up Bedrock-ProxyPass in IntelliJ IDEA
- Checkout and open as a project: https://github.com/PowerNukkit/Bedrock-ProxyPass.git
- Checkout but **don't open as a project, only download the source**: https://github.com/PowerNukkit/Bedrock-Network
- Checkout but **don't open as a project, only download the source**: https://github.com/PowerNukkit/Bedrock-Protocol
- Open the **Maven** tab, click the **+** button to add a maven project to the current project (That should be Bedrock-ProxyPass)
- Add: **Bedrock-Network**, and **Bedrock-Protocol** that you have just downloaded
- Press **Shift** two times to open the sarch window and search for the **Action**: `manage git remotes`
- Add a new remote for all 3 git repositores, pointing to the cloudburst equivalent of the project:
    * Bedrock-Network: https://github.com/CloudburstMC/Network.git
    * Bedrock-Protocol: https://github.com/CloudburstMC/Protocol.git
    * Bedrock-Proxy-Pass: https://github.com/CloudburstMC/ProxyPass.git
- Open the **Git** menu at the top of the window and click **Fetch**
- Open the **Git** tab at the bottom of the Window and open the **Log: <project-name>** tab
- Click **Paths: All** and deselect all 3 roots
- Click **Paths: No roots** and select only **Bedrock-Network**
- Find the last commit tagged **cloudburst/develop**
- Right click it and select: `Branch 'cloudburst/develop'` -> `Pull into current using merge` (manual merge may be necessary)
- Click **Paths:** button again and select only **Bedrock-Protocol** and do the same
- Repeat this process with **Bedrock-ProxyPass** but find the **cloudburst/master** tag in the logs instead of **cloudburst/develop**
- Manually adjust the **pom.xml** of the last **bedrock-vNNN** module at **Bedrock Protocol** 
- You may need to update the `ProxyPass.CODEC` manually too.
- Build and run ProxyPass proxying [Vanilla Bedrock Dedicated Server](https://www.minecraft.net/en-us/download/server/bedrock)
  with the config to export data enabled
- Connect to the vanilla bedrock server thought the proxy for it to extract the necessary data from the connection.

### Run the update tools
Now that you have extracted the data, confirm that PMMP have released the last BedrockData,
the update tools make use of that: https://github.com/pmmp/BedrockData

The update tools are classes that are located at the test folder of this project,
they have some pre-requisites explained in the comments right on top of the files,
make sure they are fulfilled before running or things can go wrong...

- Adjust the path in `org.powernukkit.updater.AllResourcesDownloader.main(String[])` to point to
  the data folder in your ProxyPass run folder, some data will be loaded from there
- Run `org.powernukkit.updater.AllResourcesDownloader.main(String[])`
- Run `mvn clean package` (Don't skip!)
- Run `org.powernukkit.updater.RuntimeItemIdUpdater.main(String[])`
- Run `mvn clean package` (Don't skip!)
- Run `org.powernukkit.dumps.ItemIdDumper.main(String[])`
- Run `org.powernukkit.dumps.RuntimeBlockStateDumper.main(String[])`
- Adjust all necessary block properties
- Run `org.powernukkit.updater.AllResourceUpdater.main(String[])`
- Run `mvn clean package` (Don't skip!)

### After the update
Try to start the server, make sure no error or warning is logged,
new blocks/items may cause issues, and we need to fix or handle them
from the Java classes, **without changing any resource**, only Java code!

Some existing blocks may receive new block state properties, make sure
to update their class file to include the new properties

Some properties may receive new values, update them too.

You can check what has changed investigating the files in the dumps folder,
they give you different views of the blocks, items, and block states, so
we can find what's happening easier.
