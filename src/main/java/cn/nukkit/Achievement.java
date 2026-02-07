package cn.nukkit;

import cn.nukkit.utils.TextFormat;

import java.util.HashMap;

/**
 * @author CreeperFace
 * @since 9/11/2016
 */
public class Achievement {

    public static final HashMap<String, Achievement> achievements;

    public final String message;
    public final String[] requires;

    public Achievement(String message, String... requires) {
        this.message = message;
        this.requires = requires;
    }

    public String getMessage() {
        return message;
    }

    public void broadcast(Player player) {
        Server server = Server.getInstance();

        String name = TextFormat.GREEN + this.getMessage();

        String translation = server.getLanguage()
                .tr("chat.type.achievement", player.getDisplayName(), name);

        if (server.getSettings().gameplaySettings().announceAchievements()) {
            server.broadcastMessage(translation);
        } else {
            player.sendMessage(translation);
        }
    }

    //TODO translate achievements according to player's language
    static {
        achievements = new HashMap<>();
        achievements.put("takingInventory", new Achievement("Taking Inventory"));
        achievements.put("gettingWood", new Achievement("Getting Wood", "takingInventory"));
        achievements.put("benchmaking", new Achievement("Benchmaking", "gettingWood"));
        achievements.put("timeToMine", new Achievement("Time to Mine!", "benchmaking"));
        achievements.put("hotTopic", new Achievement("Hot Topic", "timeToMine"));
        achievements.put("acquireHardware", new Achievement("Acquire Hardware", "hotTopic"));
        achievements.put("timeToFarm", new Achievement("Time to Farm!", "benchmaking"));
        achievements.put("bakeBread", new Achievement("Bake Bread", "timeToFarm"));
        achievements.put("theLie", new Achievement("The Lie", "timeToFarm"));
        achievements.put("gettingAnUpgrade", new Achievement("Getting an Upgrade", "timeToMine"));
        achievements.put("deliciousFish", new Achievement("Delicious Fish"));
        achievements.put("onARail", new Achievement("On A Rail"));
        achievements.put("timeToStrike", new Achievement("Time to Strike!", "benchmaking"));
        achievements.put("monsterHunter", new Achievement("Monster Hunter"));
        achievements.put("cowTipper", new Achievement("Cow Tipper"));
        achievements.put("whenPigsFly", new Achievement("When Pigs Fly"));
        achievements.put("sniperDuel", new Achievement("Sniper Duel"));
        achievements.put("diamonds", new Achievement("DIAMONDS!", "acquireHardware"));
        achievements.put("intoTheNether", new Achievement("Into The Nether"));
        achievements.put("returnToSender", new Achievement("Return to Sender"));
        achievements.put("intoFire", new Achievement("Into Fire"));
        achievements.put("localBrewery", new Achievement("Local Brewery"));
        achievements.put("theEndQuestion", new Achievement("The End?"));
        achievements.put("theEnd", new Achievement("The End"));
        achievements.put("enchanter", new Achievement("Enchanter"));
        achievements.put("overkill", new Achievement("Overkill"));
        achievements.put("librarian", new Achievement("Librarian"));
        achievements.put("adventuringTime", new Achievement("Adventuring Time"));
        achievements.put("theBeginningQuestion", new Achievement("The Beginning?"));
        achievements.put("theBeginning", new Achievement("The Beginning."));
        achievements.put("theBeaconator", new Achievement("The Beaconator"));
        achievements.put("repopulation", new Achievement("Repopulation"));
        achievements.put("diamondsToYou", new Achievement("Diamonds to you!"));
        achievements.put("overpowered", new Achievement("Overpowered"));
        achievements.put("moarTools", new Achievement("MOAR Tools"));
        achievements.put("dispenseWithThis", new Achievement("Dispense with This"));
        achievements.put("leaderOfThePack", new Achievement("Leader of the Pack"));
        achievements.put("porkChop", new Achievement("Pork Chop"));
        achievements.put("passingTheTime", new Achievement("Passing the Time"));
        achievements.put("theHaggler", new Achievement("The Haggler"));
        achievements.put("potPlanter", new Achievement("Pot Planter"));
        achievements.put("itsASign", new Achievement("It's a Sign!"));
        achievements.put("ironBelly", new Achievement("Iron Belly"));
        achievements.put("haveAShearfulDay", new Achievement("Have a Shearful Day"));
        achievements.put("rainbowCollection", new Achievement("Rainbow Collection"));
        achievements.put("stayinFrosty", new Achievement("Stayin' Frosty"));
        achievements.put("chestfulOfCobblestone", new Achievement("Chestful of Cobblestone"));
        achievements.put("renewableEnergy", new Achievement("Renewable Energy"));
        achievements.put("bodyGuard", new Achievement("Body Guard"));
        achievements.put("ironMan", new Achievement("Iron Man"));
        achievements.put("zombieDoctor", new Achievement("Zombie Doctor"));
        achievements.put("lionHunter", new Achievement("Lion Hunter"));
        achievements.put("archer", new Achievement("Archer"));
        achievements.put("tieDyeOutfit", new Achievement("Tie Dye Outfit"));
        achievements.put("trampoline", new Achievement("Trampoline"));
        achievements.put("camouflage", new Achievement("Camouflage"));
        achievements.put("mapRoom", new Achievement("Map Room"));
        achievements.put("freightStation", new Achievement("Freight Station"));
        achievements.put("smeltEverything", new Achievement("Smelt Everything!"));
        achievements.put("tasteOfYourOwnMedicine", new Achievement("Taste of Your Own Medicine"));
        achievements.put("inception", new Achievement("Inception"));
        achievements.put("saddleUp", new Achievement("Saddle Up"));
        achievements.put("artificialSelection", new Achievement("Artificial Selection"));
        achievements.put("freeDiver", new Achievement("Free Diver"));
        achievements.put("rabbitSeason", new Achievement("Rabbit Season"));
        achievements.put("theDeepEnd", new Achievement("The Deep End"));
        achievements.put("drySpell", new Achievement("Dry Spell"));
        achievements.put("superFuel", new Achievement("Super Fuel"));
        achievements.put("youNeedAMint", new Achievement("You Need a Mint"));
        achievements.put("beamMeUp", new Achievement("Beam Me Up"));
        achievements.put("theEndAgain", new Achievement("The End... Again..."));
        achievements.put("greatViewFromUpHere", new Achievement("Great View From Up Here"));
        achievements.put("superSonic", new Achievement("Super Sonic"));
        achievements.put("treasureHunter", new Achievement("Treasure Hunter"));
        achievements.put("organizationalWizard", new Achievement("Organizational Wizard"));
        achievements.put("cheatingDeath", new Achievement("Cheating Death"));
        achievements.put("feelingIll", new Achievement("Feeling Ill"));
        achievements.put("letItGo", new Achievement("Let It Go!"));
        achievements.put("soIGotThatGoingForMe", new Achievement("So I Got That Going for Me"));
        achievements.put("atlantis", new Achievement("Atlantis?"));
        achievements.put("sailTheSevenSeas", new Achievement("Sail the 7 Seas"));
        achievements.put("castaway", new Achievement("Castaway"));
        achievements.put("ahoy", new Achievement("Ahoy!"));
        achievements.put("marineBiologist", new Achievement("I am a Marine Biologist"));
        achievements.put("meGold", new Achievement("Me Gold!"));
        achievements.put("sleepWithTheFishes", new Achievement("Sleep with the Fishes"));
        achievements.put("alternativeFuel", new Achievement("Alternative Fuel"));
        achievements.put("doABarrelRoll", new Achievement("Do a Barrel Roll!"));
        achievements.put("onePickleTwoPickle", new Achievement("One Pickle, Two Pickle, Sea Pickle, Four"));
        achievements.put("echolocation", new Achievement("Echolocation"));
        achievements.put("moskstraumen", new Achievement("Moskstraumen"));
        achievements.put("topOfTheWorld", new Achievement("Top of the World"));
        achievements.put("whereHaveYouBeen", new Achievement("Where Have You Been?"));
        achievements.put("zoologist", new Achievement("Zoologist"));
        achievements.put("fruitOnTheLoom", new Achievement("Fruit on the Loom"));
        achievements.put("plethoraOfCats", new Achievement("Plethora of Cats"));
        achievements.put("killTheBeast", new Achievement("Kill the Beast!"));
        achievements.put("buyLowSellHigh", new Achievement("Buy Low, Sell High"));
        achievements.put("disenchanted", new Achievement("Disenchanted"));
        achievements.put("wereBeingAttacked", new Achievement("We're being attacked!"));
        achievements.put("soundTheAlarm", new Achievement("Sound the Alarm!"));
        achievements.put("badFeeling", new Achievement("I've got a bad feeling about this"));
        achievements.put("masterTrader", new Achievement("Master Trader"));
        achievements.put("timeForStew", new Achievement("Time for Stew"));
        achievements.put("beeOurGuest", new Achievement("Bee our guest"));
        achievements.put("totalBeelocation", new Achievement("Total Beelocation"));
        achievements.put("stickySituation", new Achievement("Sticky Situation"));
        achievements.put("bullseye", new Achievement("Bullseye"));
        achievements.put("coverMeInDebris", new Achievement("Cover me in debris"));
        achievements.put("ooohShiny", new Achievement("Oooh, shiny!"));
        achievements.put("hotTouristDestination", new Achievement("Hot tourist destination"));
        achievements.put("whateverFloatsYourGoat", new Achievement("Whatever Floats Your Goat"));
        achievements.put("waxOnWaxOff", new Achievement("Wax on, Wax off"));
        achievements.put("healingPowerOfFriendship", new Achievement("The Healing Power of Friendship!"));
        achievements.put("cavesAndCliffs", new Achievement("Caves & Cliffs"));
        achievements.put("starTrader", new Achievement("Star trader"));
        achievements.put("soundOfMusic", new Achievement("Sound of Music"));
        achievements.put("feelsLikeHome", new Achievement("Feels Like Home"));
        achievements.put("itSpreads", new Achievement("It spreads"));
        achievements.put("birthdaySong", new Achievement("Birthday song"));
        achievements.put("withOurPowersCombined", new Achievement("With our powers combined!"));
        achievements.put("sneak100", new Achievement("Sneak 100"));
        achievements.put("plantingThePast", new Achievement("Planting the past"));
        achievements.put("carefulRestoration", new Achievement("Careful restoration"));
        achievements.put("smithingWithStyle", new Achievement("Smithing with style"));
        achievements.put("craftersCraftingCrafters", new Achievement("Crafters Crafting Crafters"));
        achievements.put("whoNeedsRockets", new Achievement("Who Needs Rockets?"));
        achievements.put("overOverkill", new Achievement("Over-Overkill"));
        achievements.put("revaulting", new Achievement("Revaulting"));
        achievements.put("stayHydrated", new Achievement("Stay Hydrated!"));
    }

    public static boolean broadcast(Player player, String achievementId) {
        if (!achievements.containsKey(achievementId)) {
            return false;
        }

        Achievement achievement = achievements.get(achievementId);
        achievement.broadcast(player);

        return true;
    }

    public static boolean add(String name, Achievement achievement) {
        if (achievements.containsKey(name)) {
            return false;
        }

        achievements.put(name, achievement);
        return true;
    }
}
