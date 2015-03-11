import java.sql.Date;
import java.util.Vector;


public class Controller 
{
	public void login()
	{
		
	}
	
	public void register()
	{
		
	}
	
	public Vector<DBApi> informationOfBus(boolean ac, boolean sleeper, Date dates, String tolocation, String fromlocation)
	{
		Vector<DBApi> vector=new DBApi().getInformationOfBus(ac, sleeper, dates, tolocation, fromlocation);	
		return vector;
	}
	
	public int addBus(int bid, int maxseats, String ac, int totalsleepers)
	{
		return new DBApi().addBuses(bid, maxseats, ac, totalsleepers);
	}

	public int addRoute()
	{
		
	}
	
}
