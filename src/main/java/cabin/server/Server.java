package cabin.server;

import static spark.Spark.get;

import java.util.UUID;
import java.util.logging.Logger;

import spark.Request;
import spark.Response;
import spark.Route;
import cabin.Elevator;

public class Server {
	private static final Logger requestLogger = Logger.getLogger("requests");

	public void addElevator(String context, final Elevator elevator) {
		get(new Route(context + ":path") {

			private String getRequestString(String uuid, Request request, Object response) {
				StringBuilder requestString = new StringBuilder();
				String forwardedForIp = request.headers("X-Forwarded-For");

				requestString.append(uuid);
				requestString.append(" ¤ ");
				requestString.append(forwardedForIp != null ? forwardedForIp : request.ip());
				requestString.append(" ¤ ");
				requestString.append(request.pathInfo());
				if (request.queryString() != null) {
					requestString.append("?");
					requestString.append(request.queryString());
				}
				requestString.append(" ¤ ");
				requestString.append(response);

				return requestString.toString();
			}

			@Override
			public Object handle(Request request, Response response) {
				Object result = "";

				if (elevator.isDebug()) {
					requestLogger.info("status : " + elevator.status(false));
				}

				String uuid = UUID.randomUUID().toString();
				String path = request.params(":path");

				switch (path) {
				case Method.nextCommand:
					result = elevator.nextCommand();
					break;

				case Method.call:
					String atFloor = request.queryParams(QueryParams.atFloor);
					String direction = request.queryParams(QueryParams.to);

					if (atFloor != null) {
						elevator.call(new Integer(atFloor), direction);
					}
					break;

				case Method.go:
					String floorToGo = request.queryParams(QueryParams.floorToGo);

					if (floorToGo != null) {
						elevator.go(new Integer(floorToGo));
					}
					break;

				case Method.userHasEntered:
					elevator.userHasEntered();
					break;

				case Method.userHasExited:
					elevator.userHasExited();
					break;

				case Method.reset:
					String minFloor = request.queryParams(QueryParams.lowerFloor);
					Integer iMinFloor = minFloor != null ? new Integer(minFloor) : null;

					String maxFloor = request.queryParams(QueryParams.higherFloor);
					Integer iMaxFloor = maxFloor != null ? new Integer(maxFloor) : null;

					String cabinSize = request.queryParams(QueryParams.cabinSize);
					Integer iCabinSize = cabinSize != null ? new Integer(cabinSize) : null;

					String cause = request.queryParams(QueryParams.cause);

					elevator.reset(iMinFloor, iMaxFloor, iCabinSize, cause);
					break;

				case Method.debug:
					String debug = request.queryParams(QueryParams.debug);
					elevator.setDebug(Boolean.valueOf(debug));
					break;

				case Method.status:
					result = elevator.status(true);
					break;

				case Method.threshold:
					String alertThreshold = request.queryParams(QueryParams.alertThreshold);
					Integer iAlertThreshold = alertThreshold != null ? new Integer(alertThreshold) : null;

					String panicThreshold = request.queryParams(QueryParams.panicThreshold);
					Integer iPanicThreshold = panicThreshold != null ? new Integer(panicThreshold) : null;

					elevator.thresholds(iAlertThreshold, iPanicThreshold);

				default:
					break;
				}

				requestLogger.info(this.getRequestString(uuid, request, result));

				return result;
			}
		});

	}

}
