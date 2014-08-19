package utils;

import data_extraction.Globals;

public class ConstantMapper {
	
	public static String heroName(int id){
		switch(id){
		case 1:
			return "Anti-Mage";
		case 2:
			return "Axe";
		case 3:
			return "Bane";
		case 4:
			return "Bloodseeker";
		case 5:
			return "Crystal Maiden";
		case 6:
			return "Drow Ranger";
		case 7:
			return "Earthshaker";
		case 8:
			return "Juggernaut";
		case 9:
			return "Mirana";
		case 10:
			return "Shadow Fiend";
		case 11:
			return "Morphling";
		case 12:
			return "Phantom Lancer";
		case 13:
			return "Puck";
		case 14:
			return "Pudge";
		case 15:
			return "Razor";
		case 16:
			return "Sand King";
		case 17:
			return "Storm Spirit";
		case 18:
			return "Sven";
		case 19:
			return "Tiny";
		case 20:
			return "Vengeful Spirit";
		case 21:
			return "Windranger";
		case 22:
			return "Zeus";
		case 23:
			return "Kunkka";
		case 25:
			return "Lina";
		case 26:
			return "Lion";
		case 27:
			return "Shadow Shaman";
		case 28:
			return "Slardar";
		case 29:
			return "Tidehunter";
		case 30:
			return "Witch Doctor";
		case 31:
			return "Lich";
		case 32:
			return "Riki";
		case 33:
			return "Enigma";
		case 34:
			return "Tinker";
		case 35:
			return "Sniper";
		case 36:
			return "Necrophos";
		case 37:
			return "Warlock";
		case 38:
			return "Beastmaster";
		case 39:
			return "Queen of Pain";
		case 40:
			return "Venomancer";
		case 41:
			return "Faceless Void";
		case 42:
			return "Wraith King";
		case 43:
			return "Death Prophet";
		case 44:
			return "Phantom Assassin";
		case 45:
			return "Pugna";
		case 46:
			return "Templar Assassin";
		case 47:
			return "Viper";
		case 48:
			return "Luna";
		case 49:
			return "Dragon Knight";
		case 50:
			return "Dazzle";
		case 51:
			return "Clockwerk";
		case 52:
			return "Leshrac";
		case 53:
			return "Nature's Prophet";
		case 54:
			return "Lifestealer";
		case 55:
			return "Dark Seer";
		case 56:
			return "Clinkz";
		case 57:
			return "Omniknight";
		case 58:
			return "Enchantress";
		case 59:
			return "Huskar";
		case 60:
			return "Night Stalker";
		case 61:
			return "Broodmother";
		case 62:
			return "Bounty Hunter";
		case 63:
			return "Weaver";
		case 64:
			return "Jakiro";
		case 65:
			return "Batrider";
		case 66:
			return "Chen";
		case 67:
			return "Spectre";
		case 68:
			return "Doom";
		case 69:
			return "Ancient Apparition";
		case 70:
			return "Ursa";
		case 71:
			return "Spirit Breaker";
		case 72:
			return "Gyrocopter";
		case 73:
			return "Alchemist";
		case 74:
			return "Invoker";
		case 75:
			return "Silencer";
		case 76:
			return "Outworld Devourer";
		case 77:
			return "Lycanthrope";
		case 78:
			return "Brewmaster";
		case 79:
			return "Shadow Demon";
		case 80:
			return "Lone Druid";
		case 81:
			return "Chaos Knight";
		case 82:
			return "Meepo";
		case 83:
			return "Treant Protector";
		case 84:
			return "Ogre Magi";
		case 85:
			return "Undying";
		case 86:
			return "Rubick";
		case 87:
			return "Disruptor";
		case 88:
			return "Nyx Assassin";
		case 89:
			return "Naga Siren";
		case 90:
			return "Keeper of the Light";
		case 91:
			return "Wisp";
		case 92:
			return "Visage";
		case 93:
			return "Slark";
		case 94:
			return "Medusa";
		case 95:
			return "Troll Warlord";
		case 96:
			return "Centaur Warrunner";
		case 97:
			return "Magnus";
		case 98:
			return "Timbersaw";
		case 99:
			return "Bristleback";
		case 100:
			return "Tusk";
		case 101:
			return "Skywrath Mage";
		case 102:
			return "Abaddon";
		case 103:
			return "Elder Titan";
		case 104:
			return "Legion Commander";
		case 106:
			return "Ember Spirit";
		case 107:
			return "Earth Spirit";
		case 108:
			return "Abyssal Underlord";
		case 109:
			return "Terrorblade";
		case 110:
			return "Phoenix";
		default:
			return "Unknown";
		}
	}
	
	public static String unitName(String combatLogName){
		if(combatLogName == null)
			return null;
//		System.out.println(combatLogName);
		switch(combatLogName){
		case "npc_dota_hero_antimage":
			return "Anti-Mage";
		case "npc_dota_hero_axe":
			return "Axe";
		case "npc_dota_hero_bane":
			return "Bane";
		case "npc_dota_hero_bloodseeker":
			return "Bloodseeker";
		case "npc_dota_hero_crystal_maiden":
			return "Crystal Maiden";
		case "npc_dota_hero_drow_ranger":
			return "Drow Ranger";
		case "npc_dota_hero_earthshaker":
			return "Earthshaker";
		case "npc_dota_hero_juggernaut":
			return "Juggernaut";
		case "npc_dota_hero_mirana":
			return "Mirana";
		case "npc_dota_hero_nevermore":
			return "Shadow Fiend";
		case "npc_dota_hero_morphling":
			return "Morphling";
		case "npc_dota_hero_phantom_lancer":
			return "Phantom Lancer";
		case "npc_dota_hero_puck":
			return "Puck";
		case "npc_dota_hero_pudge":
			return "Pudge";
		case "npc_dota_hero_razor":
			return "Razor";
		case "npc_dota_hero_sand_king":
			return "Sand King";
		case "npc_dota_hero_storm_spirit":
			return "Storm Spirit";
		case "npc_dota_hero_sven":
			return "Sven";
		case "npc_dota_hero_tiny":
			return "Tiny";
		case "npc_dota_hero_vengefulspirit":
			return "Vengeful Spirit";
		case "npc_dota_hero_windrunner":
			return "Windranger";
		case "npc_dota_hero_zuus":
			return "Zeus";
		case "npc_dota_hero_kunkka":
			return "Kunkka";
		case "npc_dota_hero_lina":
			return "Lina";
		case "npc_dota_hero_lion":
			return "Lion";
		case "npc_dota_hero_shadow_shaman":
			return "Shadow Shaman";
		case "npc_dota_hero_slardar":
			return "Slardar";
		case "npc_dota_hero_tidehunter":
			return "Tidehunter";
		case "npc_dota_hero_witch_doctor":
			return "Witch Doctor";
		case "npc_dota_hero_lich":
			return "Lich";
		case "npc_dota_hero_riki":
			return "Riki";
		case "npc_dota_hero_enigma":
			return "Enigma";
		case "npc_dota_hero_tinker":
			return "Tinker";
		case "npc_dota_hero_sniper":
			return "Sniper";
		case "npc_dota_hero_necrolyte":
			return "Necrophos";
		case "npc_dota_hero_warlock":
			return "Warlock";
		case "npc_dota_hero_beastmaster":
			return "Beastmaster";
		case "npc_dota_hero_queenofpain":
			return "Queen of Pain";
		case "npc_dota_hero_venomancer":
			return "Venomancer";
		case "npc_dota_hero_faceless_void":
			return "Faceless Void";
		case "npc_dota_hero_skeleton_king":
			return "Wraith King";
		case "npc_dota_hero_death_prophet":
			return "Death Prophet";
		case "npc_dota_hero_phantom_assassin":
			return "Phantom Assassin";
		case "npc_dota_hero_pugna":
			return "Pugna";
		case "npc_dota_hero_templar_assassin":
			return "Templar Assassin";
		case "npc_dota_hero_viper":
			return "Viper";
		case "npc_dota_hero_luna":
			return "Luna";
		case "npc_dota_hero_dragon_knight":
			return "Dragon Knight";
		case "npc_dota_hero_dazzle":
			return "Dazzle";
		case "npc_dota_hero_rattletrap":
			return "Clockwerk";
		case "npc_dota_hero_leshrac":
			return "Leshrac";
		case "npc_dota_hero_furion":
			return "Nature's Prophet";
		case "npc_dota_hero_life_stealer":
			return "Lifestealer";
		case "npc_dota_hero_dark_seer":
			return "Dark Seer";
		case "npc_dota_hero_clinkz":
			return "Clinkz";
		case "npc_dota_hero_omniknight":
			return "Omniknight";
		case "npc_dota_hero_enchantress":
			return "Enchantress";
		case "npc_dota_hero_huskar":
			return "Huskar";
		case "npc_dota_hero_night_stalker":
			return "Night Stalker";
		case "npc_dota_hero_broodmother":
			return "Broodmother";
		case "npc_dota_hero_bounty_hunter":
			return "Bounty Hunter";
		case "npc_dota_hero_weaver":
			return "Weaver";
		case "npc_dota_hero_jakiro":
			return "Jakiro";
		case "npc_dota_hero_batrider":
			return "Batrider";
		case "npc_dota_hero_chen":
			return "Chen";
		case "npc_dota_hero_spectre":
			return "Spectre";
		case "npc_dota_hero_doom_bringer":
			return "Doom";
		case "npc_dota_hero_ancient_apparition":
			return "Ancient Apparition";
		case "npc_dota_hero_ursa":
			return "Ursa";
		case "npc_dota_hero_spirit_breaker":
			return "Spirit Breaker";
		case "npc_dota_hero_gyrocopter":
			return "Gyrocopter";
		case "npc_dota_hero_alchemist":
			return "Alchemist";
		case "npc_dota_hero_invoker":
			return "Invoker";
		case "npc_dota_hero_silencer":
			return "Silencer";
		case "npc_dota_hero_obsidian_destroyer":
			return "Outworld Devourer";
		case "npc_dota_hero_lycan":
			return "Lycanthrope";
		case "npc_dota_hero_brewmaster":
			return "Brewmaster";
		case "npc_dota_hero_shadow_demon":
			return "Shadow Demon";
		case "npc_dota_hero_lone_druid":
			return "Lone Druid";
		case "npc_dota_hero_chaos_knight":
			return "Chaos Knight";
		case "npc_dota_hero_meepo":
			return "Meepo";
		case "npc_dota_hero_treant":
			return "Treant Protector";
		case "npc_dota_hero_ogre_magi":
			return "Ogre Magi";
		case "npc_dota_hero_undying":
			return "Undying";
		case "npc_dota_hero_rubick":
			return "Rubick";
		case "npc_dota_hero_disruptor":
			return "Disruptor";
		case "npc_dota_hero_nyx_assassin":
			return "Nyx Assassin";
		case "npc_dota_hero_naga_siren":
			return "Naga Siren";
		case "npc_dota_hero_keeper_of_the_light":
			return "Keeper of the Light";
		case "npc_dota_hero_wisp":
			return "Wisp";
		case "npc_dota_hero_visage":
			return "Visage";
		case "npc_dota_hero_slark":
			return "Slark";
		case "npc_dota_hero_medusa":
			return "Medusa";
		case "npc_dota_hero_troll_warlord":
			return "Troll Warlord";
		case "npc_dota_hero_centaur":
			return "Centaur Warrunner";
		case "npc_dota_hero_magnataur":
			return "Magnus";
		case "npc_dota_hero_shredder":
			return "Timbersaw";
		case "npc_dota_hero_bristleback":
			return "Bristleback";
		case "npc_dota_hero_tusk":
			return "Tusk";
		case "npc_dota_hero_skywrath_mage":
			return "Skywrath Mage";
		case "npc_dota_hero_abaddon":
			return "Abaddon";
		case "npc_dota_hero_elder_titan":
			return "Elder Titan";
		case "npc_dota_hero_legion_commander":
			return "Legion Commander";
		case "npc_dota_hero_ember_spirit":
			return "Ember Spirit";
		case "npc_dota_hero_earth_spirit":
			return "Earth Spirit";
		case "npc_dota_hero_abyssal_underlord":
			return "Abyssal Underlord";
		case "npc_dota_hero_terrorblade":
			return "Terrorblade";
		case "npc_dota_hero_phoenix":
			return "Phoenix";
			
		//Non-Heroes
		//Items 
		case "npc_dota_courier":
			return "Courier";
		case "npc_dota_observer_wards":
			return "Observer Ward";
		case "npc_dota_sentry_wards":
			return "Sentry Ward";
			
		//Buildings
		case "dota_fountain":
			return "Fountain";
		
		case "npc_dota_goodguys_tower1_top":
			return "Radiant Tower T1 Top";
		case "npc_dota_goodguys_tower1_mid":
			return "Radiant Tower T1 Middle";
		case "npc_dota_goodguys_tower1_bot":
			return "Radiant Tower T1 Bottom";
		case "npc_dota_goodguys_tower2_top":
			return "Radiant Tower T2 Top";
		case "npc_dota_goodguys_tower2_mid":
			return "Radiant Tower T2 Middle";
		case "npc_dota_goodguys_tower2_bot":
			return "Radiant Tower T2 Bottom";
		case "npc_dota_goodguys_tower3_top":
			return "Radiant Tower T3 Top";
		case "npc_dota_goodguys_tower3_mid":
			return "Radiant Tower T3 Middle";
		case "npc_dota_goodguys_tower3_bot":
			return "Radiant Tower T3 Bottom";
		case "npc_dota_goodguys_tower4":
			return "Radiant Tower T4";
			
		case "npc_dota_goodguys_melee_rax_top":
			return "Radiant Melee Barracks Top";
		case "npc_dota_goodguys_range_rax_top":
			return "Radiant Ranged Barracks Top";
		case "npc_dota_goodguys_melee_rax_bot":
			return "Radiant Melee Barracks Bottom";	
		case "npc_dota_goodguys_range_rax_bot":
			return "Radiant Ranged Barracks Bottom";	
		case "npc_dota_goodguys_melee_rax_mid":
			return "Radiant Melee Barracks Middle";
		case "npc_dota_goodguys_range_rax_mid":
			return "Radiant Ranged Barracks Middle";

		case "npc_dota_goodguys_fillers":
			return "Radiant Building";
		case "npc_dota_goodguys_fort":
			return "Radiant Ancient";
			
			
		case "npc_dota_badguys_tower1_top":
			return "Dire Tower T1 Top";
		case "npc_dota_badguys_tower1_mid":
			return "Dire Tower T1 Middle";
		case "npc_dota_badguys_tower1_bot":
			return "Dire Tower T1 Bottom";
		case "npc_dota_badguys_tower2_top":
			return "Dire Tower T2 Top";
		case "npc_dota_badguys_tower2_mid":
			return "Dire Tower T2 Middle";
		case "npc_dota_badguys_tower2_bot":
			return "Dire Tower T2 Bottom";
		case "npc_dota_badguys_tower3_top":
			return "Dire Tower T3 Top";
		case "npc_dota_badguys_tower3_mid":
			return "Dire Tower T3 Middle";
		case "npc_dota_badguys_tower3_bot":
			return "Dire Tower T3 Bottom";
		case "npc_dota_badguys_tower4":
			return "Dire Tower T4";

		case "npc_dota_badguys_melee_rax_top":
			return "Dire Melee Barracks Top";
		case "npc_dota_badguys_range_rax_top":
			return "Dire Ranged Barracks Top";
		case "npc_dota_badguys_melee_rax_bot":
			return "Dire Melee Barracks Bottom";	
		case "npc_dota_badguys_range_rax_bot":
			return "Dire Ranged Barracks Bottom";	
		case "npc_dota_badguys_melee_rax_mid":
			return "Dire Melee Barracks Middle";
		case "npc_dota_badguys_range_rax_mid":
			return "Dire Ranged Barracks Middle";

		case "npc_dota_badguys_fillers":
			return "Dire Building";
		case "npc_dota_badguys_fort":
			return "Dire Ancient";
			
		//Creeps
		case "npc_dota_creep_goodguys_melee":
			return "Radiant Melee Creep";
		case "npc_dota_creep_goodguys_ranged":
			return "Radiant Ranged Creep";
		case "npc_dota_goodguys_siege":
			return "Radiant Siege Creep";
		case "npc_dota_creep_goodguys_melee_upgraded":
			return "Radiant Mega Melee Creep";
		case "npc_dota_creep_goodguys_ranged_upgraded":
			return "Radiant Mega Ranged Creep";
		case "npc_dota_goodguys_siege_upgraded":
			return "Radiant Mega Siege Creep";
			
		case "npc_dota_creep_badguys_melee":
			return "Dire Melee Creep";
		case "npc_dota_creep_badguys_ranged":
			return "Dire Ranged Creep";
		case "npc_dota_badguys_siege":
			return "Dire Siege Creep";
		case "npc_dota_creep_badguys_melee_upgraded":
			return "Dire Mega Melee Creep";
		case "npc_dota_creep_badguys_ranged_upgraded":
			return "Dire Mega Ranged Creep";
		case "npc_dota_badguys_siege_upgraded":
				return "Dire Mega Siege Creep";
			
		//Neutrals
		case "npc_dota_roshan":
			return "Roshan";
		
		case "npc_dota_neutral_black_dragon":
			return "Black Dragon";
		case "npc_dota_neutral_black_drake":
			return "Black Drake";
			
		case "npc_dota_neutral_granite_golem":
			return "Granite Golem";
		case "npc_dota_neutral_rock_golem":
			return "Rock Golem";
		
		case "npc_dota_neutral_big_thunder_lizard":
			return "Thunderhide";
		case "npc_dota_neutral_small_thunder_lizard":
			return "Rumblehide";
			
		case "npc_dota_neutral_dark_troll_warlord":
			return "Dark Troll Summoner";
		case "npc_dota_neutral_dark_troll":
			return "Dark Troll";
		
		case "npc_dota_neutral_polar_furbolg_champion":
			return "Hellbear Smasher";
		case "npc_dota_neutral_polar_furbolg_ursa_warrior":
			return "Hellbear";
		
		case "npc_dota_neutral_enraged_wildkin":
			return "Wildwing Ripper";
		case "npc_dota_neutral_wildkin":
			return "Wildwing";
		
		case "npc_dota_neutral_satyr_hellcaller":
			return "Satyr Hellcaller";
		case "npc_dota_neutral_satyr_soulstealer":
			return "Satyr Soulstealer";
		case "npc_dota_neutral_satyr_trickster":
			return "Satyr Trickster";
		
		case "npc_dota_neutral_centaur_khan":
			return "Centaur Khan";
		case "npc_dota_neutral_centaur_outrunner":
			return "Centaur Outrunner";
		
		case "npc_dota_neutral_alpha_wolf":
			return "Alpha Wolf";
		case "npc_dota_neutral_giant_wolf":
			return "Giant Wolf";
		
		case "npc_dota_neutral_mud_golem":
			return "Mud Golem";
		
		case "npc_dota_neutral_ogre_magi":
			return "Ogre Frostmage";
		case "npc_dota_neutral_ogre_mauler":
			return "Ogre Bruiser";
		
		case "npc_dota_neutral_harpy_storm":
			return "Harpy Stormcrafter";
		case "npc_dota_neutral_harpy_scout":
			return "Harpy Scout";
			
		case "npc_dota_neutral_kobold_taskmaster":
			return "Kobold Foreman";
		case "npc_dota_neutral_kobold":
			return "Kobold";
		case "npc_dota_neutral_kobold_tunneler":
			return "Kobold Tunneler";
		
		case "npc_dota_neutral_forest_troll_berserker":
			return "Forest Troll Berserker";
		
		case "npc_dota_neutral_ghost":
			return "Ghost";
		case "npc_dota_neutral_fel_beast":
			return "Fell Spirit";
			
		case "npc_dota_neutral_gnoll_assassin":
			return "Vhoul Assassin";
		
		//Player spawns
		case "npc_dota_necronomicon_warrior_1":
			return "Necronomicon Warrior Lvl 1";
		case "npc_dota_necronomicon_archer_1":
			return "Necronomicon Archer Lvl 1";
		case "npc_dota_necronomicon_warrior_2":
			return "Necronomicon Warrior Lvl 2";
		case "npc_dota_necronomicon_archer_2":
			return "Necronomicon Archer Lvl 2";
		case "npc_dota_necronomicon_warrior_3":
			return "Necronomicon Warrior Lvl 3";
		case "npc_dota_necronomicon_archer_3":
			return "Necronomicon Archer Lvl 3";
		case "npc_dota_furion_treant":
			return "Treant";
		default:
			System.out.println("Unknown unit "+combatLogName);
			return "Unknown: "+combatLogName;
		}
	}
	
	public static String itemName(String combatlog_item){
		return combatlog_item;
	}
	
	public static String abilityName(String combatlog_ability){
		return combatlog_ability;
	}
	
	public static String DTClassForName(String name){
		//System.out.println("DT requested: "+name);
		switch(name){
		case "Anti-Mage":
			return "DT_DOTA_Unit_Hero_AntiMage";
		case "Axe":
			return "DT_DOTA_Unit_Hero_Axe";
		case "Bane":
			return "DT_DOTA_Unit_Hero_Bane";
		case "Bloodseeker":
			return "DT_DOTA_Unit_Hero_Bloodseeker";
		case "Crystal Maiden":
			return "DT_DOTA_Unit_Hero_CrystalMaiden";
		case "Drow Ranger":
			return "DT_DOTA_Unit_Hero_DrowRanger";
		case "Earthshaker":
			return "DT_DOTA_Unit_Hero_Earthshaker";
		case "Juggernaut":
			return "DT_DOTA_Unit_Hero_Juggernaut";
		case "Mirana":
			return "DT_DOTA_Unit_Hero_Mirana";
		case "Shadow Fiend":
			return "DT_DOTA_Unit_Hero_Nevermore";
		case "Morphling":
			return "DT_DOTA_Unit_Hero_Morphling";
		case "Phantom Lancer":
			return "DT_DOTA_Unit_Hero_PhantomLancer";
		case "Puck":
			return "DT_DOTA_Unit_Hero_Puck";
		case "Pudge":
			return "DT_DOTA_Unit_Hero_Pudge";
		case "Razor":
			return "DT_DOTA_Unit_Hero_Razor";
		case "Sand King":
			return "DT_DOTA_Unit_Hero_SandKing";
		case "Storm Spirit":
			return "DT_DOTA_Unit_Hero_StormSpirit";
		case "Sven":
			return "DT_DOTA_Unit_Hero_Sven";
		case "Tiny":
			return "DT_DOTA_Unit_Hero_Tiny";
		case "Vengeful Spirit":
			return "DT_DOTA_Unit_Hero_VengefulSpirit";
		case "Windranger":
			return "DT_DOTA_Unit_Hero_Windrunner";
		case "Zeus":
			return "DT_DOTA_Unit_Hero_Zuus";
		case "Kunkka":
			return "DT_DOTA_Unit_Hero_Kunkka";
		case "Lina":
			return "DT_DOTA_Unit_Hero_Lina";
		case "Lion":
			return "DT_DOTA_Unit_Hero_Lion";
		case "Shadow Shaman":
			return "DT_DOTA_Unit_Hero_ShadowShaman";
		case "Slardar":
			return "DT_DOTA_Unit_Hero_Slardar";
		case "Tidehunter":
			return "DT_DOTA_Unit_Hero_Tidehunter";
		case "Witch Doctor":
			return "DT_DOTA_Unit_Hero_WitchDoctor";
		case "Lich":
			return "DT_DOTA_Unit_Hero_Lich";
		case "Riki":
			return "DT_DOTA_Unit_Hero_Riki";
		case "Enigma":
			return "DT_DOTA_Unit_Hero_Enigma";
		case "Tinker":
			return "DT_DOTA_Unit_Hero_Tinker";
		case "Sniper":
			return "DT_DOTA_Unit_Hero_Sniper";
		case "Necrophos":
			return "DT_DOTA_Unit_Hero_Necrolyte";
		case "Warlock":
			return "DT_DOTA_Unit_Hero_Warlock";
		case "Beastmaster":
			return "DT_DOTA_Unit_Hero_Beastmaster";
		case "Queen of Pain":
			return "DT_DOTA_Unit_Hero_QueenOfPain";
		case "Venomancer":
			return "DT_DOTA_Unit_Hero_Venomancer";
		case "Faceless Void":
			return "DT_DOTA_Unit_Hero_FacelessVoid";
		case "Wraith King":
			return "DT_DOTA_Unit_Hero_SkeletonKing";
		case "Death Prophet":
			return "DT_DOTA_Unit_Hero_DeathProphet";
		case "Phantom Assassin":
			return "DT_DOTA_Unit_Hero_PhantomAssassin";
		case "Pugna":
			return "DT_DOTA_Unit_Hero_Pugna";
		case "Templar Assassin":
			return "DT_DOTA_Unit_Hero_TemplarAssassin";
		case "Viper":
			return "DT_DOTA_Unit_Hero_Viper";
		case "Luna":
			return "DT_DOTA_Unit_Hero_Luna";
		case "Dragon Knight":
			return "DT_DOTA_Unit_Hero_DragonKnight";
		case "Dazzle":
			return "DT_DOTA_Unit_Hero_Dazzle";
		case "Clockwerk":
			return "DT_DOTA_Unit_Hero_Rattletrap";
		case "Leshrac":
			return "DT_DOTA_Unit_Hero_Leshrac";
		case "Nature's Prophet":
			return "DT_DOTA_Unit_Hero_Furion";
		case "Lifestealer":
			return "DT_DOTA_Unit_Hero_Life_Stealer";
		case "Dark Seer":
			return "DT_DOTA_Unit_Hero_DarkSeer";
		case "Clinkz":
			return "DT_DOTA_Unit_Hero_Clinkz";
		case "Omniknight":
			return "DT_DOTA_Unit_Hero_Omniknight";
		case "Enchantress":
			return "DT_DOTA_Unit_Hero_Enchantress";
		case "Huskar":
			return "DT_DOTA_Unit_Hero_Huskar";
		case "Night Stalker":
			return "DT_DOTA_Unit_Hero_NightStalker";
		case "Broodmother":
			return "DT_DOTA_Unit_Hero_Broodmother";
		case "Bounty Hunter":
			return "DT_DOTA_Unit_Hero_BountyHunter";
		case "Weaver":
			return "DT_DOTA_Unit_Hero_Weaver";
		case "Jakiro":
			return "DT_DOTA_Unit_Hero_Jakiro";
		case "Batrider":
			return "DT_DOTA_Unit_Hero_Batrider";
		case "Chen":
			return "DT_DOTA_Unit_Hero_Chen";
		case "Spectre":
			return "DT_DOTA_Unit_Hero_Spectre";
		case "Doom":
			return "DT_DOTA_Unit_Hero_DoomBringer";
		case "Ancient Apparition":
			return "DT_DOTA_Unit_Hero_AncientApparition";
		case "Ursa":
			return "DT_DOTA_Unit_Hero_Ursa";
		case "Spirit Breaker":
			return "DT_DOTA_Unit_Hero_SpiritBreaker";
		case "Gyrocopter":
			return "DT_DOTA_Unit_Hero_Gyrocopter";
		case "Alchemist":
			return "DT_DOTA_Unit_Hero_Alchemist";
		case "Invoker":
			return "DT_DOTA_Unit_Hero_Invoker";
		case "Silencer":
			return "DT_DOTA_Unit_Hero_Silencer";
		case "Outworld Devourer":
			return "DT_DOTA_Unit_Hero_Obsidian_Destroyer";
		case "Lycanthrope":
			return "DT_DOTA_Unit_Hero_Lycan";
		case "Brewmaster":
			return "DT_DOTA_Unit_Hero_Brewmaster";
		case "Shadow Demon":
			return "DT_DOTA_Unit_Hero_Shadow_Demon";
		case "Lone Druid":
			return "DT_DOTA_Unit_Hero_LoneDruid";
		case "Chaos Knight":
			return "DT_DOTA_Unit_Hero_ChaosKnight";
		case "Meepo":
			return "DT_DOTA_Unit_Hero_Meepo";
		case "Treant Protector":
			return "DT_DOTA_Unit_Hero_Treant";
		case "Ogre Magi":
			return "DT_DOTA_Unit_Hero_Ogre_Magi";
		case "Undying":
			return "DT_DOTA_Unit_Hero_Undying";
		case "Rubick":
			return "DT_DOTA_Unit_Hero_Rubick";
		case "Disruptor":
			return "DT_DOTA_Unit_Hero_Disruptor";
		case "Nyx Assassin":
			return "DT_DOTA_Unit_Hero_Nyx_Assassin";
		case "Naga Siren":
			return "DT_DOTA_Unit_Hero_Naga_Siren";
		case "Keeper of the Light":
			return "DT_DOTA_Unit_Hero_KeeperOfTheLight";
		case "Wisp":
			return "DT_DOTA_Unit_Hero_Wisp";
		case "Visage":
			return "DT_DOTA_Unit_Hero_Visage";
		case "Slark":
			return "DT_DOTA_Unit_Hero_Slark";
		case "Medusa":
			return "DT_DOTA_Unit_Hero_Medusa";
		case "Troll Warlord":
			return "DT_DOTA_Unit_Hero_TrollWarlord";
		case "Centaur Warrunner":
			return "DT_DOTA_Unit_Hero_Centaur";
		case "Magnus":
			return "DT_DOTA_Unit_Hero_Magnataur";
		case "Timbersaw":
			return "DT_DOTA_Unit_Hero_Shredder";
		case "Bristleback":
			return "DT_DOTA_Unit_Hero_Bristleback";
		case "Tusk":
			return "DT_DOTA_Unit_Hero_Tusk";
		case "Skywrath Mage":
			return "DT_DOTA_Unit_Hero_Skywrath_Mage";
		case "Abaddon":
			return "DT_DOTA_Unit_Hero_Abaddon";
		case "Elder Titan":
			return "DT_DOTA_Unit_Hero_Elder_Titan";
		case "Legion Commander":
			return "DT_DOTA_Unit_Hero_Legion_Commander";
		case "Ember Spirit":
			return "DT_DOTA_Unit_Hero_EmberSpirit";
		case "Earth Spirit":
			return "DT_DOTA_Unit_Hero_EarthSpirit";
		case "Abyssal Underlord":
			return "DT_DOTA_Unit_Hero_AbyssalUnderlord";
		case "Terrorblade":
			return "DT_DOTA_Unit_Hero_Terrorblade";
		case "Phoenix":
			return "DT_DOTA_Unit_Hero_Phoenix";
			
		//Non-Heroes
		//Items 
		case "Courier":
			return "DT_DOTA_Unit_Courier";
		case "Observer Ward":
			return "DT_DOTA_NPC_Observer_Ward_TrueSight";
		case "Sentry Ward":
			return "DT_DOTA_NPC_Observer_Ward_TrueSight";
			
		//Buildings
		case "Fountain":
			return "DT_DOTA_Unit_Fountain";
		
		case "Radiant Tower T1 Top":
		case "Radiant Tower T1 Middle":
		case "Radiant Tower T1 Bottom":
		case "Radiant Tower T2 Top":
		case "Radiant Tower T2 Middle":
		case "Radiant Tower T2 Bottom":
		case "Radiant Tower T3 Top":
		case "Radiant Tower T3 Middle":
		case "Radiant Tower T3 Bottom":
		case "Radiant Tower T4":
			return "DT_DOTA_BaseNPC_Tower";
			
		case "Radiant Melee Barracks Top":
		case "Radiant Ranged Barracks Top":
		case "Radiant Melee Barracks Bottom":	
		case "Radiant Ranged Barracks Bottom":	
		case "Radiant Melee Barracks Middle":
		case "Radiant Ranged Barracks Middle":
			return "DT_DOTA_BaseNPC_Barracks";

		case "Radiant Building":
			return "DT_DOTA_BaseNPC_Building";
		
		case "Radiant Ancient":
			return "DT_DOTA_BaseNPC_Fort";
			
			
		case "Dire Tower T1 Top":
		case "Dire Tower T1 Middle":
		case "Dire Tower T1 Bottom":
		case "Dire Tower T2 Top":
		case "Dire Tower T2 Middle":
		case "Dire Tower T2 Bottom":
		case "Dire Tower T3 Top":
		case "Dire Tower T3 Middle":
		case "Dire Tower T3 Bottom":
		case "Dire Tower T4":
			return "DT_DOTA_BaseNPC_Tower";

		case "Dire Melee Barracks Top":
		case "Dire Ranged Barracks Top":
		case "Dire Melee Barracks Bottom":
		case "Dire Ranged Barracks Bottom":	
		case "Dire Melee Barracks Middle":
		case "Dire Ranged Barracks Middle":
			return "DT_DOTA_BaseNPC_Barracks";

		case "Dire Building":
			return "DT_DOTA_BaseNPC_Building";
		case "Dire Ancient":
			return "DT_DOTA_BaseNPC_Fort";
			
		//Creeps
		case "Radiant Melee Creep":
			return "DT_DOTA_BaseNPC_Creep_Lane";
		case "Radiant Ranged Creep":
			return "DT_DOTA_BaseNPC_Creep_Lane";
		case "Radiant Siege Creep":
			return "DT_DOTA_BaseNPC_Creep_Siege";
		case "Radiant Mega Melee Creep":
			return "DT_DOTA_BaseNPC_Creep_Lane";
		case "Radiant Mega Ranged Creep":
			return "DT_DOTA_BaseNPC_Creep_Lane";
		case "Radiant Mega Siege Creep":
			return "DT_DOTA_BaseNPC_Creep_Siege";
			
		case "Dire Melee Creep":
			return "DT_DOTA_BaseNPC_Creep_Lane";
		case "Dire Ranged Creep":
			return "DT_DOTA_BaseNPC_Creep_Lane";
		case "Dire Siege Creep":
			return "DT_DOTA_BaseNPC_Creep_Siege";
		case "Dire Mega Melee Creep":
			return "DT_DOTA_BaseNPC_Creep_Lane";
		case "Dire Mega Ranged Creep":
			return "DT_DOTA_BaseNPC_Creep_Lane";
		case "Dire Mega Siege Creep":
			return "DT_DOTA_BaseNPC_Creep_Siege";
			
		//Neutrals
		case "Roshan":
			return "DT_DOTA_Unit_Roshan";
		
		case "Black Dragon":
		case "Black Drake":
			
		case "Granite Golem":
		case "Rock Golem":
		
		case "Thunderhide":
		case "Rumblehide":
			
		case "Dark Troll Summoner":
		case "Dark Troll":
		
		case "Hellbear Smasher":
		case "Hellbear":
		
		case "Wildwing Ripper":
		case "Wildwing":
		
		case "Satyr Hellcaller":
		case "Satyr Soulstealer":
		case "Satyr Trickster":
		
		case "Centaur Khan":
		case "Centaur Outrunner":
		
		case "Alpha Wolf":
		case "Giant Wolf":
		
		case "Mud Golem":
		
		case "Ogre Frostmage":
		case "Ogre Bruiser":
		
		case "Harpy Stormcrafter":
		case "Harpy Scout":
			
		case "Kobold Foreman":
		case "Kobold":
		case "Kobold Tunneler":
		
		case "Forest Troll Berserker":
		
		case "Ghost":
		case "Fell Spirit":
			
		case "Vhoul Assassin":
			return "DT_DOTA_BaseNPC_Creep_Neutral";
		
		//Player spawns
		case "Necronomicon Warrior Lvl 1":
		case "Necronomicon Archer Lvl 1":
		case "Necronomicon Warrior Lvl 2":
		case "Necronomicon Archer Lvl 2":
		case "Necronomicon Warrior Lvl 3":
		case "Necronomicon Archer Lvl 3":
		case "Treant":
			return "DT_DOTA_BaseNPC_Creep";
	
		default:
			System.out.println("Unknown DT: "+name);
			return "Unknown: "+name;

		}
	}

	public static String nameForIndex(int index) {
		switch(index){
		case -1: 
			return "Unknown index";
		case 10:
			return "Mirana";
		case 11:
			return "Shadow Fiend";
		case 17:
			return "Sand King";
		case 21: 
			return "Vengeful Spirit";
		case 22: 
			return "Windranger";
		case 23:
			return "Zeus";
		case 24: 
			return "Kunkka";
		case 41:
			return "Faceless Void";
		case 47:
			return "Viper";
		case 49:
			return "Dragon Knight";
		case 53:
			return "Nature's Prophet";
		case 62:
			return "Bounty Hunter";
		case 65:
			return "Batrider";
		case 66:
			return "Chen";
		case 73:
			return "Alchemist";
		case 76:
			return "Outworld Devourer";
		case 86:
			return "Rubick";
		case 97:
			return "Magnus";
		
		case 116:
			return "Dire Ranged Creep";
		case 117:
			return "Dire Mega Ranged Creep";
		case 118:
			return "Radiant Ranged Creep";
		case 119:
			return "Radiant Mega Ranged Creep";
		case 120:
			return "Dire Melee Creep";
		case 121:
			return "Dire Mega Melee Creep";
		case 122:
			return "Radiant Melee Creep";
		case 123:
			return "Radiant Mega Melee Creep";
		case 124:
			return "Radiant Tower T1 Top";
		case 125:
			return "Radiant Tower T1 Middle";
		case 126:
			return "Radiant Tower T1 Bottom";
		case 127:
			return "Radiant Tower T2 Top";
		case 128:
			return "Radiant Tower T2 Middle";
		case 129:
			return "Radiant Tower T2 Bottom";
		case 130:
			return "Radiant Tower T3 Top";
		case 131:
			return "Radiant Tower T3 Middle";
		case 132:
			return "Radiant Tower T3 Bottom";
		case 133:
			return "Radiant Tower T4";
		case 134:
			return "Dire Tower T1 Top";
		case 135:
			return "Dire Tower T1 Middle";
		case 136:
			return "Dire Tower T1 Bottom";
		case 137:
			return "Dire Tower T2 Top";
		case 138:
			return "Dire Tower T2 Middle";
		case 139:
			return "Dire Tower T2 Bottom";
		case 140:
			return "Dire Tower T3 Top";
		case 141:
			return "Dire Tower T3 Middle";
		case 142:
			return "Dire Tower T3 Bottom";
		case 143:
			return "Dire Tower T4";
		case 144:
			return "Radiant Building";
		case 145:
			return "Dire Building";
		case 146:
			return "Radiant Melee Barracks Top";
		case 147:
			return "Radiant Melee Barracks Middle";
		case 148:
			return "Radiant Melee Barracks Bottom";
		case 149:
			return "Radiant Ranged Barracks Top";
		case 150:
			return "Radiant Ranged Barracks Middle";
		case 151:
			return "Radiant Ranged Barracks Bottom";
		case 152:
			return "Dire Melee Barracks Top";
		case 153:
			return "Dire Melee Barracks Middle";
		case 154:
			return "Dire Melee Barracks Bottom";
		case 155:
			return "Dire Ranged Barracks Top";
		case 156:
			return "Dire Ranged Barracks Middle";
		case 157:
			return "Dire Ranged Barracks Bottom";
		case 158:
			return "Radiant Ancient";
		case 159:
			return "Dire Ancient";
		case 160:
			return "Radiant Siege Creep";
		case 161:
			return "Radiant Mega Siege Creep";
		case 162:
			return "Dire Siege Creep";
		case 163:
			return "Dire Mega Siege Creep";
		case 172:
			return "Kobold";
		case 173:
			return "Kobold Tunneler";
		case 174:
			return "Kobold Foreman";
		case 175:
			return "Centaur Outrunner";
		case 176:
			return "Centaur Khan";
		case 177:
			return "Fell Spirit";
		case 178:
			return "Hellbear Smasher";
		case 179:
			return "Hellbear";
		case 180:
			return "Mud Golem";
		case 181:
			return "Ogre Bruiser";
		case 182:
			return "Ogre Frostmage";
		case 183:
			return "Giant Wolf";
		case 184:
			return "Alpha Wolf";
		case 185:
			return "Wildwing";
		case 186:
			return "Wildwing Ripper";
		case 187:
			return "Satyr Soulstealer";
		case 188:
			return "Satyr Hellcaller";
		case 193:
			return "Rock Golem";
		case 194:
			return "Granite Golem";
		case 195:
			return "Thunderhide";
		case 196:
			return "Rumblehide";
		case 197:
			return "Vhoul Assassin";
		case 198:
			return "Ghost";
		case 199:
			return "Dark Troll";
		case 200:
			return "Dark Troll Summoner";
		case 201:
			return "Satyr Trickster";
		case 202:
			return "Forest Troll Berserker";
		case 204:
			return "Harpy Scout";
		case 205:
			return "Harpy Stormcrafter";
		case 206:
			return "Black Drake";
		case 207:
			return "Black Dragon";
		case 208:
			return "Necronomicon Warrior Lvl 1";
		case 209:
			return "Necronomicon Warrior Lvl 2";
		case 210:
			return "Necronomicon Warrior Lvl 3";
		case 211:
			return "Necronomicon Archer Lvl 1";
		case 212:
			return "Necronomicon Archer Lvl 2";
		case 213:
			return "Necronomicon Archer Lvl 3";
		case 214:
			return "Observer Ward";
		case 215:
			return "Sentry Ward";
		case 216:
			return "Courier";
		case 230:
			return "Treant";
		default: 
			return "Unknown index: "+index;
		}
	}
	
	public static String animationAction(int action){
		switch(action){
		case 0:
			return "Walking";
		case 424:
			return "AttackDefault";
		case 425:
			return "AttackVariant";
		case 426:
			return "AttackSpecial";//Example: Jinada hit
		case 431:
			return "AbilitySlot1";
		case 432:
			return "AbilitySlot2";
		case 433:
			return "AbilitySlot3";
		case 434:
			return "AbilitySlot4";
		case 435:
			return "AbilitySlot5";
		case 436:
			return "AbilitySlot6";
		case 438:
			return "AbilitySpecial";//Known cases: Start of sandking's sandstorm from walking
		default:
			//System.out.println("Animation: "+action);
			return ""+action;
		}
	}
	
	public static String animationType(int type){
		switch(type){
		case 0:
			return "?";
		case 1:
			return "Attack?";
		case 2:
			return "AbilityUse";
		default:
			//System.out.println("Animation: "+action);
			return ""+type;
		}
	}
	
	public static String formatTime(double time){
		return "["+ (int)(time/60)+":"+(int)(time%60)+"."+(int)((time*1000)%1000)+ "]";
	}
	
	public static double replay_tick = 0.066;
	
	public static boolean isMelee(int attackCapabilities){
		switch(attackCapabilities){
		case 1:
			return true;
		case 2:
			return false;
		default:
			System.out.println("Unknown attack capability: "+attackCapabilities);
			return false;
		}
	}
	
	public static boolean isAlive(int lifeState){
		switch(lifeState){
		case 0:
			return true;
		case 1:
			return false;
		case 2:
			return false;
		default:
			System.out.println("unknown lifeState: "+lifeState);
			return false;
		}
	}
	
	public static String team(int teamNum){
		switch(teamNum){
		case 0:
			return "Unassigned";
		case 1:
			return "Spectator";
		case 2:
			return "Radiant";
		case 3:
			return "Dire";
		case 4:
			return "Neutral";
		default:
			System.out.println("unknown teamNum: "+teamNum);
			return "";
		}
	}

	public static boolean isAttackProjectile(String projectileName) {
		switch(projectileName){
		case "Attack":
			return true;
		default:
			return false;
		}
	}

	public static String projectileForParticle(String particleName) {
		switch(particleName){
		case "ranged_goodguy":
		case "ranged_badguy":
		case "ranged_siege_good":
		case "ranged_siege_bad":
		case "ranged_tower_good":
		case "ranged_tower_bad":
			
		case "furion_base_attack":
		case "zuus_base_attack":
		case "windrunner_base_attack":
		case "nevermore_base_attack":
		case "vengeful_base_attack":
		case "viper_base_attack":
		case "viper_poison_attack":
		case "mirana_base_attack":

		case "dragon_knight_elder_dragon_corrosive":
		case "dragon_knight_elder_dragon_fire":
		case "dragon_knight_elder_dragon_frost":
		
		case "black_drake_attack":
		case "black_dragon_attack":
		case "thunderlizard_base_attack":
		case "satyr_trickster_projectile":
		case "gnoll_base_attack":
		case "ghost_base_attack":
		case "necronomicon_archer_projectile":
		case "desolator_projectile":
			return "Attack";
		
		case "mirana_spell_arrow":
			return "Sacred Arrow";
		case "dragon_knight_breathe_fire":
			return "Breathe Fire";
		case "vengeful_wave_of_terror":
			return "Wave of Terror";
		case "windrunner_shackleshot":
			return "Shackleshot";
		case "windrunner_spell_powershot_sparrowhawk":
			return "Powershot";
		case "bounty_hunter_suriken_toss":
			return "Shuriken Toss";
		case "vengeful_magic_missle":
			return "Magic Missile";
			
		case "error":
			return "Error";
		default:
			System.out.println("Unknown particle-projectile: "+particleName);
			return "Unknown";
		}
	}
	
	public enum ParticleType{
		EffectEntity,
		AffectedUnit,
		Redundant,
		Cosmetic,
		Unknown
	}
	
	public static ParticleType particleType(String particle){
		switch(particle){
		//Shadow Fiend
		case "nevermore_necro_souls":
		case "nevermore_souls":
		case "nevermore_requiemofsouls":
		case "nevermore_wings":
			return ParticleType.Redundant;
		case "nevermore_shadowraze":
			return ParticleType.EffectEntity;
			
			//Bounty Hunter
		case "bounty_hunter_windwalk":
		case "bounty_hunter_track_cast":
		case "bounty_hunter_track_Shield":
		case "bounty_hunter_track_trail":
		case "bounty_hunter_track_haste":
			return ParticleType.Redundant;
		case "bounty_hunter_hand_R_local":
		case "bounty_hunter_hand_L_local":
			return ParticleType.Cosmetic;
			
		//Nature's Prophet
		case "furion_force_of_nature_cast":
		case "furion_wrath_of_nature_cast":
			return ParticleType.Redundant;
		case "furion_wrath_of_nature_start":
		case "furion_wrath_of_nature":
			return ParticleType.AffectedUnit;
		case "Furion_Sprout":
		case "furion_teleport":
		case "furion_teleport_end_team":
			return ParticleType.EffectEntity;
			
		//Sand King
		case "sandking_caustic_finale_debuff":
		case "sandking_sandstorm":
			return ParticleType.Redundant;
		case "sandking_caustic_finale_explode":
		case "sandking_epicenter_tell":
		case "sandking_epicenter":
		case "sandking_burrowstrike":
			return ParticleType.EffectEntity;
			
		//Vengeful Spirit
		case "vengeful_negative_aura":
		case "vengeful_nether_swap":
		case "vengeful_nether_swap_target":
			return ParticleType.Redundant;
			
		//Dragon Knight
		case "dragon_knight_dragon_tail_knightform_iron_dragon":
		case "dragon_knight_dragon_tail_dragonform_iron_dragon":
		case "dragon_knight_transform_green":
		case "dragon_knight_transform_red":
		case "dragon_knight_transform_blue":
			return ParticleType.Redundant;
			
		//Mirana
		case "mirana_starfall_attack":
			return ParticleType.AffectedUnit;
		case "mirana_moonlight_cast":
		case "mirana_moonlight_recipient":
		case "mirana_starfall_moonray":
		case "mirana_spell_arrow_fx":
			return ParticleType.Redundant;
			
		//Zeus		
		case "zuus_lightning_bolt_start":
		case "zuus_thundergods_wrath_start":
			return ParticleType.Redundant;
		case "zuus_lightning_bolt":
			return ParticleType.EffectEntity;
		case "zuus_arc_lightning_head":
		case "zuus_arc_lightning":
		case "zuus_static_field":
		case "zuus_thundergods_wrath":
			return ParticleType.AffectedUnit;
					
		//Windranger
		case "windrunner_shackleshot_single":
			return ParticleType.Redundant;
		case "windrunner_shackleshot_pair":
		case "windrunner_shackleshot_pair_tree":
			return ParticleType.AffectedUnit;
			
		//Viper
		case "viper_viper_strike_warmup":
		case "viper_viper_strike_beam":
			return ParticleType.Redundant;
			
		//Heroes
		case "death_tombstone":
			return ParticleType.AffectedUnit;
		case "hero_levelup":
		case "respawn":
		case "illusion_created":
		case "illusion_killed":
		case "aegis_respawn_timer":
			return ParticleType.Redundant;
		
		//Ambient
		case "dropped_item":
		case "dropped_dust":
			return ParticleType.EffectEntity;
		case "aegis_timer":
		case "roshan_timer":
		case "building_damage_minor":
		case "building_damage_major":
			return ParticleType.Redundant;
			
		//Neutrals
		case "neutral_centaur_khan_war_stomp":
			return ParticleType.EffectEntity;
			
		//General:
		case "immunity_sphere":
		case "generic_manaburn":
		case "generic_minibash":
		case "chain_lightning":
			return ParticleType.AffectedUnit;
			
		//Items
		case "teleport_start":
		case "teleport_start_ti4":
		case "dust_of_appearance":
		case "dust_of_appearance_debuff":
		case "smoke_of_deceit":
		case "magic_stick":
		case "magic_wand":
		case "refresher":
		case "veil_of_discord":
		case "necronomicon_spawn":
		case "necronomicon_spawn_warrior":
		case "necronomicon_warrior_last_will":
		case "necronomicon_archer_manaburn":
		case "necronomicon_true_sight":
		case "pipe_of_insight_launch":
		case "pipe_of_insight":
		case "ward_spawn_generic":
		case "arcane_boots":
		case "blink_dagger_start":
		case "blink_dagger_start_ti4":
			return ParticleType.Redundant;
			
		case "blink_dagger_end":
		case "blink_dagger_end_ti4":
		case "teleport_end":
		case "teleport_end_ti4":
			return ParticleType.EffectEntity;
			
		case "orchid_pop":
		case "arcane_boots_recipient":
		case "ethereal_blade":
			return ParticleType.AffectedUnit;
			
		case "":
			return ParticleType.Unknown;
		
		default:
			//System.out.println("Unhandled particle "+particle);
			//Globals.countString(particle);
			return ParticleType.Unknown;
		}
	}
	
	public static String clickType(int orderType){
		switch(orderType){
		case 1:
			return "MoveGround";
		case 2:
			return "MoveUnit";
		case 3:
			return "AttackGround";
		case 4:
			return "AttackUnit";
		case 5:
			return "CastGround";
		case 6:
			return "CastUnit";
		case 7:
		 	return "CastTree";
		case 12:
			return "DropItem";
		case 13:
			return "GiveItem";
		case 14:
			return "PickupItem";
		case 15:
			return "PickRune";
		case 19:
			return "RepositionItem";
		default:
			System.out.println("Unknown orderType "+orderType);
			return "";
		}
	}
}
