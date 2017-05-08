package com.couchdroid.databasebuilder;


import android.content.Context;
import android.util.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;


/**
 * Created by jacobotapia on 02/05/17.
 */

public class DatabaseConfiguration {

    private final String TAG = "CouchbaseEvents";
    private Manager manager;
    private Database database;
    private String dbname;
    /*
     * A little bit of this
     * https://blog.couchbase.com/using-couchbase-lite-in-a-java-gluon-application/
     */
    private static DatabaseConfiguration instance = null;


    /**
     *
     * @param ac Context of Anddroid App
     * @throws IOException
     */

    private DatabaseConfiguration(Context ac) throws IOException {
        int s = ac.getResources().getIdentifier("database_name", "string", ac.getPackageName());
        this.dbname = String.valueOf(ac.getResources().getString(s).trim()).toLowerCase();
        Log.d (TAG, "db_name: "+this.dbname);
        try{
            manager = new Manager(new AndroidContext(ac),Manager.DEFAULT_OPTIONS);
            Log.d (TAG, "Manager created");
        }catch (IOException e){
            Log.e(TAG, "Cannot create manager object");
            return;
        }

        if (!Manager.isValidDatabaseName(this.dbname)) {
            Log.e(TAG, "Bad database name");
            return;
        }

        buildDatabase();
    }


    /**
     * Create a instance of the object if not exist
     * else return existing instance
     * @param ac Context of android app
     * @return Instance of DatabaseConfiguration (Singleton)
     * @throws IOException
     */
    public static DatabaseConfiguration getInstance(Context ac) throws IOException {
        if(instance == null) {
            instance = new DatabaseConfiguration(ac);
        }
        return instance;
    }

    /**
     * Return database for the app
     * @return database
     */
    public Database getDatabase(){
        return database;
    }

    /**
     * Creates/Open database with some name
     */
    public void buildDatabase(){
        try {
            database = manager.getDatabase(this.dbname);
            Log.d (TAG, "Database created and exists: "+database.exists());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot get database");
            return;
        }
    }

}
