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
        put("Death");
        
        put("DealDamage");
        put("Heal");
        put("ModifierGain");
        put("ModifierLoss");
        put("Kill");
        put("AbilityUse");
        put("ItemUse");
        put("GoldGain");
        put("ExperienceGain");
        
        put("ItemPurchase");
        put("ItemAddition");
        put("ItemLoss");
        
        put("VisionGain");
        put("VisionLoss");

        put("AnimationStart");
        put("AnimationCast");
        put("AnimationStop");
        put("AnimationCancel");
        
        put("LinearProjectileCreation");
        put("LinearProjectileRemoval");
        
        put("TrackingProjectileCreation");
        put("TrackingProjectileHit");
        put("TrackingProjectileDodge");
        put("TrackingProjectileExpiration");

        put("PlayerClick");
        put("Ping");
        put("SelectUnits");
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
		put("Unassigned");
		put("Spectator");
        put("Neutral");
        put("Radiant");
        put("Dire");
        put("Changing");
        teams = Collections.unmodifiableMap(current_map);
    }
    
    public static final Map<String, Integer> items;
    static {
        current_map = new HashMap<String, Integer>();
        put("Clarity");
		put("Tango");
		put("Healing Salve");
		put("Smoke of Deceit");
		put("Town Portal Scroll");
		put("Dust of Appearance");
		put("Courier");
		put("Recipe: Flying Courier");
		put("Observer Ward");
		put("Sentry Ward");
		put("Bottle");
		
		put("Iron Branch");
		put("Gauntlets of Strength");
		put("Slippers of Agility");
		put("Mantle of Intelligence");
		put("Circlet");
		put("Belt of Strength");
		put("Band of Elvenskin");
		put("Robe of the Magi");
		put("Ogre Club");
		put("Blade of Alacrity");
		put("Staff of Wizardry");
		put("Ultimate Orb");
		
		put("Ring of Protection");
		put("Quelling Blade");
		put("Stout Shield");
		put("Blades of Attack");
		put("Quarterstaff");
		put("Helm of Iron Will");
		put("Javelin");
		put("Mithril Hammer");

		put("Magic Stick");
		put("Sage's Mask");
		put("Ring of Regeneration");
		put("Boots of Speed");
		put("Gloves of Haste");
		put("Cloak");
		put("Ghost Scepter");
		put("Talisman of Evasion");
		put("Blink Dagger");

		put("Recipe: Magic Wand");
		put("Magic Wand");
		put("Recipe: Bracer");
		put("Recipe: Wraith Band");
		put("Recipe: Null Talisman");
		put("Recipe: Soul Ring");
		put("Soul Ring");
		put("Phase Boots");
		put("Power Treads");
		put("Recipe: Boots of Travel");
		put("Boots of Travel");
		
		put("Ring of Basilius");
		put("Recipe: Headdress");
		put("Ring of Aquila");
		put("Arcane Boots");
		put("Recipe: Drums of Endurance");
		put("Drums of Endurance");

		put("Recipe: Force Staff");
		put("Force Staff");
		put("Recipe: Veil of Discord");
		put("Veil of Discord");
		put("Recipe: Necronomicon");
		put("Necronomicon Level 1");
		put("Necronomicon Level 2");
		put("Necronomicon Level 3");
		put("Recipe: Orchid");
		put("Orchid");
		put("Recipe: Refresher Orb");
		put("Refresher Orb");
		put("Scythe of Vyse");
		
		put("Recipe: Crystalys");
		put("Ethereal Blade");
		put("Recipe: Daedalus");
		
		put("Recipe: Black King Bar");
		put("Black King Bar");
		put("Recipe: Manta");
		put("Manta");
		put("Recipe: Assault Cuirass");
		put("Recipe: Heart of Tarrasque");
		
		put("Recipe: Sange");
		put("Yasha");
		put("Recipe: Maelstrom");
		put("Recipe: Desolator");
		put("Recipe: Mjollnir");
		put("Mjollnir");
	
		put("Ring of Health");
		put("Void Stone");
		put("Energy Booster");
		put("Point Booster");
		put("Hyperstone");
		put("Demon Edge");
		put("Eaglesong");
		put("Reaver");
	
		put("Cheese");
        items = Collections.unmodifiableMap(current_map);
    }
    
    public static final Map<String, Integer> timeSeries;
    static {
        current_map = new HashMap<String, Integer>();
        put("PositionX");
        put("PositionY");
        put("Orientation");
        put("Health");
        put("Mana");
        put("Control");
        timeSeries = Collections.unmodifiableMap(current_map);
    }
        
    public static final Map<String, Integer> eventArguments;
    static {
        current_map = new HashMap<String, Integer>();
        put("Unit");
        put("ActingUnit");
        put("AffectedUnit");
        put("Amount");
        put("Modifier");
        put("Ability");
        put("Item");
        put("Action");
        put("Projectile");
        put("Index");
        put("PositionX");
        put("PositionY");
        put("VelocityX");
        put("VelocityY");
        put("Target");
        put("Side");
        put("Player");
        put("Cooldown");
        put("ClickType");
        eventArguments = Collections.unmodifiableMap(current_map);
    }
    
    
    public static final Map<String, Integer> actions;
    static {
        current_map = new HashMap<String, Integer>();
		put("Walking");
		put("AttackDefault");
		put("AttackVariant");
		put("AttackSpecial");
		put("AbilitySlot1");
		put("AbilitySlot2");
		put("AbilitySlot3");
		put("AbilitySlot4");
		put("AbilitySlot5");
		put("AbilitySlot6");
		put("AbilitySpecial");
        actions = Collections.unmodifiableMap(current_map);
    }
    
    public static final Map<String, Integer> projectiles;
    static {
        current_map = new HashMap<String, Integer>();
        put("Attack");
		put("Sacred Arrow");
		put("Breathe Fire");
		put("Wave of Terror");
		put("Shackleshot");
		put("Powershot");
		put("Shuriken Toss");
		put("Magic Missile");
        put("Unknown");
        projectiles = Collections.unmodifiableMap(current_map);
    }
    
    public static final Map<String, Integer> clickTypes;
    static {
        current_map = new HashMap<String, Integer>();
        put("MoveGround");
        put("MoveUnit");
        put("AttackGround");
        put("AttackUnit");
        put("CastGround");
        put("CastUnit");
        put("CastTree");
        put("DropItem");
        put("GiveItem");
        put("PickupItem");
        put("PickRune");
        put("RepositionItem");
        clickTypes = Collections.unmodifiableMap(current_map);
    }
    
    
}
