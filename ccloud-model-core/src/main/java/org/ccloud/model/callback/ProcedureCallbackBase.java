package org.ccloud.model.callback;

import java.sql.Connection;
import java.sql.SQLException;

import com.jfinal.plugin.activerecord.ICallback;

public class ProcedureCallbackBase implements ICallback {

	public Object call(Connection conn) throws SQLException {
		return null;
	}

}
