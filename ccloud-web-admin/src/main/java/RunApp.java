

import com.jfinal.core.JFinal;

public class RunApp {

	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 8899, "/", 5);
	}

}
