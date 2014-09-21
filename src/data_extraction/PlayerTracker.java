package data_extraction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.vecmath.Vector2f;

import database.Constants;
import database.Database;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.ReceiveProp;
import utils.ConstantMapper;
import utils.Utils;

public class PlayerTracker {
	private Database db;
	private ReplayData replay;
	private UnitTracker units;
	
	private Vector2f[] cursors;
	private Vector2f[] cams;
	
	private Match currentMatch;
	
	public PlayerTracker(Database db, ReplayData replay, UnitTracker units){
		this.db = db;
		this.replay = replay;
		this.units = units;
		cursors = new Vector2f[10];
		cams = new Vector2f[10];
		for(int i = 0; i < 10; ++i){
			cursors[i] = new Vector2f();
			cams[i] = new Vector2f();
		}
	}
	
	
	private void printPropChange(Entity e, Entity old, String name){
		//System.out.println(name+" "+e.getProperty(name)+" "+old.getProperty(name)+" "+e.getProperty(name).equals(old.getProperty(name)));
		if(!e.getProperty(name).equals(old.getProperty(name))){
			if(name.equals("dota_commentator_table.m_hSpectatorQueryUnit"))
				System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+e.getProperty("m_iPlayerID")+" "+name+" "+
			(currentMatch.getEntities().getByHandle((Integer)old.getProperty(name)) != null ?currentMatch.getEntities().getByHandle((Integer)old.getProperty(name)).getDtClass().getDtName(): "")
			+" -> "+
			(currentMatch.getEntities().getByHandle((Integer)e.getProperty(name)) != null ?currentMatch.getEntities().getByHandle((Integer)e.getProperty(name)).getDtClass().getDtName(): "")
			);
			
			else
				System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+e.getProperty("m_iPlayerID")+" "+name+" "+old.getProperty(name)+" -> "+e.getProperty(name));
	
		}
	}
	
	public void update(Match match, Match oldMatch){
		currentMatch = match;
		
		Iterator<Entity> it = match.getEntities().getAllByDtName("DT_DOTAPlayer");
		while(it.hasNext()){
			Entity e = it.next();
			Entity eOld = oldMatch.getEntities().getByHandle(e.getHandle());
			if(eOld == null)
				continue;
			if(oldMatch.getEntities().getByHandle(e.getHandle()) != null){
				for(ReceiveProp p : e.getDtClass().getReceiveProps()){
					if(p.getVarName().equals("dota_commentator_table.m_iCursor.0000")||
							p.getVarName().equals("dota_commentator_table.m_iCursor.0001")||
							p.getVarName().equals("dota_commentator_table.m_vecOrigin")||
							p.getVarName().equals("dota_commentator_table.m_cellX")||
							p.getVarName().equals("dota_commentator_table.m_cellY")||
							p.getVarName().equals("m_hViewModel")||
							p.getVarName().equals("localdata.m_nTickBase")||
							p.getVarName().equals("dota_commentator_table.m_iQuickBuyParity")||
							p.getVarName().equals("dota_commentator_table.m_iSpectatorClickBehavior")||
							p.getVarName().equals("m_flMusicOperatorVals.0000")
							)
						continue;
					printPropChange(e, oldMatch.getEntities().getByHandle(e.getHandle()), p.getVarName());
				}
			}
			
			String team = ConstantMapper.team((Integer)e.getProperty("m_iTeamNum"));
			if(!(team.equals("Radiant") || team.equals("Dire")))
				continue;
			int playerNr = (Integer)e.getProperty("m_iPlayerID");
			int playerID = replay.getPlayerID(playerNr);
			cursors[playerNr].set((Integer)e.getProperty("dota_commentator_table.m_iCursor.0000")/511.0f, (Integer)e.getProperty("dota_commentator_table.m_iCursor.0001")/384.0f);
			cams[playerNr].set(Utils.getPosition(e, "dota_commentator_table."));
			
			//TODO Save Cursor+Cam to DB
			if(playerNr == 5)
				System.out.println(ConstantMapper.formatTime(Utils.getTime(currentMatch))+" "+cursors[playerNr] + " "+cams[playerNr]);
			Globals.countInt((int)cursors[playerNr].y);
			//X range 0-511
			//Y range 0-384
			Object[] viewm = e.getProperty("m_hViewModel");
			if(viewm.length > 0)
				System.out.println("viewmodel "+viewm.length+" "+Arrays.toString(viewm));
			
			int quickbuy_item;
			int quickbuy_itemOld;
			int quickbuy_purchasable;
			int quickbuy_purchasableOld;
			
			Set<Integer> quickbuyItems = new HashSet<Integer>();
			Set<Integer> quickbuyItemsOld = new HashSet<Integer>();
			
			for(int i = 0; i< 9; ++i){
				//System.out.println("dota_commentator_table.m_vecClientQuickBuyState."+String.format("%04d", i)+".nItemType");
				quickbuy_item = e.getProperty("dota_commentator_table.m_vecClientQuickBuyState."+String.format("%04d", i)+".nItemType");
				quickbuy_itemOld = eOld.getProperty("dota_commentator_table.m_vecClientQuickBuyState."+String.format("%04d", i)+".nItemType");
				quickbuy_purchasable = e.getProperty("dota_commentator_table.m_vecClientQuickBuyState."+String.format("%04d", i)+".bPurchasable");
				quickbuy_purchasableOld = eOld.getProperty("dota_commentator_table.m_vecClientQuickBuyState."+String.format("%04d", i)+".bPurchasable");
				if(quickbuy_item != 0 && quickbuy_item != 5001){
					quickbuyItems.add(quickbuy_item);
				}
				if(quickbuy_itemOld != 0 && quickbuy_itemOld != 5001){
					quickbuyItemsOld.add(quickbuy_itemOld);
				}
				
				if(quickbuy_item != 0 && quickbuy_item != 5001 && quickbuy_item == quickbuy_itemOld && quickbuy_purchasable != quickbuy_purchasableOld){
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "QuickbuyPurchasabilityChange");
					db.addEventIntArgument(eventID, "Player", playerID);
					db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemForType(quickbuy_item)));
					db.addEventIntArgument(eventID, "Availability", quickbuy_purchasable);
				}
			}
			
			if(!quickbuyItems.equals(quickbuyItemsOld)){
				for(Integer item : quickbuyItems){
					if(!quickbuyItemsOld.contains(item)){
						int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "QuickbuyAddition");
						db.addEventIntArgument(eventID, "Player", playerID);
						db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemForType(item)));
						System.out.println(playerNr+ " Added to quickbuy "+item);
					}						
				}
				for(Integer item : quickbuyItemsOld){
					if(!quickbuyItems.contains(item)){
						int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "QuickbuyRemoval");
						db.addEventIntArgument(eventID, "Player", playerID);
						db.addEventIntArgument(eventID, "Item", Constants.getIndex("Items", ConstantMapper.itemForType(item)));
						System.out.println(playerNr+ " removed from quickbuy "+item);
					}						
				}
			}
			
			if(!e.getProperty("dota_commentator_table.m_iShopPanel").equals(eOld.getProperty("dota_commentator_table.m_iShopPanel"))){
				if((Integer)e.getProperty("dota_commentator_table.m_iShopPanel") ==  0){
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ClosePanel");
					db.addEventIntArgument(eventID, "Player", playerID);
					db.addEventIntArgument(eventID, "PanelType", Constants.getIndex("Panels", "Shop"));
					System.out.println("closed Shop");
				}
				else{
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "OpenPanel");
					db.addEventIntArgument(eventID, "Player", playerID);
					db.addEventIntArgument(eventID, "PanelType", Constants.getIndex("Panels", "Shop"));
					System.out.println("Opened Shop");
				}
			}
			
			if(!e.getProperty("dota_commentator_table.m_iStatsPanel").equals(eOld.getProperty("dota_commentator_table.m_iStatsPanel"))){
				if((Integer)e.getProperty("dota_commentator_table.m_iStatsPanel") ==  0){
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "ClosePanel");
					db.addEventIntArgument(eventID, "Player", playerID);
					db.addEventIntArgument(eventID, "PanelType", Constants.getIndex("Panels", "Scoreboard"));
					System.out.println("closed scoreboard");
				}
				else{
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "OpenPanel");
					db.addEventIntArgument(eventID, "Player", playerID);
					db.addEventIntArgument(eventID, "PanelType", Constants.getIndex("Panels", "Scoreboard"));
					System.out.println("Opened scoreboard");
				}
			}	
			if(!e.getProperty("dota_commentator_table.m_hSpectatorQueryUnit").equals(eOld.getProperty("dota_commentator_table.m_hSpectatorQueryUnit"))){
				if((Integer)e.getProperty("dota_commentator_table.m_hSpectatorQueryUnit") ==  2097151){
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "SelectSelf");
					db.addEventIntArgument(eventID, "Player", playerID);
					System.out.println("SelectSelf");
				}
				else{
					int eventID = db.createEvent(replay.getReplayID(), Utils.getTime(match), "SelectUnit");
					db.addEventIntArgument(eventID, "Player", playerID);
					db.addEventIntArgument(eventID, "PanelType", Constants.getIndex("Panels", "Scoreboard"));
					System.out.println("SelectUnit");
				}
			}
			/*System.out.println(e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0000.nItemType")+" "
								+e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0001.nItemType")+" "
								+e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0002.nItemType")+" "
								+e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0003.nItemType")+" "
								+e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0004.nItemType")+" "
								+e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0005.nItemType")+" "
								+e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0006.nItemType")+" "
								+e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0007.nItemType")+" "
								+e.getProperty("dota_commentator_table.m_vecClientQuickBuyState.0008.nItemType")+" ");
			Entity obst = match.getEntities().getByHandle((Integer)e.getProperty("m_hObserverTarget"));
			System.out.println(obst!= null ? obst.getDtClass().getDtName() : "no obs");
			//System.out.println((Integer)e.getProperty("m_hViewModel"));
			
			
			Entity viewe = match.getEntities().getByHandle((Integer)e.getProperty("m_hViewEntity"));
			System.out.println(viewe!= null ? viewe.toString() : "no viewe");	
			System.out.println();
			System.out.println(e.toString());*/
		}
	}
	
	public Vector2f getMouse(int playerNr){
		return cursors[playerNr];
	}
}
