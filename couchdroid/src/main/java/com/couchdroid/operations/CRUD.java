package com.couchdroid.operations;

import java.util.List;

/**
 * Created by jacobotapia on 02/05/17.
 */

public interface CRUD {

    /**
     * Persis a java object in the database
     * @param o Object to persist
     * @return return String with document id of the persisted object
     */
    public String create(Object o);


    /**
     * Update a java object previously persisted in the database
     * @param o Object to update
     * @return true if the object was updated,false if the object couldn't be persisted
     */
    public boolean update(Object o);

    /**
     * Return a list of Objects of a given class, persisted in the database
     * @param c Class of the objects to be found
     * @param <T>
     * @return list of objects of the given class
     */
    public <T> List<T> list(Class c);

    /**
     * Look for an object of a certain class given his document id
     * @param id Document id of the object
     * @param c Class of the object
     * @param <T>
     * @return Object of the given class if was founded the id on the database, null if the document couldn't be founded
     */
    public <T> T find(String id,Class c);

    /**
     * Deletes a document from the database
     * @param id
     * @return true if the document was deleted, false if the document could't be deleted
     */
    public boolean delete(String id);

    /**
     * Drop the database
     */
    public void deleteDatabase();

}
