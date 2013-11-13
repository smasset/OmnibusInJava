import spark.Spark;
import cabin.DefaultElevator;
import cabin.FifoElevator;
import cabin.OmnibusElevator;
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
	}
}
