package com.couchdroid.crud;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchdroid.databasebuilder.DatabaseConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by jacobotapia on 02/05/17.
 */

public class CRUDOperations implements com.couchdroid.operations.CRUD {

    DatabaseConfiguration dc;
    private static CRUDOperations instance;
    private final String TAG = "CouchbaseEvents";

    /**
     * Constructor
     * @param ac Android context of the app
     * @throws IOException
     */
    private CRUDOperations(Context ac) throws IOException {
        dc = DatabaseConfiguration.getInstance(ac);
    }

    /**
     * Return an instance of this object
     * @param ac Android context of the app
     * @return instance of this class
     * @throws IOException
     */
    public static CRUDOperations getInstance(Context ac) throws IOException {
        if(instance == null) {
            instance = new CRUDOperations(ac);
        }
        return instance;
    }

    /**
     * Persist a java object in the database
     * @param o Object to create
     * @return String with the document id of the persisted object
     */
    public String create(Object o){

        Map<String, Object> properties = new HashMap<String, Object>();

        String type = getObjectClassName(o);

        properties.put("type", type);
        properties.put(type, o);

        Document document = null;
        document = this.dc.getDatabase().createDocument();

        Class[] params = {String.class};
        Method method = null;
        try {
            method = o.getClass().getSuperclass().getDeclaredMethod("setId",params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object[] args = {document.getId()};
        try {
            method.invoke(o,args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        String docID = document.getId();
        Document retrievedDocument = null;
        retrievedDocument = this.dc.getDatabase().getDocument(docID);

        Log.d (TAG, "Document written to database named with ID = " + retrievedDocument.getProperties());
        return document.getId();

    }

    /**
     * Given a document id, and a Class
     * return a generic object for that given class
     * @param id
     * @param c
     * @param <T>
     * @return T object of the given class
     */
    public <T> T find(String id,Class c) {
        Document doc = this.dc.getDatabase().getExistingDocument(id);
        if(doc!=null){
            final String type = this.getClassName(c);
            Gson gson = new Gson();
            String jsonString = gson.toJson(doc.getProperties().get(type), Map.class);
            T t = (T) gson.fromJson(jsonString, c);
            return t;
        }else {
            return null;
        }
    }

    /**
     * Given a java object, update his information
     * in the database if exist on the database
     * @param o Object to persist
     * @return true if the object was updated, false if was unable to update
     */
    public boolean update(final Object o){

        Document d = this.findDocument(o);

        if(d!=null){

            final String type = getObjectClassName(o);
            ObjectMapper om = new ObjectMapper();
            final Map<String,Object> map = om.convertValue(o,Map.class);

            try {
                d.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        Map<String, Object> properties = newRevision.getUserProperties();
                        properties.put(type, map);
                        newRevision.setUserProperties(properties);
                        Log.d("BASURA",newRevision.getProperties().toString());
                        return true;
                    }
                });
                return true;
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Return a list of objects in database for a given class
     * @param c Class of objects to query
     * @param <T>
     * @return a list of found objects of the given class in the database
     */
    public <T> List<T> list(Class c){

        final String type = this.getClassName(c);

        UUID idOne = UUID.randomUUID();

        List<T> l = new ArrayList<T>();

        View view = this.dc.getDatabase().getView(type);

        view.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                if(document.get("type").equals(type)) {
                    emitter.emit(document.get("_id"), document.get(type));
                }
            }
        }, idOne.toString());


        Query q = view.createQuery();

        QueryEnumerator r = null;
        try {
            r = q.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        for(Iterator<QueryRow> it=r;it.hasNext();){
            QueryRow qr = it.next();
            Document doc = qr.getDocument();
            String jsonString = gson.toJson(doc.getProperties().get(type), Map.class);
            T t = (T) gson.fromJson(jsonString, c);
            l.add(t);
        }
        return l;

    }

    /**
     * Delete a document based on his id
     * @param id String document id to delete
     * @return true if the document was deleted, false if we can't delete de document
     */
    public boolean delete(String id){
        try {
            Document d = this.dc.getDatabase().getExistingDocument(id);
            if(d!=null){
                return d.delete();
            }else{
                return false;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Drops the database
     */
    public void deleteDatabase(){
        try{
            dc.getDatabase().delete();
            dc.buildDatabase();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the Class of the given object
     * @param o Object to find class
     * @return Class of the given object
     */
    private Class getObjectClass(Object o){
        return o.getClass();
    }

    /**
     * Get a string representation of a Class
     * instead of app.example.mock.Mock gets only Mock
     * @param c Class to find his name
     * @return String with the class name
     */
    private String getClassName(Class c){
        String[] v = c.toString().split("\\.");
        String type = v[v.length-1];
        return type;
    }

    /**
     * Get a string representation of the Class of an object
     * instead of app.example.mock.Mock gets only Mock
     * @param o Object to find his class name
     * @return String with the class name
     */
    private String getObjectClassName(Object o){
        String[] v = String.valueOf(o.getClass()).split("\\s")[1].split("\\.");
        String type = v[v.length-1];
        return type;
    }

    /**
     *
     * @param o Object to get his id
     * @return String with the id of an object
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String getObjectId(Object o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class[] args = new Class[0];
        Method method = o.getClass().getSuperclass().getDeclaredMethod("getId",args);
        Object id = method.invoke(o);
        if(id!=null){
            return id.toString();
        }else{
            return null;
        }
    }

    /**
     * Given an object, check if its persisted in some document
     * @param o Object to find his document
     * @return Document if its on the database, null if the document don't exists
     */
    private Document findDocument(Object o){
        String id = null;
        try {
            id = this.getObjectId(o);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(id!=null){
            Document d = this.dc.getDatabase().getExistingDocument(id);
            if(d!=null){
                return d;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }



}
