package org.wso2.appserver.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * 
 * Database class demonstrating enforcing tenant key in database tables
 * The class is not tied to the CarbonContext
 */
public class DBUtils {
	private static final String addDataQ =
	                                       "INSERT INTO  `toolsdb`.`customer` (`tenantID`, `toolID`, `time`) VALUES (?, ?, ?)";
	private static final String getDataQ = "SELECT * FROM  `customer` where `tenantID` = ?";

	private static String dbUrl = "jdbc:mysql://localhost:3306/toolsdb";
	private static String user = "root";
	private static String password = "root";

	public static void putData(int tID, String toolID) {
		Connection con = null;
		PreparedStatement preparedStatement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, user, password);
			preparedStatement = con.prepareStatement(addDataQ);
			preparedStatement.setInt(1, tID);
			preparedStatement.setString(2, toolID);
			preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
	        e.printStackTrace();
        } finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static ToolPurchase[] getData(int tID) {
		Connection con = null;
		PreparedStatement preparedStatement = null;
		ArrayList<ToolPurchase> purchasesArray = new ArrayList<ToolPurchase>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, user, password);
			preparedStatement = con.prepareStatement(getDataQ);
			preparedStatement.setInt(1, tID);
			ResultSet rs = preparedStatement.executeQuery();
			ToolPurchase purchase;
			while (rs.next()) {
				purchase = new ToolPurchase();
				purchase.setTenantID(rs.getInt("tenantID"));
				purchase.setToolID(rs.getString("toolID"));
				purchase.setTime(rs.getTimestamp("time"));
				purchasesArray.add(purchase);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
	        e.printStackTrace();
        } finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (ToolPurchase[]) purchasesArray.toArray(new ToolPurchase[purchasesArray.size()]);
	}
}
