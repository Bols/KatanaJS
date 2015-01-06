package no.bols.katana.server;

import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class KatanaServlet extends HttpServlet {

    public static Map<String, Object> store = new HashMap<String, Object>();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ret = null;

        try {
            System.out.println(request.getPathInfo());
            String[] path = request.getPathInfo().split("/");
            String id = path[2];
            Class<?> clazz = Class.forName(path[1]);

            String inputStreamString = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A").next();

            Object receivedObject = new Gson().fromJson(inputStreamString, clazz);
            System.out.println("UPDATE " + clazz.getSimpleName() + ":" + inputStreamString);
            Method getId = clazz.getMethod("getId");

            String objectId = (String) getId.invoke(receivedObject);
            if(store.get(objectId)==null){
                store.put(objectId, receivedObject);
            }else{
                Object existingObject = store.get(objectId);
                BeanUtils.copyProperties(existingObject,receivedObject);
            }
            ret=new Gson().toJson(receivedObject);
            response.getWriter().write(ret);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ret = null;
        System.out.println(request.toString());
        try {

            System.out.println(request.getPathInfo());
            String[] path = request.getPathInfo().split("/");
            String id = path[2];
            if(id.equals("metadata")){
                ret=getMetaData();
            }else{
                ret = new Gson().toJson(store.get(id));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(ret);
        response.getWriter().write(ret);

    }

    private String getMetaData() {
        try {
            return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("metadata.json").toURI())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    private MetaData getMetaData() {
//        MetaDataObject root = new MetaDataObject();
//        root.setClass(RootObject.class);
//        HashMap<String, Reference> rootRelations = new HashMap<String, Reference>();
//        rootRelations.put("app", new Reference(Application.class));
//        root.setRelations(rootRelations);
//        root.addView(new View(View.NORMAL, "{#view viewName=\"root\" obj=\"app\" viewType=\"" + View.NORMAL + "\"/}"));
//        MetaDataObject app = new MetaDataObject();
//        app.setClass(Application.class);
//        HashMap<String, Reference> relations = new HashMap<String, Reference>();
//        relations.put("person", new Reference(PersonReference.class));
//        app.setRelations(relations);
//        app.setFields(new String[]{"applicationName"});
//        app.addView(new View(View.NORMAL, "Application  {applicationName}, koblet Person er {person.userName}!" +
//                "<div>\n" +
//                "{#view viewName=\"personview\" obj=\"person\" viewType=\"" + View.NORMAL + "\"/}" +
//                "</div><div style=\"font-size:6px\">{#link viewType=\"" + View.EDIT + "\"/}</div>"));
////        + "{#link viewType=\"Edit\"/}"));
//
//        app.addView(new View(View.EDIT, "Editer Application: <input name=\"applicationName\">{applicationName}</input>, koblet Person er {person.userName}!" +
//                "<div>\n" +
//                "{#view obj=\"person\" viewType=\"" + View.EDIT + "\"/}" +
//                "</div>"));
//        //"{#submit viewType=\"Normal\"/}"));
//
//        MetaDataObject person = new MetaDataObject();
//        person.setClass(Person.class);
//        person.setRelations(new HashMap<String, Reference>());
//        person.setFields(new String[]{"userName"});
//        person.addView(new View(View.NORMAL, "<div style=\"color:gray; border: 1px solid gray\";><div style=\"font-size:10px\">{#link viewType=\"" + View.EDIT + "\"/}</div>Dette er Person {userName}. </div> "));
//        person.addView(new View(View.EDIT, "<div style=\"color:red; border: 1px solid red\";>Editer Person: <input {#field name=\"userName\"/}/></div>{#submit/} {#link viewType=\"" + View.NORMAL + "\"/}"));
//
//
//        //TODO: denne burde genereres on-the-fly, og arve den generelle ToOneReference
//        MetaDataObject personReference = new MetaDataObject();
//        personReference.setClass(PersonReference.class);
//        personReference.setWrapper(true);
//        HashMap<String, Reference> pfRef = new HashMap<String, Reference>();
//        pfRef.put("wrappedObject", new Reference(Person.class));
//        personReference.setRelations(pfRef);
//        personReference.addView(new View(View.NORMAL, "<div style=\"color:silver; border: 1px solid yellow\";><div style=\"font-size:10px\">{#link viewType=\"" + View.EDIT + "\"/}</div>{#wrappedObject}Wrappet person:{#view viewName=\"wrappedObject\" obj=\"wrappedObject\" viewType=\"" + View.NORMAL + "\"/}{:else}Ingen person valgt!{/wrappedObject} </div> "));
//        personReference.addView(new View(View.EDIT, "<div style=\"color:red; border: 1px solid red\";>Velg Person: {#selectList/} {#view viewName=\"wrappedObject\" obj=\"wrappedObject\" viewType=\"" + View.EDIT + "\"/}  {#submit/} {#link viewType=\"" + View.NORMAL + "\"/}</div>"));
//
//
////        MetaDataObject personReference = new MetaDataObject(toOneReference);
////        personReference.setClass(ToOneReference.class);
//
//        MetaData m = new MetaData();
//        m.setClasses(new MetaDataObject[]{root, app, person, personReference});
//        m.setRootClassName(RootObject.class.getName());
//        return m;
//    }
//
//    public static class MetaData {
//        String rootClassName;
//        MetaDataObject classes[];
//
//        public String getRootClassName() {
//            return rootClassName;
//        }
//
//        public void setRootClassName(String rootClassName) {
//            this.rootClassName = rootClassName;
//        }
//
//        public MetaDataObject[] getClasses() {
//            return classes;
//        }
//
//        public void setClasses(MetaDataObject[] classes) {
//            this.classes = classes;
//        }
//    }
//
//    public static class MetaDataObject {
//        String className;
//        String fields[] = new String[]{};
//        HashMap<String, View> views = new HashMap<String, View>();
//        private String superReference;
//        private boolean wrapper = false;
//
//        public MetaDataObject(MetaDataObject superReference) {
//            this.superReference = superReference.className;
//        }
//
//        public MetaDataObject() {
//        }
//
//        public void addView(View view) {
//            views.put(view.viewName, view);
//        }
//
//        public HashMap<String, View> getViews() {
//            return views;
//        }
//
//        HashMap<String, Reference> relations = new HashMap<String, Reference>();
//
//        public String[] getFields() {
//            return fields;
//        }
//
//        public void setFields(String[] fields) {
//            this.fields = fields;
//        }
//
//        public String getClassName() {
//            return className;
//        }
//
//        public void setClass(Class containedClass) {
//            this.className = containedClass.getName();
//        }
//
//        public HashMap<String, Reference> getRelations() {
//            return relations;
//        }
//
//        public void setRelations(HashMap<String, Reference> relations) {
//            this.relations = relations;
//        }
//
//
//        public void setWrapper(boolean wrapper) {
//            this.wrapper = wrapper;
//        }
//    }
//
//
//    public static class Reference {
//        String referencedClassName;
//        String wrapperName;
//
//        public Reference(Class referencedClass) {
//            this.referencedClassName = referencedClass.getName();
//        }
//
//        public Reference(Class referencedClass, Class<ToOneReference> wrapperClass) {
//            this.referencedClassName = referencedClass.getName();
//            this.wrapperName = wrapperClass.getName();
//        }
//    }

}
