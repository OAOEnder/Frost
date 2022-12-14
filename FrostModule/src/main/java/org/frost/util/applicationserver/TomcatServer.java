package org.frost.util.applicationserver;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;

/**
 * Configured so all request are forwarded to the DispatcherServlet
 */
public class TomcatServer  {

    private Tomcat tomcat = new Tomcat();

    public void start(){
        tomcat.setPort(8080);
        tomcat.getConnector();
        Context context = tomcat.addContext("",null);
        Tomcat.addServlet(context,"dispatcherServlet", new DispatcherServlet());
        context.addServletMappingDecoded("/","dispatcherServlet");

        try {
            tomcat.start();
            new Thread(()-> tomcat.getServer().await());
        }catch (LifecycleException e) {
            e.printStackTrace();
        }



    }
}
