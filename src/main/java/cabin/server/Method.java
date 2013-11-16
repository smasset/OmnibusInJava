package cabin.server;

public enum Method {
	// Mandatory commands
	nextCommand, call, go, userHasEntered, userHasExited, reset,

	// Custom commands
	debug,
	status,
	threshold;
}
