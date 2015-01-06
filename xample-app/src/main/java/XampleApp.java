import no.bols.katana.server.KatanaServer;
import no.bols.katana.server.KatanaServlet;
import no.xample.domain.Todo;
import no.xample.app.TodoApplication;

public class XampleApp {

    public static void main(String args[]){
        Todo todo = new Todo("1");
        todo.setTask("Rydde garasje");
        KatanaServlet.store.put("1", todo);


        TodoApplication app=new TodoApplication();
        app.setId("app");
        app.todo=todo;
        app.name="Xample-app";
        app.version="V0.09";
        KatanaServlet.store.put("app",app);

        new KatanaServer().start().waitForFinish();
    }
}
