/**
 * 
 */
package cn.edu.zju.isst.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.edu.zju.isst.util.L;

/**
 * 数据库辅助类
 * 
 * @author theasir
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	/**
	 * 数据库名称（文件名）
	 */
	private static final String DATABASE_NAME = "main.db";
	/**
	 * 数据库版本
	 */
	private static final int DATABASE_VERSION = 1;

	private static DBHelper INSTANCE;
	
	/**
	 * @param context
	 */
	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public synchronized static void createInstance(Context context){
		if (INSTANCE == null) {
			INSTANCE = new DBHelper(context.getApplicationContext());
		}
	}
	
	public synchronized static DBHelper getInstance(){
		if (INSTANCE == null) {
			throw new IllegalStateException(
                    "DBHelper::createInstance() needs to be called "
                            + "before DBHelper::getInstance()");
		}
		return INSTANCE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists main (name TEXT PRIMARY KEY, object BLOB)");
        //archive表
        db.execSQL("create table if not exists archive (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,title VARCHAR,description VARCHAR,updatedAt INTEGER," +
                "publisherId INTEGER,publisher BLOB,content VARCHAR)");
        //user表
        db.execSQL("create table if not exists user (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,user VARCHAR,password VARCHAR," +
                "name VARCHAR,gender INTEGER,grade INTEGER,classId INTEGER,major VARCHAR,cityId INTEGER,email VARCHAR,phone VARCHAR,qq VARCHAR," +
                "company VARCHAR,position VARCHAR,signature VARCHAR，cityPrincipal BOOLEAN,privateQQ BOOLEAN,privateEmail BOOLEAN,privatePhone BOOLEAN," +
                "privateCompany BOOLEAN,privatePosition BOOLEAN)");
        //city表
        db.execSQL("create table if not exists city (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,name VARCHAR,cityMaster BLOB)");
        //cityactivity表
        db.execSQL("create table if not exists cityactivity (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,title VARCHAR,imgUrl VARCHAR," +
                "cityId INTEGER,location VARCHAR,startTime INTEGER,expireTime INTEGER,updatedAt INTEGER,content VARCHAR,isParticipate BOOLEAN,publisher BLOB)");
		//comment表
        db.execSQL("create table if not exists comment (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,title VARCHAR,content VARCHAR,createdAt INTEGER,User BLOB)");
        //job表
        db.execSQL("create table if not exists job (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,title VARCHAR,company VARCHAR,position VARCHAR,description VARCHAR," +
                "updatedAt INTEGER,publisherId BLOB,content VARCHAR)");
        //klass表
        db.execSQL("create table if not exists klass (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,name VARCHAR)");
        //major表
        db.execSQL("create table if not exists major (_id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR)");
        //myparticipatedactivity表
        db.execSQL("create table if not exists myparticipatedactivity (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,title VARCHAR,imgUrl VARCHAR," +
                "cityId INTEGER,location VARCHAR,startTime INTEGER,expireTime INTEGER,updatedAt INTEGER,content VARCHAR,isParticipate BOOLEAN,publisher BLOB)");
        //mypublicactivity表
        db.execSQL("create table if not exists mypublicactivity (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,title VARCHAR,imgUrl VARCHAR,"+
                "cityId INTEGER,location VARCHAR,startTime INTEGER,expireTime INTEGER,updatedAt INTEGER,content VARCHAR,isParticipate BOOLEAN,publisher BLOB)");
        //publisher表
        db.execSQL("create table if not exists publisher (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,name VARCHAR,phone VARCHAR,qq VARCHAR,email VARCHAR)");
        //新建pushmessage表
        db.execSQL("create table if not exists pushmessage (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,title VARCHAR,content VARCHAR,createdTime INTEGER)");
        //restaurant表
        db.execSQL("create table if not exists restaurant (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,name VARCHAR,picture VARCHAR,address VARCHAR,hotline VARCHAR," +
                "businessHours VARCHAR,description VARCHAR,content VARCHAR)");
        //restaurantmenu表
        db.execSQL("create table if not exists restaurantmenu (_id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR,picture VARCHAR,description VARCHAR,price FLOAT)");
        //usercenterlist表
        db.execSQL("create table if not exists usercenterlist (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,title VARCHAR,content VARCHAR,updateAt INTEGER)");
        L.i("Create DB!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
