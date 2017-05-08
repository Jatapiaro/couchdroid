package com.couchdroid.object;

import java.util.UUID;

/**
 * Created by jacobotapia on 05/05/17.
 * All the objects that need to be persisted
 * must extend this class, so we can be sure
 * that all objects have an id parameter
 */

public class CouchdroidObject {

    private String id;

    public CouchdroidObject(){
    }

    /**
     * Return string id
     * @return id for object
     */
    public String getId(){
        return this.id;
    }

    /**
     * Set id for the object
     * @param id String id
     */
    public void setId(String id){
        this.id = id;
    }





}
