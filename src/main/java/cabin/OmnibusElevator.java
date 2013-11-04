package cabin;

public class OmnibusElevator {

    private String commands[] = {
        "OPEN","CLOSE","UP",
        "OPEN","CLOSE","UP",
        "OPEN","CLOSE","UP",
        "OPEN","CLOSE","UP",
        "OPEN","CLOSE","UP",
        "OPEN","CLOSE","DOWN",
        "OPEN","CLOSE","DOWN",
        "OPEN","CLOSE","DOWN",
        "OPEN","CLOSE","DOWN",
        "OPEN","CLOSE","DOWN"
    };

    private int count = 0;

    public String nextCommand() {
        return commands[(count++)%commands.length];
    }
}
