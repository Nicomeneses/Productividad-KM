
package crud1;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class Categorias extends javax.swing.JFrame {
    Conexion con = new Conexion();
    Connection c = con.conectar();
    public int ide;
    
    public Categorias(int ide) {
        super("Categorías");
        this.ide=ide;
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        listarCategorias("");
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
       
    public void listarCategorias(String b){
        String sql;
        try {
            DefaultTableModel tabla = new DefaultTableModel();
            tabla.addColumn("NOMBRE CATEGORÍA"); 
            tbCategorias.setModel(tabla);
                if(b.length()>0 && b!=null){
                    sql="SELECT categoria from categorias WHERE categoria LIKE '%"+b+"%' order by categoria asc";
                }else{
                    sql="SELECT categoria from categorias order by categoria asc";
                }        
                Statement st = c.createStatement();
                String[] datos = new String[2];
                ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){ 
                        datos[0] = rs.getString("categoria");       
                        tabla.addRow(datos);
                    }
            tbCategorias.setModel(tabla);
        } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
 
    public boolean compruebaCat(){
         String cat = txtCatNombre.getText();
         String sql = "SELECT count(*) from categorias where categoria=?";
         try {
           PreparedStatement pstpass = c.prepareStatement(sql);
           pstpass.setString(1, cat);
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
        String cat = txtCatNombre.getText().trim();
        if(cat.length()>0){
            if(!compruebaCat()){
                String sql="INSERT INTO categorias values(null,?)";
                try {
                    PreparedStatement pst = c.prepareStatement(sql);
                    pst.setString(1, cat);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Categoría ingresada con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                    btnInicio();
                } catch (SQLException e) {
               JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }  
            }else{
              JOptionPane.showMessageDialog(null, "Ya existe una categoría con este nombre.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
            }

        }else{
            JOptionPane.showMessageDialog(null, "Debes ingresar un nombre.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void popupMod(){
        String sql,cat;
        int valor = tbCategorias.getSelectedRow();
        if(valor>=0){
            cat=tbCategorias.getValueAt(valor,0).toString();
            txtCatNombre.setText(tbCategorias.getValueAt(valor,0).toString());
            sql="SELECT id,categoria FROM categorias where categoria=?";
            try {   
                String[] datos = new String[1];
                PreparedStatement pst = c.prepareStatement(sql);
                pst.setString(1, cat);
                ResultSet rs = pst.executeQuery(); 
                while(rs.next()){ 
                    datos[0] = rs.getString("id");       
                    idCat.setText(datos[0]);
                    idCat.setVisible(false);
                    popupModBtn();
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
        int valor = tbCategorias.getSelectedRow();
        if(valor>=0){
            cat=tbCategorias.getValueAt(valor,0).toString(); 
            sql="SELECT * from categorias where categoria=?";
            try {
                String[] datos = new String[1];
                PreparedStatement pst = c.prepareStatement(sql);
                pst.setString(1, cat);
                ResultSet rs = pst.executeQuery();
                    while(rs.next()){
                        datos[0] = rs.getString("id"); 
                    }
                    sql2="DELETE FROM categorias where id="+datos[0]+""; 
                    PreparedStatement pst2 = c.prepareStatement(sql2);
                    pst2.executeUpdate(); 
                    JOptionPane.showMessageDialog(null,"Categoria eliminada con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                    listarCategorias("");
                    btnInicio();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage()+sql+sql2,"Error",JOptionPane.ERROR_MESSAGE);
            }

          
        }else{
            JOptionPane.showMessageDialog(null, "Debes seleccionar una fila.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public void btnModcat(){
        String cat = txtCatNombre.getText();       
        String id = idCat.getText();
        String sql = null;  
        if(id.length()>0){
            if(cat.length()>0){    
                    try {     
                        sql="UPDATE categorias set categoria=? where id="+id+"";
                        PreparedStatement pst = c.prepareStatement(sql);
                        pst.setString(1, cat);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null,"Categoria modificada con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                        listarCategorias("");
                        btnInicio();
                    } catch (SQLException e) {
                         JOptionPane.showMessageDialog(null, e.getMessage()+sql,"Error",JOptionPane.ERROR_MESSAGE);
                    }
            }else{
             JOptionPane.showMessageDialog(null, "Debes ingresar un nombre.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
            }
         }
      }
    
    public void btnInicio(){
        btnNuevoCat.setEnabled(true);
        btnCancelCat.setEnabled(false);    
        btnModCat.setEnabled(false);
        btnGuardarCat.setEnabled(false);
        txtCatNombre.setEditable(false);
        txtCatNombre.setText("");
        idCat.setText("");idCat.setVisible(false);
    }
    
    public void popupModBtn(){
        btnNuevoCat.setEnabled(false);
        btnCancelCat.setEnabled(true); 
        btnModCat.setEnabled(true);
        btnGuardarCat.setEnabled(false);
        txtCatNombre.setEditable(true); 
        
    }
    public void btnNuevo(){ 
         btnNuevoCat.setEnabled(false);
         btnCancelCat.setEnabled(true);
         btnModCat.setEnabled(false);
         btnGuardarCat.setEnabled(true);
         txtCatNombre.setEditable(true); 
         idCat.setText("");idCat.setVisible(false);      
    }
    
    public void btnModmenu(){
         btnNuevoCat.setEnabled(false);
         btnCancelCat.setEnabled(true);
         btnModCat.setEnabled(true);
         btnGuardarCat.setEnabled(false);
         txtCatNombre.setEditable(true);
         idCat.setText("");idCat.setVisible(false);
    }
    
    public void btnCancel(){
        btnInicio();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MenuDespl = new javax.swing.JPopupMenu();
        MenuMod = new javax.swing.JMenuItem();
        MenuDel = new javax.swing.JMenuItem();
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
        txtCatNombre = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        btnGuardarCat = new javax.swing.JButton();
        btnNuevoCat = new javax.swing.JButton();
        btnModCat = new javax.swing.JButton();
        btnCancelCat = new javax.swing.JButton();
        idCat = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbCategorias = new javax.swing.JTable();

        MenuMod.setText("Modificar");
        MenuMod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuModActionPerformed(evt);
            }
        });
        MenuDespl.add(MenuMod);

        MenuDel.setText("Eliminar");
        MenuDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuDelActionPerformed(evt);
            }
        });
        MenuDespl.add(MenuDel);

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

        btncategoria.setBackground(new java.awt.Color(220, 220, 220));
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

        txtCatNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCatNombreKeyTyped(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnGuardarCat.setText("Guardar");
        btnGuardarCat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCatActionPerformed(evt);
            }
        });

        btnNuevoCat.setText("Nuevo");
        btnNuevoCat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoCatActionPerformed(evt);
            }
        });

        btnModCat.setText("Modificar");
        btnModCat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModCatActionPerformed(evt);
            }
        });

        btnCancelCat.setText("Cancelar");
        btnCancelCat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelCatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnNuevoCat, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGuardarCat, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnModCat, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelCat, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoCat)
                    .addComponent(btnGuardarCat)
                    .addComponent(btnModCat)
                    .addComponent(btnCancelCat))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(idCat))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(txtCatNombre)))
                .addGap(131, 131, 131))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCatNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(idCat)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        tbCategorias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbCategorias.setComponentPopupMenu(MenuDespl);
        jScrollPane1.setViewportView(tbCategorias);

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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelCatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelCatActionPerformed
     btnCancel();
    }//GEN-LAST:event_btnCancelCatActionPerformed

    private void btnModCatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModCatActionPerformed
     btnModcat();
    }//GEN-LAST:event_btnModCatActionPerformed

    private void btnNuevoCatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoCatActionPerformed
     btnNuevo();
    }//GEN-LAST:event_btnNuevoCatActionPerformed

    private void btnGuardarCatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCatActionPerformed
        btnGuardar();
        listarCategorias("");
    }//GEN-LAST:event_btnGuardarCatActionPerformed

    private void btncerrarsesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncerrarsesionActionPerformed
       dispose();
       Login l = new Login();
    }//GEN-LAST:event_btncerrarsesionActionPerformed

    private void btnusuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnusuariosActionPerformed
          dispose();
    Usuarios c = new Usuarios(ide);
    }//GEN-LAST:event_btnusuariosActionPerformed

    private void btncategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncategoriaActionPerformed
     
    }//GEN-LAST:event_btncategoriaActionPerformed

    private void btnhomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhomeActionPerformed
        dispose();
        Inicio c = new Inicio(ide);
    }//GEN-LAST:event_btnhomeActionPerformed

    private void MenuModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuModActionPerformed
        popupMod();
    }//GEN-LAST:event_MenuModActionPerformed

    private void MenuDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuDelActionPerformed
        popupDel();
    }//GEN-LAST:event_MenuDelActionPerformed

    private void txtCatNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCatNombreKeyTyped
        
    }//GEN-LAST:event_txtCatNombreKeyTyped

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed
  
    }//GEN-LAST:event_txtBuscarKeyPressed

    private void btnproductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnproductosActionPerformed
        dispose();
        Productos p = new Productos(ide);
    }//GEN-LAST:event_btnproductosActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
             String b = txtBuscar.getText();
       listarCategorias(b);
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MenuDel;
    private javax.swing.JPopupMenu MenuDespl;
    private javax.swing.JMenuItem MenuMod;
    private javax.swing.JButton btnCancelCat;
    private javax.swing.JButton btnGuardarCat;
    private javax.swing.JButton btnModCat;
    private javax.swing.JButton btnNuevoCat;
    private javax.swing.JButton btncategoria;
    private javax.swing.JButton btncerrarsesion;
    private javax.swing.JButton btnhome;
    private javax.swing.JButton btnproductos;
    private javax.swing.JButton btnusuarios;
    private javax.swing.JLabel idCat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbCategorias;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCatNombre;
    // End of variables declaration//GEN-END:variables
}
