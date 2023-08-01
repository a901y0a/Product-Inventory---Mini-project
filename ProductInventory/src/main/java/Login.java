import java.util.*;
import java.sql.*;
public class Login {
	private static final String db_url="jdbc:mysql://localhost:3306/product";
	private static final String db_username="root";
	private static final String db_password="agalya@2004";
	public static void main(String[] args)
	{
		Scanner ob=new Scanner(System.in);
		System.out.println("WELCOME TO PRODUCT INVENTORY MANAGEMENT SYSTEM !!!");
		System.out.println("Select your position:");
			System.out.println("1.Admin");
			System.out.println("2.Customer");
			System.out.println("Enter Admin/Customer ");
			int choose=ob.nextInt();
			ob.nextLine();
			switch(choose)
			{
			     case 1:
			    	 adminLogin(ob);
			    	 break;
			     case 2:
			    	 employeeLogin(ob);
			    	
			    	 break;
			    default:
			    	System.out.println("Enter another choice!!It's invalid");
			}
		}
	
	private static void employeeLogin(Scanner ob) {
		Customer customer=new Customer();
		System.out.println("Customer Login");
		System.out.println("Your id:");
		int id=ob.nextInt();
		ob.nextLine();
		System.out.println("Your password:");
		String password=ob.nextLine();
		if(EmployeecheckCredentials(id,password))
		{
			System.out.println("Logged in!.Welcome");
			customer.eventdetails();
			
			
		}
		else
		{
			System.out.println("Invalid username or password");
		}
		
	}
	private static void adminLogin(Scanner ob) {
		Administrator admin=new Administrator();
		System.out.println("Admin Login");
		System.out.println("Your id:");
		int id=ob.nextInt();
		ob.nextLine();
		System.out.println("Your password:");
		String password=ob.nextLine();
		if(AdmincheckCredentials(id,password))
		{
			System.out.println(" Succesfully Logged in!.Welcome");
			admin.managerDetails();
		}
		else
		{
			System.out.println("Invalid username or password");
		}
		
	}
	private static boolean EmployeecheckCredentials(int id, String password) {
	    try(Connection connection=DriverManager.getConnection(db_url,db_username,db_password))
	    		{
	    			String query="SELECT * FROM customerlogin where id=? and password=? ";
	    			PreparedStatement preparedStatement=connection.prepareStatement(query);
	    			preparedStatement.setInt(1, id);
	    			preparedStatement.setString(2, password);
	    			
	    			ResultSet resultSet=preparedStatement.executeQuery();
	    			return resultSet.next();
	    		}
	    catch(SQLException e)
	    {
	    	e.printStackTrace();
	    }
		return false;
	}
	private static boolean AdmincheckCredentials(int id, String password) {
	    try(Connection connection=DriverManager.getConnection(db_url,db_username,db_password))
	    		{
	    			String query="SELECT * FROM adminlogin where id=? and password=?";
	    			PreparedStatement preparedStatement=connection.prepareStatement(query);
	    			preparedStatement.setInt(1, id);
	    			preparedStatement.setString(2, password);
	    			
	    			ResultSet resultSet=preparedStatement.executeQuery();
	    			return resultSet.next();
	    		}
	    catch(SQLException e)
	    {
	    	e.printStackTrace();
	    }
		return false;
	}
	

}

