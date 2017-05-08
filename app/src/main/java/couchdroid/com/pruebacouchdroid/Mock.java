package couchdroid.com.pruebacouchdroid;

import com.couchdroid.CouchdroidObject;

/**
 * Created by jacobotapia on 05/05/17.
 */

public class Mock extends CouchdroidObject{

    public String name;

    public Mock(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return "Mock: "+this.name;
    }

}
