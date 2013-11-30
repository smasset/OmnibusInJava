package cabin;
import spark.Spark;
import cabin.server.Server;

public class Launcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server cabinServer = new Server();

		// Set port to be CloudBees aware
		Spark.setPort(Integer.parseInt(System.getProperty("app.port", "4567")));

		cabinServer.addElevator("/", new DefaultElevator());
		cabinServer.addElevator("/omnibus/", new OmnibusElevator());
		cabinServer.addElevator("/fifo/", new FifoElevator());
		cabinServer.addElevator("/loveinanelevator/", new LoveInAnElevator(-5, 35, 30, 2));
		cabinServer.addElevator("/vengaboys/", new UpAndDownElevator(-5, 35, 30, 2));
		cabinServer.addElevator("/youngandrestless/", new YoungAndRestlessElevator(-5, 35, 30, 2));
	}
}
