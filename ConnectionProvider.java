import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionProvider
{
	static Connection con;
	public static Connection Connections()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3308/busmgmt","root","awesome");
			return con;
		}
		catch(ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
