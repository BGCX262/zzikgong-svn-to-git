package com.example.pphotocapsule;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
	private static String DB_PATH = "/data/data/com.example.pphotocapsule/databases/";
	private static String DB_NAME = "photocapsule.sqlite";
	private SQLiteDatabase myDatabase;
	private final Context myContext;
	
	public Database(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	public void createDataBase() throws IOException{
		SQLiteDatabase checkDB = null;
		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}catch(SQLiteException e){
		}
		
		if(checkDB != null){
			checkDB.close();
		}
		
		boolean dbExist = checkDB != null? true : false;
		if(dbExist){
		}
		else{
			this.getReadableDatabase();
			try{
				InputStream myInput = myContext.getAssets().open(DB_NAME);
				String outFileName = DB_PATH + DB_NAME;
				OutputStream myOutput = new FileOutputStream(outFileName);
				byte[] buffer = new byte[1024];
				int length;
				while((length = myInput.read(buffer)) > 0){
					myOutput.write(buffer, 0, length);
				}
				myOutput.flush();
				myOutput.close();
				myInput.close();
			}catch(IOException e){
				throw new Error("Error copying database");
			}
		}
	}
	
	public void openDataBase(){
		String myPath = DB_PATH + DB_NAME;
		myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}
	
	public synchronized void close(){
		if(myDatabase != null)
			myDatabase.close();
		super.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	
	}
	
	public Cursor getExplanation() {
		Cursor c = myDatabase.rawQuery("SELECT checked FROM explanation WHERE id = 1;", null);
		
		if (c != null && c.getCount() != 0)
			c.moveToFirst();

		return c;
	}
	
	public void updateExplanation() {
		myDatabase.execSQL("UPDATE explanation SET checked = 1 WHERE id = 1;");
	}
	
	public void insertRegister(int mYear, int mMonth, int mDay, int mHour, int mMinute, double lat, double lng, String street) {
		myDatabase.execSQL("INSERT INTO register (year, month, date, hour, minute, latitude, longitude, street) VALUES ("+mYear+", "+mMonth+", "+mDay+", "+mHour+", "+mMinute+", "
							+lat+", "+lng+", '"+street+"');");
	}
	
	public void insertRegisterNewImage(String fileName, int mYear, int mMonth, int mDay, int mHour, int mMinute, double lat, double lng, String street) {
		myDatabase.execSQL("INSERT INTO register (title, year, month, date, hour, minute, latitude, longitude, street) VALUES ('"+fileName+"', "+mYear+", "+mMonth+", "+mDay+", "+mHour+", "+mMinute+", "
							+lat+", "+lng+", '"+street+"');");
	}
	
	public void updateRegister(int id, String filename) {
		myDatabase.execSQL("UPDATE register SET title = '"+filename+"' WHERE id = "+id+";");
	}
	
	public void updateRegisterMergedImageToOne(String filename) {
		myDatabase.execSQL("UPDATE register SET mergedImage = 1 WHERE title = '"+filename+"';");
	}
	
	public void updateRegisterMergedImageToZero(String filename) {
		myDatabase.execSQL("UPDATE register SET mergedImage = 0 WHERE title = '"+filename+"';");
	}
	
	public Cursor getMaxId() {
		Cursor c = myDatabase.rawQuery("SELECT MAX(id) FROM register;", null);
		
		if (c != null && c.getCount() != 0)
			c.moveToFirst();

		return c;
	}
	
	public Cursor getInformation(int id) {
		Cursor c = myDatabase.rawQuery("SELECT * FROM register WHERE id = "+ id + ";", null);
		
		if (c != null && c.getCount() != 0)
			c.moveToFirst();

		return c;
	}
	
	public Cursor getInformationThroughFileName(String fileName) {
		Cursor c = myDatabase.rawQuery("SELECT * FROM register WHERE title = '"+ fileName + "';", null);
		
		if (c != null && c.getCount() != 0)
			c.moveToFirst();

		return c;
	}
	
	public Cursor getFileName() {
		Cursor c = myDatabase.rawQuery("SELECT title FROM register WHERE mergedImage = 1;", null);
		
		if (c != null && c.getCount() != 0)
			c.moveToFirst();

		return c;
	}
}
