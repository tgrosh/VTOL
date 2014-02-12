package com.singletongames.vtol;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LevelDB extends SQLiteOpenHelper {
	private static LevelDB instance = new LevelDB(Resources.mActivity);
	
	static final String dbName = "VTOLDB.Levels";
	static final String tLevels = "Levels";
	static final String fLevelID = "LevelID";
	static final String fLevelName = "LevelName";
	static final String fChapterID = "ChapterID";
	static final String fLevelLocked = "LevelLocked";
	static final String fLevelFinished = "LevelFinished";
	static final String fLevelGemCaptured = "GemCaptured";
	static final String fLevelTime = "LevelTime";
	private List<Level> mAllLevels = new ArrayList<Level>();
	
	static final int mDBVersion = 10;
	
	public LevelDB(Context context) {
		// THE VALUE OF 1 ON THE NEXT LINE REPRESENTS THE VERSION NUMBER OF THE DATABASE
		// IN THE FUTURE IF YOU MAKE CHANGES TO THE DATABASE, YOU NEED TO INCREMENT THIS NUMBER
		// DOING SO WILL CAUSE THE METHOD onUpgrade() TO AUTOMATICALLY GET TRIGGERED
		super(context, dbName, null, mDBVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// ESTABLISH NEW DATABASE TABLES IF THEY DON'T ALREADY EXIST IN THE DATABASE
		db.execSQL("CREATE TABLE IF NOT EXISTS "+tLevels+" (" +				
				fLevelID + " INTEGER , " +				
				fChapterID + " INTEGER, " +
				fLevelName + " TEXT, " +
				fLevelLocked + " TEXT, " +
				fLevelFinished + " TEXT, " +
				fLevelGemCaptured + " TEXT," +
				fLevelTime + " INTEGER" +
				")");

		addLevel(db, 0,1, "Training 1", false, false, false, 0);
		addLevel(db, 0,2, "Training 2", true, false, false, 0);
		addLevel(db, 0,3, "Training 3", true, false, false, 0);
		addLevel(db, 1,1, "Overworld 1", false, false, false, 0);
		addLevel(db, 1,1, "Overworld 2", true, false, false, 0);
		addLevel(db, 1,1, "Overworld 3", true, false, false, 0);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+tLevels);
		onCreate(db);
	}

	public List<Level> allLevels(){
		mAllLevels.clear();
		
		Cursor myCursor = this.getReadableDatabase().rawQuery("SELECT * FROM "+tLevels, null);
		while (myCursor.moveToNext()){
			int LevelID = myCursor.getInt(0);				
			int ChapterID = myCursor.getInt(1);
			String LevelName = myCursor.getString(2);
			String LevelLocked = myCursor.getString(3);
			String LevelFinished = myCursor.getString(4);
			String LevelGemCaptured = myCursor.getString(5);
			int LevelTime = myCursor.getInt(6);
			
			Level lvl = new Level(Resources.mEngine, ChapterID, LevelID, LevelName, Boolean.valueOf(LevelLocked), Boolean.valueOf(LevelFinished), Boolean.valueOf(LevelGemCaptured), LevelTime);
			mAllLevels.add(lvl);
		}
		
		return mAllLevels;
	}
	
	public List<Level> getLevels(int ChapterID){
		List<Level> levels = new ArrayList<Level>();
		for (Level lvl: allLevels()){
			if (lvl.getChapterID() == ChapterID){
				levels.add(lvl);
			}
		}
		return levels;
	}

	public Level getLevel(int chapterID, int levelID) {		
		for (Level lvl: allLevels()){
			if (lvl.getChapterID() == chapterID && lvl.getLevelID() == levelID){
				return lvl;
			}
		}
		return null;
	}
	
	private boolean addLevel(SQLiteDatabase db, int ChapterID, int LevelID, String levelName, boolean pLevelLocked, boolean pLevelFinished, boolean pLevelGemCaptured, int pLevelTime){
		ContentValues cv = new ContentValues();
        cv.put(fLevelID, LevelID);        
        cv.put(fChapterID, ChapterID);
        cv.put(fLevelName, levelName);
        cv.put(fLevelLocked, String.valueOf(pLevelLocked));
        cv.put(fLevelFinished, String.valueOf(pLevelFinished));
        cv.put(fLevelGemCaptured, String.valueOf(pLevelGemCaptured));
        cv.put(fLevelTime, pLevelTime);
        if (db.insert(tLevels, "", cv) == -1){
        	return false;
        }
		
		return true;
	}
	public boolean addLevel(Level lvl){
		return addLevel(this.getWritableDatabase(), lvl.getChapterID(), lvl.getLevelID(), lvl.getName(), lvl.isLevelLocked(), lvl.isLevelFinished(), lvl.isLevelGemCaptured(), lvl.getLevelTime());
	}
	
	private boolean updateLevel(SQLiteDatabase db, int ChapterID, int levelID, String levelName, boolean pLevelLocked, boolean pLevelFinished, boolean pLevelGemCaptured, int pLevelTime){
		ContentValues cv = new ContentValues();
		cv.put(fLevelName, levelName);
        cv.put(fLevelLocked, String.valueOf(pLevelLocked));
        cv.put(fLevelFinished, String.valueOf(pLevelFinished));
        cv.put(fLevelGemCaptured, String.valueOf(pLevelGemCaptured));
        cv.put(fLevelTime, pLevelTime);
        if (db.update(tLevels, cv,fLevelID+"=? and "+fChapterID+"=?", new String[]{String.valueOf(levelID),String.valueOf(ChapterID)}) == -1){
        	return false;
        }
		
		return true;
	}
	public boolean updateLevel(Level lvl){
		return updateLevel(this.getWritableDatabase(),lvl.getChapterID(), lvl.getLevelID(),lvl.getName(),lvl.isLevelLocked(),lvl.isLevelFinished(),lvl.isLevelGemCaptured(),lvl.getLevelTime());
	}

	public static LevelDB getInstance() {
		return instance;
	}

	public boolean unlockLevel(int chapterID, int LevelID){
		ContentValues cv = new ContentValues();
        cv.put(fLevelLocked, String.valueOf(false));
        if (this.getWritableDatabase().update(tLevels, cv,fLevelID + "=" + LevelID + " AND " + fChapterID + "=" + chapterID, null) == -1){
        	return false;
        }
		
		return true;
	}

}