package no.bols.katana.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.File;

public class KatanaServer {

    private Server server;

    public KatanaServer start(){
        server = new Server(8080);

        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
        ResourceHandler resource_handler = new ResourceHandler();
        ContextHandler staticHandler = new ContextHandler("/"); /* the server uri path */
        resource_handler.setResourceBase(findIndexHTMLLocation());
        staticHandler.setHandler(resource_handler);


        //ServletContextHandler jerseyCtxHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ErrorHandler loggingErrorHandler=new ErrorHandler();
        loggingErrorHandler.setShowStacks(true);
        ServletContextHandler jsonCtxHandler = new ServletContextHandler(null,"/rest",null,null,null,loggingErrorHandler);
        jsonCtxHandler.addServlet(KatanaServlet.class, "/katana/*");

        // Add the ResourceHandler to the server.
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { jsonCtxHandler,staticHandler, new DefaultHandler() });
        server.setHandler(handlers);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private String findIndexHTMLLocation() {
        ClassLoader loader = this.getClass().getClassLoader();
        File indexLoc = new File(loader.getResource("index.html").getFile());
        return indexLoc.getParentFile().getAbsolutePath();
    }

    public KatanaServer waitForFinish(){
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }
}
