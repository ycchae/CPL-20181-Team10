package mobApp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MysqlHelper {
	private Connection con;
	private Statement st;
	private ResultSet rs;
	private String[] columns;
	
	/* constructor */
	public MysqlHelper(String host, String port, String databaseName, String id, String passwd) throws Exception
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName +"?characterEncoding=UTF-8&serverTimezone=UTC", id, passwd);
			st = con.createStatement();
		}
		catch (Exception e)
		{
			System.out.println("error: " + e.getMessage());
		}
	}
	
	private void getColumnNames() throws SQLException
	{
		if (rs == null)
			return;
			
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();
		columns = new String[numberOfColumns + 1];
		columns[0] = "";  // 0 index is not used
		
		// get the column names; column indexes start from 1
		for (int i = 1; i < numberOfColumns + 1; i++)
			columns[i] = rsMetaData.getColumnName(i);
	}
	
	/* input : table name, column name */
	public String selectColumn(String table, String column) throws Exception
	{
		boolean check = false;
		int index = 0;
		
		try
		{
			String SQL = "select * from " + table + ";";
			rs = st.executeQuery(SQL);
			getColumnNames();  // column names setting
			
			for (int i = 1; i < columns.length; i++) {
				if (columns[i].equals(column)) {
					index = i;
					check = true;
					break;
				}
			}
			System.out.printf("%40s", columns[index]);
			System.out.printf("\n");
			System.out.printf("-----------------------------------------");
			System.out.printf("\n");
			if (check == true) {
				while (rs.next()) {
						return rs.getString(columns[index]);
				}
			}
			else
				System.out.println(column + " : no item");
			return null;
		}
		catch(Exception e)
		{
			System.out.println("error  : " + e.getMessage());
			return null;
		}
	}
}
