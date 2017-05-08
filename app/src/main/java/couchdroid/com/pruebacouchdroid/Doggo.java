package couchdroid.com.pruebacouchdroid;

import com.couchdroid.CouchdroidObject;

import java.util.List;

/**
 * Created by jacobotapia on 02/05/17.
 */

public class Doggo extends CouchdroidObject{

    private String name;
    private String raze;
    private int age;
    private List<String> juguetes;

    public Doggo(String name,String raze,int age,List<String> juguetes){
        super();
        this.name = name;
        this.raze = raze;
        this.age = age;
        this.setJuguetes(juguetes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaze() {
        return raze;
    }

    public void setRaze(String raze) {
        this.raze = raze;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getJuguetes() {
        return juguetes;
    }

    public void setJuguetes(List<String> juguetes) {
        this.juguetes = juguetes;
    }

    /*@Override
    public String toString(){
        return this.name+" : "+this.raze+" : "+this.age+" : "+this.juguetes.toString()+" : "+this.getId();
    }*/


}
