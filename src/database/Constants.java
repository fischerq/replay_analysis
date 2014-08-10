package database;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {
	private static Map<String, Integer> current_map = null;
	private static void put(String value){
		current_map.put(value, current_map.size());
	}
	
	public static final Map<String, Integer> unitTypes;
    static {
        current_map = new HashMap<String, Integer>();
        put("Anti-Mage");
        put("Axe");
        put("Bane");
        put("Bloodseeker");
        put("Crystal Maiden");
        put("Drow Ranger");
        put("Earthshaker");
        put("Juggernaut");
        put("Mirana");
        put("Shadow Fiend");
        put("Morphling");
        put("Phantom Lancer");
        put("Puck");
        put("Pudge");
        put("Razor");
        put("Sand King");
        put("Storm Spirit");
        put("Sven");
        put("Tiny");
        put("Vengeful Spirit");
        put("Windranger");
        put("Zeus");
        put("Kunkka");
        put("Lina");
        put("Lion");
        put("Shadow Shaman");
        put("Slardar");
        put("Tidehunter");
        put("Witch Doctor");
        put("Lich");
        put("Riki");
        put("Enigma");
        put("Tinker");
        put("Sniper");
        put("Necrophos");
        put("Warlock");
        put("Beastmaster");
        put("Queen of Pain");
        put("Venomancer");
        put("Faceless Void");
        put("Wraith King");
        put("Death Prophet");
        put("Phantom Assassin");
        put("Pugna");
        put("Templar Assassin");
        put("Viper");
        put("Luna");
        put("Dragon Knight");
        put("Dazzle");
        put("Clockwerk");
        put("Leshrac");
        put("Nature's Prophet");
        put("Lifestealer");
        put("Dark Seer");
        put("Clinkz");
        put("Omniknight");
        put("Enchantress");
        put("Huskar");
        put("Night Stalker");
        put("Broodmother");
        put("Bounty Hunter");
        put("Weaver");
        put("Jakiro");
        put("Batrider");
        put("Chen");
        put("Spectre");
        put("Doom");
        put("Ancient Apparition");
        put("Ursa");
        put("Spirit Breaker");
        put("Gyrocopter");
        put("Alchemist");
        put("Invoker");
        put("Silencer");
        put("Outworld Devourer");
        put("Lycanthrope");
        put("Brewmaster");
        put("Shadow Demon");
        put("Lone Druid");
        put("Chaos Knight");
        put("Meepo");
        put("Treant Protector");
        put("Ogre Magi");
        put("Undying");
        put("Rubick");
        put("Disruptor");
        put("Nyx Assassin");
        put("Naga Siren");
        put("Keeper of the Light");
        put("Wisp");
        put("Visage");
        put("Slark");
        put("Medusa");
        put("Troll Warlord");
        put("Centaur Warrunner");
        put("Magnus");
        put("Timbersaw");
        put("Bristleback");
        put("Tusk");
        put("Skywrath Mage");
        put("Abaddon");
        put("Elder Titan");
        put("Legion Commander");
        put("Ember Spirit");
        put("Earth Spirit");
        put("Abyssal Underlord");
        put("Terrorblade");
        put("Phoenix");
        
        put("Radiant Melee Creep");
        put("Radiant Ranged Creep");
        put("Radiant Siege Creep");
        put("Radiant Mega Melee Creep");
        put("Radiant Mega Ranged Creep");
        put("Radiant Mega Siege Creep");
        put("Dire Melee Creep");
        put("Dire Ranged Creep");
        put("Dire Siege Creep");
        put("Dire Mega Melee Creep");
        put("Dire Mega Ranged Creep");
        put("Dire Mega Siege Creep");        
		
        put("Radiant Tower T1 Top");
        put("Radiant Tower T1 Middle");
        put("Radiant Tower T1 Bottom");
        put("Radiant Tower T2 Top");
        put("Radiant Tower T2 Middle");
        put("Radiant Tower T2 Bottom");
        put("Radiant Tower T3 Top");
        put("Radiant Tower T3 Middle");
        put("Radiant Tower T3 Bottom");
        put("Radiant Tower T4");
        put("Dire Tower T1 Top");
        put("Dire Tower T1 Middle");
        put("Dire Tower T1 Bottom");
        put("Dire Tower T2 Top");
        put("Dire Tower T2 Middle");
        put("Dire Tower T2 Bottom");
        put("Dire Tower T3 Top");
        put("Dire Tower T3 Middle");
        put("Dire Tower T3 Bottom");
        put("Dire Tower T4");
        put("Radiant Building");
        put("Dire Building");
        put("Radiant Melee Barracks Top");
        put("Radiant Melee Barracks Middle");
        put("Radiant Melee Barracks Bottom");
        put("Radiant Ranged Barracks Top");
        put("Radiant Ranged Barracks Middle");
        put("Radiant Ranged Barracks Bottom");
        put("Dire Melee Barracks Top");
        put("Dire Melee Barracks Middle");
        put("Dire Melee Barracks Bottom");
        put("Dire Ranged Barracks Top");
        put("Dire Ranged Barracks Middle");
        put("Dire Ranged Barracks Bottom");
        put("Radiant Ancient");
        put("Dire Ancient");
        
        put("Kobold");
        put("Kobold Tunneler");
        put("Kobold Foreman");
        put("Centaur Outrunner");
        put("Centaur Khan");
        put("Fell Spirit");
        put("Hellbear Smasher");
        put("Hellbear");
        put("Mud Golem");
        put("Ogre Bruiser");
        put("Ogre Frostmage");
        put("Giant Wolf");
        put("Alpha Wolf");
        put("Wildwing");
        put("Wildwing Ripper");
        put("Satyr Soulstealer");
        put("Satyr Hellcaller");
        put("Rock Golem");
        put("Granite Golem");
        put("Thunderhide");
        put("Rumblehide");
        put("Vhoul Assassin");
        put("Ghost");
        put("Dark Troll");
        put("Dark Troll Summoner");
        put("Satyr Trickster");
        put("Forest Troll Berserker");
        put("Harpy Scout");
        put("Harpy Stormcrafter");
        put("Black Drake");
        put("Black Dragon");
        
        
        put("Necronomicon Warrior Lvl 1");
        put("Necronomicon Warrior Lvl 2");
        put("Necronomicon Warrior Lvl 3");
        put("Necronomicon Archer Lvl 1");
        put("Necronomicon Archer Lvl 2");
        put("Necronomicon Archer Lvl 3");
        put("Observer Ward");
        put("Sentry Ward");
        put("Courier");
        
        put("Treant");

        unitTypes = Collections.unmodifiableMap(current_map);
    }
    
    public static final Map<String, Integer> eventTypes;
    static {
        current_map = new HashMap<String, Integer>();
        put("Spawn");
        
        put("DealDamage");
        put("Heal");
        put("GainModifier");
        put("LoseModifier");
        put("Kill");
        put("UseAbility");
        put("UseItem");
        put("GainGold");
        put("GainExperience");
        put("PurchaseItem");
        put("AddItem");
        put("LoseItem");
        put("Die");
        
        put("ChangeVisibility");

        put("StartAnimation");
        put("CastAnimation");
        put("StopAnimation");
        put("CancelAnimation");

        eventTypes = Collections.unmodifiableMap(current_map);
    }
    
    public static final Map<String, Integer> sides;
    static {
        current_map = new HashMap<String, Integer>();
        put("Radiant");
        put("Dire");
        sides = Collections.unmodifiableMap(current_map);
    }
    
    public static final Map<String, Integer> teams;
    static {
        current_map = new HashMap<String, Integer>();
        put("Neutral");
        put("Radiant");
        put("Dire");
        put("Changing");
        teams = Collections.unmodifiableMap(current_map);
    }
    
    public static final Map<String, Integer> timeSeries;
    static {
        current_map = new HashMap<String, Integer>();
        put("PositionX");
        put("PositionY");
        put("Rotation");
        put("Health");
        put("Mana");
        timeSeries = Collections.unmodifiableMap(current_map);
    }
}
