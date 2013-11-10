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
					String atFloor = request.queryParams(QueryParams.atFloor.toString());
					String direction = request.queryParams(QueryParams.to.toString());

					if (atFloor != null) {
						elevator.call(new Integer(atFloor), direction);
					}
					break;
				case go:
					String floorToGo = request.queryParams(QueryParams.floorToGo.toString());

					if (floorToGo != null) {
						elevator.go(new Integer(floorToGo));
					}
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
