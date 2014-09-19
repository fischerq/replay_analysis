package database;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Constants {
	private static HashMap<String, Integer> current_map = null;
	private static void put(String value){
		current_map.put(value, current_map.size());
	}
	
	private static final Map<String, HashMap<String, Integer>> maps;
	static{
		Map<String, HashMap<String, Integer>> tmpMap = new HashMap<String, HashMap<String, Integer>>();
		tmpMap.put("StringMappings", new HashMap<String, Integer>());
		tmpMap.put("UnitTypes", getUnitTypes());
		tmpMap.put("EventTypes", getEventTypes());
		tmpMap.put("EventArguments", getEventArguments());
		tmpMap.put("Sides", getSides());
		tmpMap.put("Teams", getTeams());		
		tmpMap.put("Items", getItems());
		tmpMap.put("InventorySlots", getInventorySlots());
		tmpMap.put("ToggleStates", getToggleStates());
		tmpMap.put("TimeSeries", getTimeSeries());
		tmpMap.put("Actions", getActions());
		tmpMap.put("Projectiles", getProjectiles());
		tmpMap.put("ClickTypes", getClickTypes());
		tmpMap.put("Modifiers", getModifiers());
		
		current_map = new HashMap<String, Integer>();
		put("StringMappings");
		put("UnitTypes");
		put("EventTypes");
		put("EventArguments");
		put("Sides");
		put("Teams");
		put("Items");
		put("InventorySlots");
		put("ToggleStates");
		put("TimeSeries");
		put("Actions");
		put("Projectiles");
		put("ClickTypes");
		put("Modifiers");
		tmpMap.put("StringMappings", current_map);
				
		maps = Collections.unmodifiableMap(tmpMap); 
	}
	
	public static int getIndex(String mapping, String string){
		Map<String, Integer> map = maps.get(mapping);
		if(map == null){
			System.out.println("Unknown mapping "+mapping);
			return -1;
		}
		else{
			Integer index = map.get(string);
			if(index == null){
				System.out.println("Unknown string (mapping: "+mapping+"): "+string);
				return -1;
			}
			else
				return index;
		}			
	}
	
	public static Set<String> getAllValues(String mapping){
		return maps.get(mapping).keySet();
	}
	
	private static HashMap<String, Integer> getUnitTypes(){
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
        put("Satyr Mindstealer");
        put("Satyr Tormenter");
        put("Rock Golem");
        put("Granite Golem");
        put("Thunderhide");
        put("Rumblehide");
        put("Vhoul Assassin");
        put("Ghost");
        put("Dark Troll");
        put("Dark Troll Summoner");
        put("Satyr Banisher");
        put("Hill Troll Berserker");
        put("Hill Troll Priest");
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

        return current_map;
    }
    
	private static HashMap<String, Integer> getEventTypes(){
        current_map = new HashMap<String, Integer>();
        put("Spawn");
        put("Death");
        
        put("DealDamage");
        put("Heal");
        
        put("ModifierGain");
        put("ModifierLoss");
        
        put("AbilityLevel");
        put("AbilityUse");
        
        put("CooldownStart");
        put("CooldownEnd");
        
        put("Kill");
        put("ItemUse");
        put("GoldGain");
        put("ExperienceGain");
        
        put("ItemPurchase");
        put("ItemAddition");
        put("ItemLoss");
        put("ItemMove");
        put("ItemChargeChange");
        put("ItemToggle");
        
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
        
        return current_map;
    }
	
	private static HashMap<String, Integer> getEventArguments(){
        current_map = new HashMap<String, Integer>();
        put("Unit");
        put("ActingUnit");
        put("AffectedUnit");
        put("Amount");
        put("Modifier");
        put("Ability");
        put("Level");
        put("Duration");
        put("Item");
        put("InventorySlot");
        put("InventorySlotOrigin");
        put("ToggleState");
        put("Action");
        put("AnimationEvent");
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

        return current_map;
    }  
    
	private static HashMap<String, Integer> getSides(){
        current_map = new HashMap<String, Integer>();
        put("Radiant");
        put("Dire");
        return current_map;
    }
    
	private static HashMap<String, Integer> getTeams(){
        current_map = new HashMap<String, Integer>();
		put("Unassigned");
		put("Spectator");
        put("Neutral");
        put("Radiant");
        put("Dire");
        put("Changing");

        return current_map;
    }
    
	private static HashMap<String, Integer> getItems(){
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
		put("Chainmail");
		put("Quarterstaff");
		put("Helm of Iron Will");
		put("Broadsword");
		put("Platemail");
		put("Javelin");
		put("Mithril Hammer");

		put("Magic Stick");
		put("Sage's Mask");
		put("Ring of Regeneration");
		put("Boots of Speed");
		put("Gloves of Haste");
		put("Cloak");
		put("Gem of True Sight");
		put("Ghost Scepter");
		put("Talisman of Evasion");
		put("Blink Dagger");

		put("Recipe: Magic Wand");
		put("Magic Wand");
		put("Recipe: Bracer");
		put("Bracer");
		put("Recipe: Wraith Band");
		put("Wraith Band");
		put("Recipe: Null Talisman");
		put("Null Talisman");
		put("Recipe: Soul Ring");
		put("Soul Ring");
		put("Phase Boots");
		put("Power Treads");
		put("Oblivion Staff");
		put("Perseverance");
		put("Recipe: Boots of Travel");
		put("Boots of Travel");
		
		put("Ring of Basilius");
		put("Recipe: Headdress");
		put("Headdress");
		put("Ring of Aquila");
		put("Arcane Boots");
		put("Recipe: Drums of Endurance");
		put("Drums of Endurance");
		put("Recipe: Pipe of Insight");
		put("Pipe of Insight");

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
		put("Aghanim's Scepter");
		put("Recipe: Refresher Orb");
		put("Refresher Orb");
		put("Scythe of Vyse");
		
		put("Recipe: Crystalys");
		put("Crystalys");
		put("Ethereal Blade");
		put("Monkey King Bar");
		put("Recipe: Daedalus");
		put("Daedalus");
		
		put("Hood of Defiance");
		put("Recipe: Black King Bar");
		put("Black King Bar");
		put("Recipe: Manta");
		put("Manta");
		put("Recipe: Assault Cuirass");
		put("Assault Cuirass");
		put("Recipe: Heart of Tarrasque");
		put("Heart of Tarrasque");
		
		put("Recipe: Sange");
		put("Sange");
		put("Recipe: Yasha");
		put("Yasha");
		put("Recipe: Maelstrom");
		put("Maelstrom");
		put("Sange and Yasha");
		put("Recipe: Desolator");
		put("Desolator");
		put("Recipe: Mjollnir");
		put("Mjollnir");
	
		put("Ring of Health");
		put("Void Stone");
		put("Energy Booster");
		put("Vitality Booster");
		put("Point Booster");
		put("Hyperstone");
		put("Demon Edge");
		put("Reaver");
		put("Mystic Staff");
		put("Eaglesong");
	
		put("Cheese");
		put("Aegis");

		return current_map;
    }
    
	private static HashMap<String, Integer> getInventorySlots(){
        current_map = new HashMap<String, Integer>();
        put("TopLeft");
        put("TopCenter");
        put("TopRight");
        put("BottomLeft");
        put("BottomCenter");
        put("BottomRight");
        put("Stash1");
        put("Stash2");
        put("Stash3");
        put("Stash4");
        put("Stash5");
        put("Stash6");

        return current_map;
    }
    
	private static HashMap<String, Integer> getToggleStates(){
        current_map = new HashMap<String, Integer>();
        put("Active");
        put("Disabled");
        put("Strength");
        put("Intelligence");
        put("Agility");

        return current_map;
    }
    
	private static HashMap<String, Integer> getTimeSeries(){
        current_map = new HashMap<String, Integer>();
        put("PositionX");
        put("PositionY");
        put("Orientation");
        put("Health");
        put("Mana");
        put("Control");
        
        return current_map;
    }
        
	private static HashMap<String, Integer> getActions(){
        current_map = new HashMap<String, Integer>();
		put("Walking");
		put("Attack Default");
		put("Attack Variant");
		put("Attack Special");

		put("Ability Special");

		//Abilities
		put("Shadowraze Short");
		put("Shadowraze Medium");
		put("Shadowraze Long");
		put("Requiem of Souls");
		
		put("Magic Missile");
		put("Wave of Terror");
		
		put("Breathe Fire");
		put("Dragon Tail");
		
		put("Viper Strike");
		
		put("Shuriken Toss");
		put("Track");
		
		put("Epicenter");
		
		put("Starfall");
		put("Sacred Arrow");
		
		put("Shackleshot");
		put("Powershot");
		
		put("Sprout");
		put("Teleportation");
		put("Force of Nature");
		put("Wrath of Nature");
		
		put("Arc Lightning");
		put("Lightning Bolt");
		put("Thundergod's Wrath");
		
		put("Necronomicon Archer Mana Burn");
		
		put("War Stomp");
		put("Frenzy");
		
		return current_map;
    }
    
	private static HashMap<String, Integer> getProjectiles(){
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

        return current_map;
    }
    
	private static HashMap<String, Integer> getClickTypes(){
        current_map = new HashMap<String, Integer>();
        put("Move Ground");
        put("Move Unit");
        put("Attack Ground");
        put("Attack Unit");
        put("Cast Ground");
        put("Cast Unit");
        put("Cast Tree");
        put("Drop Item");
        put("Give Item");
        put("Pick Up Item");
        put("Pick Rune");
        put("Reposition Item");
        
        return current_map;
    }
    
	private static HashMap<String, Integer> getModifiers(){
        current_map = new HashMap<String, Integer>();
        put("Phased");
        put("Magic Immune");
        put("True Sight");
        put("Illusion");
        put("Stunned");
        put("Invisible");
        put("Kill");
        put("Projectile Vision");
        
        put("Haste Rune");
        put("Invisibility Rune");
        put("Double Damage Rune");
        put("Regeneration Rune");
        
        put("Buyback");
        
        put("Jinada");
        put("Jinada Slow");
        put("Shadow Walk");
        put("Track");
        put("Track Effect");
        
        put("Burrowstrike Impale");
        put("Sandstorm");
        put("Sandstorm Invisibility");
        put("Caustic Finale");
        put("Epicenter Slow");
        
        put("Corrosive Breath");
        put("Frost Breath");
        
        put("Leap Buff");
        put("Moonlight Shadow");
        
        put("Presence of the Dark Lord");
        put("Requiem of Souls Invisibility Break");
        put("Requiem of Souls");
        
        put("Shackle Shot");
        put("Windrun");
        put("Windrun Slow");
        put("Focus Fire");
        
        put("Wave of Terror");
        put("Vengeance Aura");
        put("Negative Vengeance Aura");
        
        put("Poison Attack");
        put("Corrosive Skin");
        put("Viper Strike");
                
        put("Glyph");
        put("Invulnerable");
        put("Backdoor Protection");
        
        put("Fountain Regeneration");
        
        put("Creep Haste");
        put("Creep Slow");
        
        put("Teleporting");
        put("Soul Ring");
        put("Clarity Regeneration");
        put("Tango Regeneration");
        put("Healing Salve Regeneration");
        put("Bottle Regeneration");
        put("Ring of Basilius");
        put("Ring of Aquila");
        put("Phase Boots");
        put("Headdress");
        put("Insight Aura");
        put("Drums of Endurance");
        put("Drums od Endurance Active");
        put("Assault Cuirass Positive");
        put("Force Staff");
        put("Desolator");
        put("Greater Maim");
        put("Maelstrom Chain Lightning");
        put("Static Charge");
        put("Mjollnir Chain Lightning");
        put("Dust of Appearance");
        put("Smoke of Deceit");
        put("Black King Bar");
        put("Veil of Discord");
        put("Assault Aura Positive");
        put("Assault Aura Negative");
        put("Orchid Malevolence");
        put("Scythe of Vyse");
        put("Manta Phase");
        put("Manta Illusion");
        put("Boots of Travel Incoming");
        put("Ethereal Blade Ethereal");
        put("Ethereal Blade Slow");
        put("Barrier");
        put("Pipe Debuff");
        

        put("Flying");
        put("Courier Take Stash");
        put("Courier Transfer Items");
        put("Courier Burst");
        
        put("Roshan Spell Block");
        put("Roshan Slam");
        
        put("Frost Attack");
        put("Envenomed Weapon");
        put("Neutral Spell Immunity");
        put("Mana Aura");
        put("Unholy Aura");
        put("Speed Aura");
        put("Toughness Aura");
        put("Endurance Aura");
        put("Command Aura");
        put("Frenzy");







        put("");
        put("");
        put("");
        put("");
        put("");
        put("");
        
        return current_map;
    }
}
