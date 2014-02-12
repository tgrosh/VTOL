package com.singletongames.vtol;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChapterDB extends SQLiteOpenHelper {
	private static ChapterDB instance = new ChapterDB(Resources.mActivity);
	
	static final String dbName = "VTOLDB.Chapters";
	static final String tTableName = "Chapters";
	static final String fID = "ID";
	static final String fName = "Name";
	static final String fLocked = "Locked";
	static final String fFinished = "ChapterFinished";
	private List<Chapter> mAllChapters = null;
	
	static final int mDBVersion = 7;
	
	public ChapterDB(Context context) {
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
				fLocked + " TEXT, " +
				fFinished + " TEXT " +
				")");

		addChapter(db, 0, "Training", false, false);
		addChapter(db, 1, "Overworld", true, false);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+tTableName);
		onCreate(db);
	}

	public List<Chapter> allChapters(){
		if (mAllChapters == null){
			mAllChapters = new ArrayList<Chapter>();
			
			Cursor myCursor = this.getReadableDatabase().rawQuery("SELECT * FROM "+tTableName, null);
			while (myCursor.moveToNext()){
				int ID = myCursor.getInt(0);
				String Name = myCursor.getString(1);
				boolean Locked = myCursor.getString(2).equals("true");
				boolean Finished = myCursor.getString(3).equals("true");
				
				Chapter chapter = new Chapter(Name, ID, Locked, Finished);
				mAllChapters.add(chapter);
			}
		}

		return mAllChapters;
	}

	private boolean addChapter(SQLiteDatabase db, int ID, String name, boolean pLocked, boolean pFinished){
		ContentValues cv = new ContentValues();
        cv.put(fID, ID);
        cv.put(fName, name);
        cv.put(fLocked, String.valueOf(pLocked));
        cv.put(fFinished, String.valueOf(pFinished));
        if (db.insert(tTableName, "", cv) == -1){
        	return false;
        }
		
		return true;
	}
	public boolean addChapter(Chapter chapter){
		return addChapter(this.getWritableDatabase(), chapter.getId(), chapter.getName(), chapter.isLocked(), chapter.isFinished());
	}
	
	private boolean updateChapter(SQLiteDatabase db, int pID, String name, boolean pLocked, boolean pFinished){
		ContentValues cv = new ContentValues();
		cv.put(fName, name);
		cv.put(fLocked, String.valueOf(pLocked));
        cv.put(fFinished, String.valueOf(pFinished));
        if (db.update(tTableName, cv, fID+"=?", new String[]{String.valueOf(pID)})  == -1){
        	return false;
        }
		
		return true;
	}
	public boolean updateChapter(Chapter chapter){
		return updateChapter(this.getWritableDatabase(),chapter.getId(),chapter.getName(),chapter.isLocked(),chapter.isFinished());
	}

	
	public static ChapterDB getInstance() {
		return instance;
	}
}