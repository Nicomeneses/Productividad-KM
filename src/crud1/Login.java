
package crud1;
import java.sql.*;
import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    Conexion con = new Conexion();
    Connection c = con.conectar();
    
    public Login() {
        super("Inicio de Sesión");  
        initComponents();  
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtusuario = new javax.swing.JTextField();
        btnlogin = new javax.swing.JButton();
        txtpass = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Usuario");

        jLabel2.setText("Contraseña");

        txtusuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtusuarioActionPerformed(evt);
            }
        });

        btnlogin.setText("Ingresar");
        btnlogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnloginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtusuario)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnlogin, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 93, Short.MAX_VALUE))
                    .addComponent(txtpass))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtusuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnlogin, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtusuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtusuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtusuarioActionPerformed

    private void btnloginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnloginActionPerformed
   
    String usuario = txtusuario.getText();
    String contrasena = txtpass.getText().toLowerCase().trim();
    if(usuario.equals("rahe")){
         dispose();
    }
   /* if(usuario.length()>0 && contrasena.length()>0){
        try { 
            String sqlusu="select count(*) from usuarios2 where usuario =? limit 1";
            PreparedStatement pstusu = c.prepareStatement(sqlusu);
            pstusu.setString(1, usuario);
            ResultSet rsusu = pstusu.executeQuery(); 
            rsusu.beforeFirst();
            rsusu.next();
            int numfilasusu = rsusu.getInt("count(*)"); 
            
                if(numfilasusu>0){
                  String sqlpass="select count(*),id,tipo from usuarios2 where usuario =? and contrasena =? limit 1";
                    try {
                        PreparedStatement pstpass = c.prepareStatement(sqlpass);
                        pstpass.setString(1, usuario);
                        Encriptador e = new Encriptador();
                        String pass=e.getStringMessageDigest(contrasena, e.MD5); 
                        pstpass.setString(2, pass);
                        ResultSet rspass = pstpass.executeQuery(); 
                        rspass.beforeFirst();
                        rspass.next();
                       
                        int numfilaspass = rspass.getInt("count(*)"); 
                        int id = rspass.getInt("id");
                        int tipo = rspass.getInt("tipo");
                        if(numfilaspass>0){
                            //JOptionPane.showMessageDialog(null, "Datos ingresados correctamente.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                            txtusuario.setText("");txtusuario.setEnabled(false);
                            txtpass.setText("");txtpass.setEnabled(false);
                            dispose();
                            Inicio i = new Inicio(id);
                        }else{
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.","Error",JOptionPane.ERROR_MESSAGE);
                        }    
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Datos invalidos.","Error",JOptionPane.ERROR_MESSAGE);
                }
            
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }else{
      JOptionPane.showMessageDialog(null, "Debe ingresar sus datos","Mensaje",JOptionPane.INFORMATION_MESSAGE);
 
    }  */

    }//GEN-LAST:event_btnloginActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnlogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField txtpass;
    private javax.swing.JTextField txtusuario;
    // End of variables declaration//GEN-END:variables
}
