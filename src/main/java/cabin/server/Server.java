package cabin.server;

import static spark.Spark.get;
import spark.Request;
import spark.Response;
import spark.Route;
import cabin.Elevator;
import cabin.OmnibusElevator;

public class Server {
	private final Elevator elevator;

	public Server() {
		elevator = new OmnibusElevator();
	}

	public void start() {
		get(new Route("/:path") {
			@Override
			public Object handle(Request request, Response response) {
				Object result = "";

				String path = request.params(":path");

				switch (Method.valueOf(path)) {
				case nextCommand:
					result = elevator.nextCommand();
					break;
				case call:
					// TODO : Get parameters from request
					elevator.call(null, null);
					break;
				case go:
					// TODO : Get parameter from request
					elevator.go(null);
					break;
				case userHasEntered:
					elevator.userHasEntered();
					break;
				case userHasExited:
					elevator.userHasExited();
					break;
				case reset:
					// TODO : Get parameter from request
					elevator.reset(null);
					break;
				default:
					break;
				}

				return result;
			}
		});

	}

	public static void main(String[] args) {
		new Server().start();
	}

}
