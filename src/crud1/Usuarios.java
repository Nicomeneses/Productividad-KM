
package crud1;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Usuarios extends JFrame {
     Conexion con = new Conexion();
     Connection c = con.conectar();
     public int ide;
     
     public Usuarios(int ide) {
        super("Usuarios");
        this.ide=ide;
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        llenarCombo();
        listarUsuarios("");
        btnInicio();
        getTipo(ide);
    }
    
        public void getTipo(int id){
        try {
            String sql="select tipo from usuarios2 where id=?";
            PreparedStatement pst = c.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery(); 
            rs.beforeFirst();
            rs.next(); 
            int tipo = rs.getInt("tipo");
             switch(tipo){
                case 1:
                btnusuarios.setVisible(true);
                break;  
                case 2:
                btnusuarios.setVisible(false);
                break;     
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void llenarCombo(){
        comboUsTipo.addItem("Seleccione...");
        comboUsTipo.addItem("Administrador");
        comboUsTipo.addItem("Usuario");
        comboUsTipo.setSelectedItem(0);
    }
    
    public void listarUsuarios(String b){
        String sql;
        try {
            DefaultTableModel tabla = new DefaultTableModel();
            tabla.addColumn("NOMBRE USUARIO");
            tabla.addColumn("CORREO");
            tabla.addColumn("TIPO");
            tbUsuarios.setModel(tabla);
                if(b.length()>0 && b!=null){
                    sql="SELECT * FROM usuarios2 WHERE  nombre LIKE '%"+b+"%' order by tipo asc";
                }else{
                    sql="SELECT * FROM usuarios2 order by tipo asc";
                }           
                Statement st = c.createStatement();
                String[] datos = new String[3];
                ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){ 
                        datos[0] = rs.getString("nombre");       
                        datos[1] = rs.getString("usuario"); 
                        datos[2] = rs.getString("tipo");
                        switch (rs.getString("tipo")) {
                            case "1":
                                datos[2]="Administrador";
                                break;
                            case "2":
                                datos[2]="Usuario";
                                break;
                        }
                        tabla.addRow(datos);
                    }
                    
            tbUsuarios.setModel(tabla);
        } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }     
    }
    
     public boolean compruebaCat(){
         String us = txtUsNombre.getText();
         String sql = "SELECT count(*) from usuarios2 where nombre=?";
         try {
           PreparedStatement pstpass = c.prepareStatement(sql);
           pstpass.setString(1, us);
           ResultSet rspass = pstpass.executeQuery(); 
           rspass.beforeFirst();
           rspass.next();
           int numfilaspass = rspass.getInt("count(*)"); 
            if(numfilaspass>0){
                return true;
            } else{
                return false;
            } 
        } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
         

    public void btnGuardar(){
        String nom = txtUsNombre.getText().trim();
        String us =txtUsUsuario.getText().toLowerCase().trim();  
        Encriptador e = new Encriptador();
        String password = e.getStringMessageDigest(txtUsPassword.getText().toLowerCase().trim(), e.MD5); 
        int tipo =comboUsTipo.getSelectedIndex();            
             //JOptionPane.showMessageDialog(null, StringMD.getStringMessageDigest(password, StringMD.MD5),"Mensaje",JOptionPane.INFORMATION_MESSAGE);
             if(nom.length()>0 && us.length()>0 && password.length()>0 && tipo>0 ){
                 if(!compruebaCat()){
                     String sql="INSERT INTO usuarios2 values(null,?,?,?,?)";
                     try {
                         PreparedStatement pst = c.prepareStatement(sql);
                         pst.setString(1, nom);
                         pst.setString(2, us);
                         pst.setString(3, password);
                         pst.setInt(4, tipo);
                         pst.executeUpdate();
                         JOptionPane.showMessageDialog(null, "Usuario registrado con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                         btnInicio();
                         listarUsuarios("");
                     } catch (SQLException ex) {
                         JOptionPane.showMessageDialog(null, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                     }
                 }else{
                     JOptionPane.showMessageDialog(null, "Ya existe un usuario con este nombre.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                 }
                 
             }else{
                 JOptionPane.showMessageDialog(null, "Debes rellenar los datos.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
             } 
    }
    
    
    public void popupMod(){
        String sql,nom;
        int valor = tbUsuarios.getSelectedRow();
        if(valor>=0){
            nom=tbUsuarios.getValueAt(valor,0).toString();
            //txtCatNombre.setText(tbCategorias.getValueAt(valor,0).toString());
            sql="SELECT * FROM usuarios2 where nombre=?";
            try {   
                String[] datos = new String[4];
                PreparedStatement pst = c.prepareStatement(sql);
                pst.setString(1, nom);
                ResultSet rs = pst.executeQuery(); 
                while(rs.next()){ 
                    datos[0] = rs.getString("id"); 
                    datos[1] = rs.getString("nombre"); 
                    datos[2] = rs.getString("usuario"); 
                    datos[3] = rs.getString("tipo"); 
                    idUsuario.setText(datos[0]);
                    txtUsNombre.setText(datos[1]);txtUsNombre.setEnabled(true);
                    txtUsUsuario.setText(datos[2]);txtUsUsuario.setEditable(true);
                    comboUsTipo.setSelectedIndex(Integer.parseInt(datos[3]));comboUsTipo.setEnabled(true);
                    idUsuario.setVisible(false);                   
                    popupModBtn2();
                }
            } catch (SQLException e) {
               JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
         JOptionPane.showMessageDialog(null, "Debes seleccionar una fila.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void popupDel(){
        String sql="",sql2="",cat;
        int valor = tbUsuarios.getSelectedRow();
        if(valor>=0){
            cat=tbUsuarios.getValueAt(valor,1).toString(); 
            sql="SELECT * from usuarios2 where nombre=?";
            try {
                String[] datos = new String[1];
                PreparedStatement pst = c.prepareStatement(sql);
                pst.setString(1, cat);
                ResultSet rs = pst.executeQuery();
                    while(rs.next()){
                        datos[0] = rs.getString("id"); 
                    }
                    sql2="DELETE FROM usuarios2 where id="+datos[0]+""; 
                    PreparedStatement pst2 = c.prepareStatement(sql2);
                    pst2.executeUpdate(); 
                    JOptionPane.showMessageDialog(null,"Usuario eliminado con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                    listarUsuarios("");
                    btnInicio();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }

          
        }else{
            JOptionPane.showMessageDialog(null, "Debes seleccionar una fila.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void btnModUs(){
        String nom = txtUsNombre.getText().trim(); 
        String usu = txtUsUsuario.getText().trim();
        int tipo = comboUsTipo.getSelectedIndex();
        String id = idUsuario.getText().trim();
        String sql = null;  
        if(id.length()>0){
            if(nom.length()>0 && tipo>0 && usu.length()>0){
                    try {     
                        sql="UPDATE usuarios2 set nombre=?,usuario=?,tipo=? where id="+id+"";
                        PreparedStatement pst = c.prepareStatement(sql);
                        pst.setString(1, nom);
                        pst.setString(2, usu);
                        pst.setInt(3, tipo);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null,"Usuario modificado con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                        listarUsuarios("");
                        btnInicio();
                    } catch (SQLException e) {
                         JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }

            }else{
             JOptionPane.showMessageDialog(null, "Debes ingresar los datos.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
            }
         }
      }

     public void controlCampos(boolean b){
        for(int i=0; jPanel2.getComponents().length>i;i++){
           if(jPanel2.getComponents()[i]instanceof JTextField){
               if(b==true){
                   ((JTextField)jPanel2.getComponents()[i]).setEnabled(false);  
                   ((JTextField)jPanel2.getComponents()[i]).setText("");
               }else{
                   ((JTextField)jPanel2.getComponents()[i]).setEnabled(true);  
                   ((JTextField)jPanel2.getComponents()[i]).setText("");
               }
           }
 
           else if(jPanel2.getComponents()[i]instanceof JComboBox){
               if(b==true){
                   ((JComboBox)jPanel2.getComponents()[i]).setEnabled(false);  
                   ((JComboBox)jPanel2.getComponents()[i]).setSelectedIndex(0);
               }else{
                 ((JComboBox)jPanel2.getComponents()[i]).setEnabled(true);
               }
           }   
        }
    }
     
    public void btnInicio(){
        btnNuevoUs.setEnabled(true);
        btnCancelUs.setEnabled(false);    
        btnModUs.setEnabled(false);
        btnGuardarUs.setEnabled(false);
        controlCampos(true);
        idUsuario.setText("");idUsuario.setVisible(false);
    }
    
    public void popupModBtn2(){
        btnNuevoUs.setEnabled(false);
        btnCancelUs.setEnabled(true); 
        btnModUs.setEnabled(true);
        btnGuardarUs.setEnabled(false);
        txtUsNombre.setEnabled(true); 
        txtUsUsuario.setEnabled(true);   
    }
    
    public void btnNuevo(){ 
         btnNuevoUs.setEnabled(false);
         btnCancelUs.setEnabled(true);
         btnModUs.setEnabled(false);
         btnGuardarUs.setEnabled(true);
         txtUsNombre.setEditable(true); 
         controlCampos(false);
         idUsuario.setText("");idUsuario.setVisible(false);      
    }
    
    public void btnModmenu(){
         btnNuevoUs.setEnabled(false);
         btnCancelUs.setEnabled(true);
         btnModUs.setEnabled(true);
         btnGuardarUs.setEnabled(false);
         txtUsNombre.setEditable(true);
         idUsuario.setText("");idUsuario.setVisible(false);
    }
    
    public void btnCancel(){
        btnInicio();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        popupMod = new javax.swing.JMenuItem();
        popupEli = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        btnhome = new javax.swing.JButton();
        btncategoria = new javax.swing.JButton();
        btnproductos = new javax.swing.JButton();
        btnusuarios = new javax.swing.JButton();
        btncerrarsesion = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtUsNombre = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        btnGuardarUs = new javax.swing.JButton();
        btnNuevoUs = new javax.swing.JButton();
        btnModUs = new javax.swing.JButton();
        btnCancelUs = new javax.swing.JButton();
        idUsuario = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtUsPassword = new javax.swing.JPasswordField();
        txtUsUsuario = new javax.swing.JTextField();
        comboUsTipo = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbUsuarios = new javax.swing.JTable();

        popupMod.setText("Modificar");
        popupMod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupModActionPerformed(evt);
            }
        });
        jPopupMenu1.add(popupMod);

        popupEli.setText("Eliminar");
        popupEli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupEliActionPerformed(evt);
            }
        });
        jPopupMenu1.add(popupEli);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(750, 550));

        toolbar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setAlignmentY(0.4898F);
        toolbar.setName(""); // NOI18N

        btnhome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnhome.setIcon(new javax.swing.ImageIcon("C:\\Users\\ricardo\\Documents\\NetBeansProjects\\CRUD1\\icons\\Home.png")); // NOI18N
        btnhome.setText("    Inicio    ");
        btnhome.setFocusable(false);
        btnhome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnhome.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnhome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhomeActionPerformed(evt);
            }
        });
        toolbar.add(btnhome);

        btncategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btncategoria.setIcon(new javax.swing.ImageIcon("C:\\Users\\ricardo\\Documents\\NetBeansProjects\\CRUD1\\icons\\List.png")); // NOI18N
        btncategoria.setText("    Categorías    ");
        btncategoria.setFocusable(false);
        btncategoria.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btncategoria.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btncategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncategoriaActionPerformed(evt);
            }
        });
        toolbar.add(btncategoria);

        btnproductos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnproductos.setIcon(new javax.swing.ImageIcon("C:\\Users\\ricardo\\Documents\\NetBeansProjects\\CRUD1\\icons\\Database.png")); // NOI18N
        btnproductos.setText("    Productos    ");
        btnproductos.setFocusable(false);
        btnproductos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnproductos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnproductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnproductosActionPerformed(evt);
            }
        });
        toolbar.add(btnproductos);

        btnusuarios.setBackground(new java.awt.Color(220, 220, 220));
        btnusuarios.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnusuarios.setIcon(new javax.swing.ImageIcon("C:\\Users\\ricardo\\Documents\\NetBeansProjects\\CRUD1\\icons\\Boss.png")); // NOI18N
        btnusuarios.setText("    Usuarios    ");
        btnusuarios.setFocusable(false);
        btnusuarios.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnusuarios.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnusuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnusuariosActionPerformed(evt);
            }
        });
        toolbar.add(btnusuarios);

        btncerrarsesion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btncerrarsesion.setIcon(new javax.swing.ImageIcon("C:\\Users\\ricardo\\Documents\\NetBeansProjects\\CRUD1\\icons\\User group.png")); // NOI18N
        btncerrarsesion.setText("    Cerrar Sesión    ");
        btncerrarsesion.setFocusable(false);
        btncerrarsesion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btncerrarsesion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btncerrarsesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncerrarsesionActionPerformed(evt);
            }
        });
        toolbar.add(btncerrarsesion);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("LOGO");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Ingreso de Categorias"));

        jLabel2.setText("Nombre");

        txtUsNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUsNombreKeyTyped(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnGuardarUs.setText("Guardar");
        btnGuardarUs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarUsActionPerformed(evt);
            }
        });

        btnNuevoUs.setText("Nuevo");
        btnNuevoUs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoUsActionPerformed(evt);
            }
        });

        btnModUs.setText("Modificar");
        btnModUs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModUsActionPerformed(evt);
            }
        });

        btnCancelUs.setText("Cancelar");
        btnCancelUs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelUsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnNuevoUs, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGuardarUs, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnModUs, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelUs, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoUs)
                    .addComponent(btnGuardarUs)
                    .addComponent(btnModUs)
                    .addComponent(btnCancelUs))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Usuario");

        jLabel5.setText("Contraseña");

        jLabel6.setText("Tipo");

        comboUsTipo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboUsTipoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(idUsuario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addGap(53, 53, 53)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtUsNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(txtUsUsuario))
                .addGap(46, 46, 46)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtUsPassword)
                    .addComponent(comboUsTipo, 0, 203, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtUsPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(idUsuario))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtUsNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUsUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3)
                            .addComponent(comboUsTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Consulta de Categorías"));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setText("Busqueda");

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(jLabel4)
                .addGap(97, 97, 97)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        tbUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbUsuarios.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(tbUsuarios);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(118, 118, 118))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnhomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhomeActionPerformed
        dispose();
        Inicio c = new Inicio(ide);
    }//GEN-LAST:event_btnhomeActionPerformed

    private void btncategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncategoriaActionPerformed
        dispose();
        Categorias c = new Categorias(ide);
    }//GEN-LAST:event_btncategoriaActionPerformed

    private void btnproductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnproductosActionPerformed
        dispose();
        Productos p = new Productos(ide);
    }//GEN-LAST:event_btnproductosActionPerformed

    private void btnusuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnusuariosActionPerformed
  
    }//GEN-LAST:event_btnusuariosActionPerformed

    private void btncerrarsesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncerrarsesionActionPerformed
        dispose();
        Login l = new Login();
    }//GEN-LAST:event_btncerrarsesionActionPerformed

    private void txtUsNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsNombreKeyTyped

    }//GEN-LAST:event_txtUsNombreKeyTyped

    private void btnGuardarUsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarUsActionPerformed
       btnGuardar();
     
    }//GEN-LAST:event_btnGuardarUsActionPerformed

    private void btnNuevoUsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoUsActionPerformed
        btnNuevo();
    }//GEN-LAST:event_btnNuevoUsActionPerformed

    private void btnModUsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModUsActionPerformed
        btnModUs();
    }//GEN-LAST:event_btnModUsActionPerformed

    private void btnCancelUsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelUsActionPerformed
        btnCancel();
    }//GEN-LAST:event_btnCancelUsActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed

    }//GEN-LAST:event_txtBuscarKeyPressed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        String b = txtBuscar.getText();
        listarUsuarios(b);
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void comboUsTipoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboUsTipoItemStateChanged

    }//GEN-LAST:event_comboUsTipoItemStateChanged

    private void popupModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupModActionPerformed
     popupMod();
    }//GEN-LAST:event_popupModActionPerformed

    private void popupEliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupEliActionPerformed
      popupDel();
    }//GEN-LAST:event_popupEliActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelUs;
    private javax.swing.JButton btnGuardarUs;
    private javax.swing.JButton btnModUs;
    private javax.swing.JButton btnNuevoUs;
    private javax.swing.JButton btncategoria;
    private javax.swing.JButton btncerrarsesion;
    private javax.swing.JButton btnhome;
    private javax.swing.JButton btnproductos;
    private javax.swing.JButton btnusuarios;
    private javax.swing.JComboBox comboUsTipo;
    private javax.swing.JLabel idUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem popupEli;
    private javax.swing.JMenuItem popupMod;
    private javax.swing.JTable tbUsuarios;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtUsNombre;
    private javax.swing.JPasswordField txtUsPassword;
    private javax.swing.JTextField txtUsUsuario;
    // End of variables declaration//GEN-END:variables
}
