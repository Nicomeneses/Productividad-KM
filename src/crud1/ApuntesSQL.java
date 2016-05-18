
package crud1;
import java.sql.*;



public class ApuntesSQL extends Conexion{
     String sql;
     Conexion con = new Conexion();
     Connection cn = con.conectar();
    public void listado(){
       
        sql="select * from productos order by id desc";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){      
                System.out.println(rs.getString("nombre"));    
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void insertar(){
       
        sql="INSERT INTO categorias values(null,?)";
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, "as");
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void update(){
        try {
            PreparedStatement pst = cn.prepareStatement("UPDATE categorias set categoria='asdf' where id='1'");
            pst.executeUpdate();
        } catch (Exception e) {
             System.out.println(e.getMessage());
        }
    }
    public void delete(){
         try {
            PreparedStatement pst = cn.prepareStatement("DELETE FROM categorias where id='1'");
            pst.executeUpdate();
        } catch (Exception e) {
             System.out.println(e.getMessage());
        }
    }
    
    
}
