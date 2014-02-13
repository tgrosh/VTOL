package com.singletongames.vtol;


import java.util.ArrayList;
import java.util.List;

import org.andengine.util.debug.Debug;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LanderDB extends SQLiteOpenHelper {
	private static LanderDB instance = new LanderDB(Resources.mActivity);
	
	static final String dbName = "VTOLDB.Landers";
	static final String tTableName = "Landers";
	static final String fID = "ID";
	static final String fName = "Name";
	static final String fDescription = "Description";
	static final String fMaxThrust = "MaxThrust";
	static final String fMaxFuel = "MaxFuel";
	static final String fFuelPerSecond = "FuelPerSecond";
	static final String fToughness = "Toughness";
	static final String fDensity = "Density";
	static final String fLocked = "Locked";
	
	private List<LanderInfo> mAllLanders = null;
	
	static final int mDBVersion = 12;
	
	public LanderDB(Context context) {
		// THE VALUE OF 1 ON THE NEXT LINE REPRESENTS THE VERSION NUMBER OF THE DATABASE
		// IN THE FUTURE IF YOU MAKE CHANGES TO THE DATABASE, YOU NEED TO INCREMENT THIS NUMBER
		// DOING SO WILL CAUSE THE METHOD onUpgrade() TO AUTOMATICALLY GET TRIGGERED
		super(context, dbName, null, mDBVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// ESTABLISH NEW DATABASE TABLES IF THEY DON'T ALREADY EXIST IN THE DATABASE
		db.execSQL("CREATE TABLE IF NOT EXISTS "+tTableName+" (" +
				fID + " INTEGER PRIMARY KEY , " +
				fName + " TEXT, " +
				fDescription + " TEXT, " +
				fMaxThrust + " REAL, " +
				fMaxFuel + " REAL, " +
				fFuelPerSecond + " REAL, " +
				fToughness + " REAL, " +
				fDensity + " REAL, " +
				fLocked + " TEXT " +
				")");
		
		addLander(db, new LanderInfo(0, "Hauler", "The workhorse of the VTOL fleet, the Hauler is a stable, durable ship, capable of a wide range of missions.", 15f, 300f, 10f, 10f, .1f, false));
		addLander(db, new LanderInfo(1, "Luna", "A mainstay of the V.A.S.A space fleet for decades, this lander excels in low gravity environments.", 12f, 200f, 10f, 3.5f, .09f, true));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+tTableName);
		onCreate(db);
	}

	public List<LanderInfo> allLanders(){
		if (mAllLanders == null){
			mAllLanders = new ArrayList<LanderInfo>();
			
			Cursor myCursor = this.getReadableDatabase().rawQuery("SELECT * FROM "+tTableName, null);
			while (myCursor.moveToNext()){
				int ID = myCursor.getInt(0);
				String Name = myCursor.getString(1);
				String Description = myCursor.getString(2);
				float maxThrust = myCursor.getFloat(3);
				float maxFuel = myCursor.getFloat(4);
				float fuelPerSecond = myCursor.getFloat(5);
				float toughness = myCursor.getFloat(6);
				float density = myCursor.getFloat(7);
				boolean Locked = myCursor.getString(8).equals("true");				
				
				LanderInfo item = new LanderInfo(ID, Name, Description, maxThrust, maxFuel, fuelPerSecond, toughness, density, Locked);
				mAllLanders.add(item);
			}
		}

		return mAllLanders;
	}

	public LanderInfo getLander(int LanderID){
		for (LanderInfo item: allLanders()){
			if (item.getId() == LanderID){
				Debug.w("DEBUG: Using Lander: " + item.toString());
				return item;
			}
		}
		return null;
	}
	
	private boolean addLander(SQLiteDatabase db, LanderInfo info){
		ContentValues cv = new ContentValues();
        cv.put(fID, info.getId());
        cv.put(fName, info.getName());
        cv.put(fDescription, info.getDescription());
        cv.put(fMaxThrust, info.getMaxEngineThrust());
        cv.put(fMaxFuel, info.getMaxFuel());
        cv.put(fFuelPerSecond, info.getFuelPerSecond());
        cv.put(fToughness, info.getToughness());
        cv.put(fDensity, info.getDensity());
        cv.put(fLocked, String.valueOf(info.isLocked()));
        if (db.insert(tTableName, "", cv) == -1){
        	return false;
        }
		
		return true;
	}
	
	private boolean updateLander(SQLiteDatabase db, int pID, LanderInfo info){
		ContentValues cv = new ContentValues();
		cv.put(fName, info.getName());
        cv.put(fDescription, info.getDescription());
        cv.put(fMaxThrust, info.getMaxEngineThrust());
        cv.put(fMaxFuel, info.getMaxFuel());
        cv.put(fFuelPerSecond, info.getFuelPerSecond());
        cv.put(fToughness, info.getToughness());        
        cv.put(fDensity, info.getDensity());
        cv.put(fLocked, String.valueOf(info.isLocked()));
        if (db.update(tTableName, cv, fID + "=?", new String[]{String.valueOf(pID)})  == -1){
        	return false;
        }
		
		return true;
	}

	
	public static LanderDB getInstance() {
		return instance;
	}
}