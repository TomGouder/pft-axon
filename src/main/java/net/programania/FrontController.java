package net.programania;

import net.programania.axon.AxonBootstrap;
import net.programania.commands.CreateTransactionCommand;
import net.programania.h2.TransactionDao;
import org.axonframework.commandhandling.gateway.CommandGateway;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;

import static spark.Spark.get;

public class FrontController extends Controller {
  private static final CommandGateway commandGateway = AxonBootstrap.bootstrap();
  private static final TransactionDao dao = new TransactionDao();

  public static void main(String[] args) {
    get(new Route("/") {
      @Override
      public Object handle(Request request, Response response) {
        HashMap<String, Object> scope = new HashMap<>();
        scope.put("ip", request.ip());
        scope.put("transactions", dao.select());
        return render(scope, "/templates/index.html");
      }
    });
    get(new Route("/create") {
      @Override
      public Object handle(Request request, Response response) {
        commandGateway.send(CreateTransactionCommand.factory());
        response.redirect("/");
        return "Transacción creada!";
      }
    });
  }
}

