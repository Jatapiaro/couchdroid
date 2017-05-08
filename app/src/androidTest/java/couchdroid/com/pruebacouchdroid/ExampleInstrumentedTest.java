package couchdroid.com.pruebacouchdroid;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.couchdroid.crud.CRUDOperations;
import com.couchdroid.databasebuilder.DatabaseConfiguration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /*
    *Se verifica que la base de datos sea creada
    *Y que a pesar de que en los resources este como Doggos
    *El framework debe pasarlo a lower case para aceptar el nombre
    *Por lo tanto se espera que la base de datos creada tenga el nombre
    *esperado
    */
    @Test
    public void database_created() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseConfiguration dc = DatabaseConfiguration.getInstance(appContext);
        assertEquals("doggos", dc.getDatabase().getName());
        assertNotEquals("otra_db",dc.getDatabase().getName());
    }


    /*
    *Se prueba que al eliminar la base de datos
    * se cree una nueva con el mismo nombre
    */
    @Test
    public void drop_database() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseConfiguration dc = DatabaseConfiguration.getInstance(appContext);
        assertEquals("doggos", dc.getDatabase().getName());
        CRUDOperations co = CRUDOperations.getInstance(appContext);
        co.deleteDatabase();
        assertEquals("doggos", dc.getDatabase().getName());
    }


    /*
    *Se probara que cualquier tipo de objeto
    * sea creado con una simple instrucción
    */
    @Test
    public void object_creation() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseConfiguration dc = DatabaseConfiguration.getInstance(appContext);
        CRUDOperations co = CRUDOperations.getInstance(appContext);
        co.deleteDatabase();
        /*
        *Se espera que la base de datos tenga 0 elementos
        */
        assertEquals(0,dc.getDatabase().getDocumentCount());
        ArrayList<String> l = new ArrayList<String>();
        l.add("Pelota");
        l.add("Hueso");
        Doggo d = new Doggo("Borán","Beagle",1,l);
        co.create(d);
        assertNotEquals(null,d.getId());
        assertEquals(1,dc.getDatabase().getDocumentCount());
        Mock m = new Mock("Hola");
        co.create(m);
        assertNotEquals(null,m.getId());
        assertEquals(2,dc.getDatabase().getDocumentCount());
    }

    @Test
    public void find() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        CRUDOperations co = CRUDOperations.getInstance(appContext);
        co.deleteDatabase();
        Mock m = new Mock("Jacobo");
        co.create(m);
        Mock mi = co.find(m.getId(),Mock.class);
        assertEquals(true,m.name.equals(mi.name));
    }

    /*
    * Probamos que cualquier objeto pueda
    * actualizarse
    */
    @Test
    public void edited_obj() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        CRUDOperations co = CRUDOperations.getInstance(appContext);
        co.deleteDatabase();
        List<String> l = new ArrayList<String>();
        l.add("Pelota");
        l.add("Hueso");
        Doggo d = new Doggo("Borán","Beagle",1,l);
        co.create(d);
        assertNotEquals(null,d.getId());
        d.setName("Jacobo");
        assertEquals(true,co.update(d));
        Doggo p = co.find(d.getId(),Doggo.class);
        assertEquals(true,p.getName().equals(d.getName()));
        /*
        *Se prueba que los objetos con un id erroneo
        * o nulo no se actualicen
        */
        Doggo r = new Doggo("B","B",1,new ArrayList<String>());
        assertEquals(false,co.update(r));
        r.setId("FAKE-ID");
        assertEquals(false,co.update(r));
        assertEquals(false,co.update(new Mock("Falso")));
    }

    @Test
    public void delete_obj() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        CRUDOperations co = CRUDOperations.getInstance(appContext);
        DatabaseConfiguration dc = DatabaseConfiguration.getInstance(appContext);
        co.deleteDatabase();
        assertEquals(0,dc.getDatabase().getDocumentCount());
        Mock m = new Mock("Jacobo");
        co.create(m);
        assertEquals(1,dc.getDatabase().getDocumentCount());
        assertEquals(true,co.delete(m.getId()));
        assertEquals(0,dc.getDatabase().getDocumentCount());
        assertEquals(false,co.delete("fake-id"));
    }

    @Test
    public void list_obj() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        CRUDOperations co = CRUDOperations.getInstance(appContext);
        DatabaseConfiguration dc = DatabaseConfiguration.getInstance(appContext);
        co.deleteDatabase();
        assertEquals(0,co.list(Mock.class).size());
        Mock m = new Mock("Jacobo");
        co.create(m);
        co.create(new Mock("Miguel"));
        co.create(new Mock("Veléz"));
        assertEquals(3,co.list(Mock.class).size());
        assertEquals(0,co.list(Doggo.class).size());
    }

    @Test
    public void integration_test() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseConfiguration dc = DatabaseConfiguration.getInstance(appContext);
        CRUDOperations co = CRUDOperations.getInstance(appContext);
        co.deleteDatabase();
        assertEquals("doggos",dc.getDatabase().getName());
        Mock m = new Mock("Alfredo");
        assertNotEquals(null,co.create(m));
        m.name = "Jacobo";
        assertEquals(true,co.update(m));
        assertEquals(false,co.update(new Mock("Falso")));
        Mock mo = co.find(m.getId(),Mock.class);
        assertNotEquals(null,mo.name.equals(m.name));
        assertNotEquals(null,co.find(m.getId(),Mock.class));
        assertEquals(null,co.find("fake.id",Mock.class));
        assertEquals(false,co.update(new Mock("Lol")));

        Mock falso = new Mock("Flase");
        assertEquals(true,co.delete(m.getId()));
        assertEquals(false,co.delete(falso.getId()));
        assertEquals(false,co.delete("fake-id"));

        co.create(new Mock("Mock"));
        co.create(new Mock("Mock2"));
        co.create(new Mock("Mock3"));
        co.create(new Mock("Mock4"));

        assertEquals(4,co.list(Mock.class).size());


    }



}
