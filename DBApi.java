import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Vector;


public class DBApi
{
	
	Connection con;
	Date destinationarrivaltime, dateofjourney;
	String bid, query,name, gender, id, idno, fromlocation, tolocation;
	int availablesleeper, availableseat,mobile, seatno;
	Time bookingtime, destinationreachtime,arrivaltime,departuretime;
	Double duration, fare;
	Vector<String> header=new Vector<>();
	
	public Vector<DBApi> getInformationOfBus(boolean ac, boolean sleeper, Date dates, String tolocation, String fromlocation)
	{
		ResultSet rs = null;
		PreparedStatement ps;
		String sac;
		Vector<DBApi> db=new Vector<>();
		try 
		{
			con=ConnectionProvider.Connections();
			
			if(sleeper==true)
				query="SELECT r.BID as Bus_Id, r.Arrival_Time as Arrival_Time, r.Departure_Time as Departure_Time, r.Destination_Arrival_Time as Destination_Arrival_Time, r.Duration as Duration, r.Fare as Fare, s.Available_Sleeper as Available_Sleeper FROM bus as b, routes as r, seatsstatus as s WHERE b.Type_Ac=? && s.Total_Sleepers_Available>0 && r.Dates like'% "+dates+"%'  && r.To_Location=? && r.From_Location=?;";
			else
				query="SELECT r.BID as Bus_Id, r.Arrival_Time as Arrival_Time, r.Departure_Time as Departure_Time, r.Destination_Arrival_Time as Destination_Arrival_Time, r.Duration as Duration, r.Fare as Fare, s.Available_Seat as Available_Seat FROM bus as b, routes as r, seatsstatus as s WHERE b.Type_Ac=? && s.Total_Sleepers_Available=0 && r.Dates like'% "+dates+"%' && r.To_Location=? && r.From_Location=?;";
			
			ps= con.prepareStatement(query);
			sac=Boolean.toString(ac);
			
			ps.setString(1, sac);
			ps.setString(2, tolocation);
			ps.setString(3, fromlocation);
			
			rs=ps.executeQuery();
			ResultSetMetaData rsmd=rs.getMetaData();
			
			int colcount=rsmd.getColumnCount();
			for(int i=1;i<=colcount;i++)
			{
			   header.add(rsmd.getColumnName(i).toUpperCase());
			}
			
			while(rs.next())
			{
				DBApi d = new DBApi();
				d.bid = rs.getString(1);
				d.arrivaltime = rs.getTime(2);
				d.departuretime= rs.getTime(3);
				d.destinationarrivaltime = rs.getDate(4);
				d.duration=rs.getDouble(5);
				d.fare=rs.getDouble(6);
				if(sleeper==true)
					d.availablesleeper=rs.getInt(7);
				else
					d.availableseat=rs.getInt(7);
				db.add(d);
			}
			
			rs.close();
			ps.close();
			con.close();
			return db;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	public int addBuses(int bid, int maxseats, String ac, int totalsleepers)
	{
		PreparedStatement ps;
		
		try 
		{
			con=ConnectionProvider.Connections();
			query="insert into bus values(?,?,?,?);";
			ps= con.prepareStatement(query);
			ps.setInt(1, bid);
			ps.setInt(2, maxseats);
			ps.setString(3, ac);
			ps.setInt(4, totalsleepers);
			
			int res=ps.executeUpdate();
			ps.close();
			con.close();
			return res;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public int addRoute(int bid, double fare, String fromlocation, String tolocation, Time arrivaltime, Time departuretime, Time destinationarrivaltime, double duration, int prior, Date journey)
	{
		PreparedStatement ps;
		
		try 
		{
			con=ConnectionProvider.Connections();
			query="insert into routes (BID, Fare, From_Location, To_Location, Arrival_Time, Departure_Time, Destination_Arrival_Time, Duration, Prior, Date_Of_Journey) values(?,?,?,?,?,?,?,?,?,?);";
			ps=con.prepareStatement(query);
			ps.setInt(1, bid);
			ps.setDouble(2, fare);
			ps.setString(3, fromlocation);
			ps.setString(4, tolocation);
			ps.setTime(5, arrivaltime);
			ps.setTime(6, departuretime);
			ps.setTime(7, destinationarrivaltime);
			ps.setDouble(8, duration);
			ps.setInt(9, prior);
			ps.setDate(10, journey);
			int res=ps.executeUpdate();
			ps.close();
			con.close();
			return res;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public int addPassenger(Timestamp time, String name, int mobile, String gender, int id, String idno)
	{
		PreparedStatement ps;
		
		try
		{
			con=ConnectionProvider.Connections();
			query="insert into passenger(Booking_Time, Name, Mobile, Gender, Id, Id_No) values(?,?,?,?,?,?);";
			ps=con.prepareStatement(query);
			ps.setTimestamp(1, time);
			ps.setString(2, name);
			ps.setInt(3, mobile);
			ps.setString(4, gender);
			ps.setInt(5, id);
			ps.setString(6, idno);
			int res=ps.executeUpdate();
			ps.close();
			con.close();
			return res;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public Vector<DBApi> printTicket(String TicketNo)
	{
		PreparedStatement ps;
		ResultSet res;
		
		try
		{
			con=ConnectionProvider.Connections();
			query="SELECT p.Name, p.Mobile, p.Gender, p.Id, p.Id_No, p.Booking_Time, r.Seat_No, r.Date_Of_Journey, rs.Fare, rs.From_Location, rs.To_Location, rs.Arrival_Time, rs.Departure_Time, rs.Destination_Arrival_Time, rs.Duration from passenger as p, reserves as r, routes as rs where p.PID=r.PID && p.Booking_Time=r.Booking_Time && r.RID=rs.RID && Ticket_no=?;";
			ps=con.prepareStatement(query);
			ps.setString(1, TicketNo);
			res=ps.executeQuery();
			ps.close();
			con.close();
			
			Vector<DBApi> vector=new Vector<>();
						
			if(res.next())
			{
				DBApi db=new DBApi();
				db.name=res.getString(1);
				db.mobile=res.getInt(2);
				db.gender=res.getString(3);
				db.id=res.getString(4);
				db.idno=res.getString(5);
				db.bookingtime=res.getTime(6);
				db.seatno=res.getInt(7);
				db.dateofjourney=res.getDate(8);
				db.fare=res.getDouble(9);
				db.fromlocation=res.getString(10);
				db.tolocation=res.getString(11);
				db.arrivaltime=res.getTime(12);
				db.departuretime=res.getTime(13);
				db.destinationreachtime=res.getTime(14);
				db.duration=res.getDouble(15);
				
				vector.add(db);
			}
			return vector;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int updateReservesTable(String ticketno,int rid, int bid, Timestamp bookingtime, int seatno, Date dateofjourney)
	{
		PreparedStatement ps;
		
		try
		{
			con=ConnectionProvider.Connections();
			query="insert into reserves values(?,?,?,?,?,?);";
			ps=con.prepareStatement(query);
			ps.setString(1, ticketno);
			ps.setInt(2, rid);
			ps.setInt(3, bid);
			ps.setTimestamp(4, bookingtime);
			ps.setInt(5, seatno);
			ps.setDate(6, dateofjourney);
			int res=ps.executeUpdate();
			ps.close();
			con.close();
			return res;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public int seatStatusForSleepers(int rid, int sleeperno, Date dateofjourney)
	{
		//for implementation of this a loop of rid will go as per 2 rules
		
		PreparedStatement ps;
		try
		{
			con=ConnectionProvider.Connections();
			query="select Booked_Sleeper_No, Available_Sleeper from seatsstatus where Dates=? && RID=?;";
			ps=con.prepareStatement(query);
			
			ps.setInt(2, rid);
			ps.setDate(1, dateofjourney);
			ResultSet rs=ps.executeQuery();
			
			String bookedseats=rs.getString(1);
			int avail=rs.getInt(2);
			
			avail--;
			bookedseats+=Integer.toBinaryString(sleeperno);
			
			query="update seatsstatus set Available_Sleeper=? && Booked_Sleeper_No=? where Dates=? && RID=?;";
			ps=con.prepareStatement(query);
			
			ps.setInt(1, avail);
			ps.setString(2, bookedseats);
			ps.setInt(4, rid);
			ps.setDate(3, dateofjourney);
			int res=ps.executeUpdate();
			
			ps.close();
			con.close();
			return res;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public int seatStatusForSeats(int rid, int seatno, Date dateofjourney)
	{
		PreparedStatement ps;
		
		try
		{
			con=ConnectionProvider.Connections();
			query="select Booked_Seat_No, Available_Seat from seatsstatus where Dates=? && RID=?;";
			ps=con.prepareStatement(query);
			
			ps.setInt(2, rid);
			ps.setDate(1, dateofjourney);
			ResultSet rs=ps.executeQuery();
			
			String bookedseats=rs.getString(1);
			int avail=rs.getInt(2);
			
			avail--;
			bookedseats+=Integer.toBinaryString(seatno);
			
			query="update seatsstatus set Available_Seat=? && Booked_Seat_No=? where Dates=? && RID=?;";
			ps=con.prepareStatement(query);
			
			ps.setInt(1, avail);
			ps.setString(2, bookedseats);
			ps.setInt(4, rid);
			ps.setDate(3, dateofjourney);
			int res=ps.executeUpdate();
			
			ps.close();
			con.close();
			return res;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void main(String[] args) throws SQLException 
	{
		
	}
}
