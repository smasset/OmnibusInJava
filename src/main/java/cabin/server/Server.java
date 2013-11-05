package cabin.server;

import static spark.Spark.get;
import spark.Request;
import spark.Response;
import spark.Route;
import cabin.Elevator;

public class Server {
	public void addElevator(String context, final Elevator elevator) {
		get(new Route(context + ":path") {
			@Override
			public Object handle(Request request, Response response) {
				Object result = "";

				String path = request.params(":path");

				switch (Method.valueOf(path)) {
				case nextCommand:
					result = elevator.nextCommand();
					break;
				case call:
					Integer atFloor = new Integer(request.queryParams(QueryParams.atFloor.toString()));
					String direction = request.queryParams(QueryParams.to.toString());

					elevator.call(atFloor, direction);
					break;
				case go:
					Integer floorToGo = new Integer(request.queryParams(QueryParams.floorToGo.toString()));

					elevator.go(floorToGo);
					break;
				case userHasEntered:
					elevator.userHasEntered();
					break;
				case userHasExited:
					elevator.userHasExited();
					break;
				case reset:
					String cause = request.queryParams(QueryParams.cause.toString());

					elevator.reset(cause);
					break;
				default:
					break;
				}

				return result;
			}
		});

	}

}
