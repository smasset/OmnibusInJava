package cabin.server;

import static spark.Spark.get;

import spark.Request;
import spark.Response;
import spark.Route;
import cabin.Elevator;

public class Server {
    private final Elevator elevator;

    public Server() {
        elevator = new Elevator();
    }

    public void start() {
        get(new Route("/:path") {
            @Override
            public Object handle(Request request, Response response) {
                String path = request.params(":path");
                if (path.equals("nextCommand")) {
                    return elevator.nextCommand();
                } else {
                    return "";
                }
            }
        });

    }

    public static void main(String[] args) {
        new Server().start();
    }

}
