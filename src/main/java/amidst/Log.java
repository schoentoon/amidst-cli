package amidst;

public class Log {
	private static LogListener listener;
	private static boolean isUsingListener;
	
	public static void i(Object... s) {
		printwithTag("info", s);
		if (isUsingListener)
			listener.info(s);
	}
	public static void debug(Object... s) {
		printwithTag("debug", s);
		if (isUsingListener)
			listener.debug(s);
	}
	public static void w(Object... s) {
		printwithTag("warning", s);
		if (isUsingListener)
			listener.warning(s);
	}
	public static void e(Object... s) {
		printwithTag("error", s);
		if (isUsingListener)
			listener.error(s);
	}
	public static void kill(Object... s) {
		String msg = printwithTag("kill", s);
		if (isUsingListener)
			listener.kill(s);
		throw new RuntimeException(msg);
	}
	
	public static void writeTo(LogListener l) {
		listener = l;
		isUsingListener = (l != null);
	}
	
	private static String printwithTag(String tag, Object... msgs) {
		System.out.print("[" + tag + "] ");
		String msg = "";
		for (int i = 0; i < msgs.length; i++) {
			msg += msgs[i];
			msg += (i < msgs.length - 1) ? " " : "\n";
		}
		System.out.print(msg);
		return msg;
	}
}
