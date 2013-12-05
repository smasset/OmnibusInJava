package cabin;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

import spark.Spark;
import cabin.server.Server;

public class Launcher implements UncaughtExceptionHandler {
	private static final Logger logger = Logger.getLogger(Launcher.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		Server cabinServer = new Server();

		// Set port to be CloudBees aware
		Spark.setPort(Integer.parseInt(System.getProperty("app.port", "4567")));
		Thread.setDefaultUncaughtExceptionHandler(launcher); 

		cabinServer.addElevator("/", new DefaultElevator());
		cabinServer.addElevator("/omnibus/", new OmnibusElevator());
		cabinServer.addElevator("/fifo/", new FifoElevator());
		cabinServer.addElevator("/loveinanelevator/", new LoveInAnElevator(-5, 48, 30, 8));
		cabinServer.addElevator("/vengaboys/", new UpAndDownElevator(-5, 48, 30, 8));
//		cabinServer.addElevator("/youngandrestless/", new YoungAndRestlessElevator(-5, 48, 30, 8));
		cabinServer.addElevator("/youngandrestless/", new MultiBoundedCabinUpAndDownElevator(-5, 48, 30, 8));
		cabinServer.addElevator("/multi-cabin/", new MultiCabinElevator(-5, 48, 30, 8));
		cabinServer.addElevator("/multi-omnibus/", new MultiCabinOmnibusElevator(-5, 48, 30, 8));
		cabinServer.addElevator("/multi-vengaboys/", new MultiCabinUpAndDownElevator(-5, 48, 30, 8));
		cabinServer.addElevator("/multi-youngandrestless/", new MultiCabinYoungAndRestlessElevator(-5, 48, 30, 8));
		cabinServer.addElevator("/bounded-vengaboys/", new MultiBoundedCabinUpAndDownElevator(-5, 48, 30, 8));
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		logger.fatal("uncaughtException", e);
	}
}
