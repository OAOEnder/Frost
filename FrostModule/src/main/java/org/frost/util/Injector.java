package org.frost.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.lang.annotation.*;

//the purpose of this class is to be called by the application container
//to scan the classSet and inject the dependencies for each component class and then instantiate the
//object of the class, and map it as the value with the class name as the type.
public class Injector {

    Set<Class<?>> classSet;

    public Injector(){

    }
    public Injector(Set<Class<?>> classSet){
        this.classSet = classSet;
    }
    public void setClassSet(Set<Class<?>> classSet){
        this.classSet = classSet;
    }

    public Map mapClassObjects() throws Exception {
        //need to use the map of classes to their dependencies, to instantiate an object of the class type.
        Map<Class, Object> objectMap = new HashMap<>();

        Map<Class, List<Object>> dependencyMap = scanClasses();

        System.out.println(dependencyMap.toString());//testing the dependency map

        for(int i = 0; i < dependencyMap.size(); i++ ) { //for each class in the dependency map
            Class<?> cl = dependencyMap.getClass();
            Constructor[] constructors = cl.getConstructors(); //get its constructors
            for ( Constructor constructor: constructors ) {
                if(constructor.getAnnotations().length > 0){//if the constructor is annotated with @Inject --> inject the dependencies and create a new instance

                    //creating the object using the dependency objects that are in the dependencyMap
                    Object obj = constructor.newInstance(dependencyMap.get(cl));

                    objectMap.put(cl, obj);//
                }
            }
            //This line uses the classes constructor to create a new instance,
            //using its

        }
        return objectMap;
    }

    private Map scanClasses() throws Exception{
        HashMap<Class, List<Object>> dependencyMap = new HashMap<>();//dependentToDependencies
        List<Object> objectList = new ArrayList<>();
        for ( Class currentClass : classSet ) {//for each class in classSet --> check their annotations
            Annotation[] annotations = currentClass.getAnnotations();
            if(annotations.length > 0){//if this class is annotated --> get its Constructors
                Constructor[] outterConstructors = currentClass.getConstructors();
                for (Constructor outterConstructor: outterConstructors ) { //for each constructor -->check their annotations for @inject

                    //here is where we could potentially look at recursively checking the deeper constructor for dependency parameters.

                    if (outterConstructor.getAnnotations().length > 0 ) {//if the outterConstructor has the @inject annotation, get its dependencies/parameters
                        Class<?>[] dependencies = outterConstructor.getParameterTypes();
                        for (Class dependency : dependencies) { //now for each individual dependency --> get their constructor and use it to build the object and map it
                            Constructor[] innerConstructors = dependency.getConstructors();

                            //here is where we could potentially look at recursively checking the deeper constructor for dependency parameters.

                            //instantiating the dependencyObject
                            try {
                                Object dependencyObj = innerConstructors[0].newInstance();
                                objectList.add(dependencyObj);//adding the object to the list of dependencyObjects that will be mapped to the class that needs them.

                            } catch (Exception e) {
                                System.out.println("Failed to instantiate dependency");
                                System.out.println(e.getMessage());
                            }
                        }
                        //once its gone through each dependency and created an object and stored the obj in the dependencyList,
                        //its time to feed the current class and the dependencyList to the Map
                        dependencyMap.put(currentClass, objectList); //MAPPING the class and list of dependency objects that the class will use to create its own object.
                    }
                }
            }
        }
        return dependencyMap;
    }


    private static Annotation[] getAnnotations(Class currentClass){
        return currentClass.getAnnotations();
    }




}
