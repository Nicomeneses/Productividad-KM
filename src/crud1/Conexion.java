
package crud1;
import java.sql.*;

public class Conexion {
    Connection con=null;
    /*public Connection conectar(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/sushikoi","root","");
           
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error :"+e.getMessage());
        }    
        return con;
    }*/
   public Connection conectar(){
         try
        {           
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String database = 
                      "JDBC:ODBC:Driver={Microsoft Access Driver (*.mdb, *.accdb)}; DBQ=C:\\blabla.mdb";
            Connection conn = DriverManager.getConnection(database, "", "");            
            return conn;      
                   
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
