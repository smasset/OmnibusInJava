import cabin.DefaultElevator;
import cabin.OmnibusElevator;
import cabin.server.Server;

public class Launcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server cabinServer = new Server();
		cabinServer.addElevator("/", new DefaultElevator());
		cabinServer.addElevator("/omnibus/", new OmnibusElevator());
	}
}
