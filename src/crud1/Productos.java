package crud1;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;

public class Productos extends JFrame {
    Conexion con = new Conexion();
    Connection c = con.conectar();
    public int ide;
    private FileInputStream Stream;
    private int longitudBytes;
    private Image imagen;

    public Productos(int ide) {
        super("Productos");
        initComponents();
        this.ide=ide;
        comboCat();
        btnInicio();
        listadoProd("");
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);     
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
       
    public void listadoProd(String b){
        String sql;
        try {
            DefaultTableModel tabla = new DefaultTableModel();
            tabla.addColumn("CATEGORÍA"); 
            tabla.addColumn("NOMBRE"); 
            tabla.addColumn("DESCRIPCIÓN");
            tabla.addColumn("PRECIO");
            tbProductos.setModel(tabla);
                if(b.length()>0 && b!=null){
                    sql="SELECT c.categoria as categoria ,p.nombre as nombre,p.descripcion as descripcion,"
                            + " p.precio as precio "
                            + " FROM productos as p LEFT JOIN categorias as c "
                            + " ON p.id_categoria=c.id "
                            + " WHERE c.categoria LIKE '%"+b+"%' or "
                            + " p.nombre LIKE '%"+b+"%' "    
                            + " ORDER BY c.categoria ASC ";
                }else{
                    sql="SELECT c.categoria as categoria ,p.nombre as nombre,p.descripcion as descripcion,"
                            + " p.precio as precio "
                            + " FROM productos as p LEFT JOIN categorias as c "
                            + " ON p.id_categoria=c.id "
                            + " ORDER BY c.categoria ASC ";
                }        
                Statement st = c.createStatement();
                String[] datos = new String[4];
                ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){ 
                        datos[0] = rs.getString("categoria");
                        datos[1] = rs.getString("nombre");
                        datos[2] = rs.getString("descripcion");
                        datos[3] = rs.getString("precio");
                        tabla.addRow(datos);
                    }
            tbProductos.setModel(tabla);
        } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public void guardarProd(){
        String nom = txtProdNombre.getText().trim();
        int cat = comboProdCat.getSelectedIndex();
        String desc = txtProdDesc.getText().trim();
        String img = null;
        String min = "no";
        String p = txtProdPrecio.getText();
        int precio = Integer.parseInt(txtProdPrecio.getText());
        
        if(nom.length()>0 && cat>0 &&  precio>0){
         if(!compruebaProd()){
            //tomar el index numerico de cada item, que sera igual alos id de la bd
            //si esto no es posible,se tendra que tomar el nombre del item y consultar el id por el nombre
            String sql="INSERT into productos values(null,?,?,?,?,?,?)";
            try {
                PreparedStatement pst = c.prepareStatement(sql);
                pst.setInt(1, cat);
                pst.setString(2,nom);
                pst.setString(3, desc);
                pst.setInt(4,precio);
                pst.setString(5,img);pst.setString(6,min);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Producto ingresado con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                btnInicio();
                listadoProd("");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
         }else{
            JOptionPane.showMessageDialog(null, "Ya existe un producto con ese nombre.","Error",JOptionPane.ERROR_MESSAGE);
         }
        }else{
           JOptionPane.showMessageDialog(null, "Debes rellenar los campos.","Error",JOptionPane.ERROR_MESSAGE);

        }
    }
    
    public boolean compruebaProd(){
         String cat = txtProdNombre.getText();
         String sql = "SELECT count(*) from productos where nombre=?";
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
    
     public void comboCat(){
        try {
            String sql;
            sql="SELECT * from categorias order by id asc";
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            comboProdCat.addItem("Seleccione...");
            while(rs.next()){ 
                comboProdCat.addItem(rs.getString("categoria"));
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public void popupMod(){
        String sql,nom;
        int valor = tbProductos.getSelectedRow();
        if(valor>=0){
            nom=tbProductos.getValueAt(valor,1).toString();
            //txtCatNombre.setText(tbCategorias.getValueAt(valor,0).toString());
            sql="SELECT * FROM productos where nombre=?";
            try {   
                String[] datos = new String[5];
                PreparedStatement pst = c.prepareStatement(sql);
                pst.setString(1, nom);
                ResultSet rs = pst.executeQuery(); 
                while(rs.next()){ 
                    datos[0] = rs.getString("id"); 
                    datos[1] = rs.getString("nombre"); 
                    datos[2] = rs.getString("descripcion"); 
                    datos[3] = rs.getString("precio"); 
                    datos[4] = rs.getString("id_categoria"); 
                    idprod.setText(datos[0]);
                    txtProdNombre.setText(datos[1]);txtProdNombre.setEnabled(true);
                    txtProdDesc.setText(datos[2]);txtProdDesc.setEditable(true);
                    txtProdPrecio.setText(datos[3]);txtProdPrecio.setEnabled(true);  
                    comboProdCat.setSelectedIndex(Integer.parseInt(datos[4]));comboProdCat.setEnabled(true);
                    idprod.setVisible(false);                   
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
        int valor = tbProductos.getSelectedRow();
        if(valor>=0){
            cat=tbProductos.getValueAt(valor,1).toString(); 
            sql="SELECT * from productos where nombre=?";
            try {
                String[] datos = new String[1];
                PreparedStatement pst = c.prepareStatement(sql);
                pst.setString(1, cat);
                ResultSet rs = pst.executeQuery();
                    while(rs.next()){
                        datos[0] = rs.getString("id"); 
                    }
                    sql2="DELETE FROM productos where id="+datos[0]+""; 
                    PreparedStatement pst2 = c.prepareStatement(sql2);
                    pst2.executeUpdate(); 
                    JOptionPane.showMessageDialog(null,"Producto eliminado con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                    listadoProd("");
                    btnInicio();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }

          
        }else{
            JOptionPane.showMessageDialog(null, "Debes seleccionar una fila.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void btnModProd(){
        String nom = txtProdNombre.getText().trim(); 
        int cat = comboProdCat.getSelectedIndex();
        String desc = txtProdDesc.getText().trim();  
        int precio = Integer.parseInt(txtProdPrecio.getText());  
        String id = idprod.getText().trim();
        String sql = null;  
        if(id.length()>0){
            if(nom.length()>0 && cat>0){
                    try {     
                        sql="UPDATE productos set nombre=?,id_categoria=?,descripcion=?,precio=? where id="+id+"";
                        PreparedStatement pst = c.prepareStatement(sql);
                        pst.setString(1, nom);
                        pst.setInt(2, cat);
                        pst.setString(3, desc);
                        pst.setInt(4, precio);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null,"Producto modificado con éxito.","Mensaje",JOptionPane.INFORMATION_MESSAGE);
                        listadoProd("");
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
                   txtProdDesc.setEditable(false);
                   txtProdDesc.setText("");
               }else{
                   ((JTextField)jPanel2.getComponents()[i]).setEnabled(true);  
                   ((JTextField)jPanel2.getComponents()[i]).setText("");
                   txtProdDesc.setEditable(true);
                   txtProdDesc.setText("");
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
        btnNuevoProd.setEnabled(true);
        btnCancelProd.setEnabled(false);    
        btnModProd.setEnabled(false);
        btnGuardarProd.setEnabled(false);
        controlCampos(true);
        lbImagen.setEnabled(false);
        idprod.setText("");idprod.setVisible(false);
    }
    
    public void popupModBtn(){
        btnNuevoProd.setEnabled(false);
        btnCancelProd.setEnabled(true); 
        btnModProd.setEnabled(true);
        btnGuardarProd.setEnabled(false);
    }
    
    public void btnNuevo(){ 
         btnNuevoProd.setEnabled(false);
         btnCancelProd.setEnabled(true);
         btnModProd.setEnabled(false);
         btnGuardarProd.setEnabled(true);   
         idprod.setText("");idprod.setVisible(false);    
         lbImagen.setEnabled(true);
    }
    
    public void btnModmenu(){
         btnNuevoProd.setEnabled(false);
         btnCancelProd.setEnabled(true);
         btnModProd.setEnabled(true);
         btnGuardarProd.setEnabled(false);
         idprod.setText("");idprod.setVisible(false);
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
        logo = new javax.swing.JPanel();
        lblogo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtProdNombre = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        btnGuardarProd = new javax.swing.JButton();
        btnNuevoProd = new javax.swing.JButton();
        btnModProd = new javax.swing.JButton();
        btnCancelProd = new javax.swing.JButton();
        idprod = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtProdPrecio = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtProdDesc = new javax.swing.JTextArea();
        comboProdCat = new javax.swing.JComboBox();
        lbImagen = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbProductos = new javax.swing.JTable();

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
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(750, 600));

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

        btnproductos.setBackground(new java.awt.Color(220, 220, 220));
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

        logo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblogo.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblogo.setText("LOGO");

        javax.swing.GroupLayout logoLayout = new javax.swing.GroupLayout(logo);
        logo.setLayout(logoLayout);
        logoLayout.setHorizontalGroup(
            logoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(lblogo, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        logoLayout.setVerticalGroup(
            logoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Ingreso de Productos"));

        jLabel2.setText("Nombre");

        txtProdNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtProdNombreKeyTyped(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnGuardarProd.setText("Guardar");
        btnGuardarProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProdActionPerformed(evt);
            }
        });

        btnNuevoProd.setText("Nuevo");
        btnNuevoProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoProdActionPerformed(evt);
            }
        });

        btnModProd.setText("Modificar");
        btnModProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModProdActionPerformed(evt);
            }
        });

        btnCancelProd.setText("Cancelar");
        btnCancelProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelProdActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnNuevoProd, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGuardarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnModProd, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelProd, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoProd)
                    .addComponent(btnGuardarProd)
                    .addComponent(btnModProd)
                    .addComponent(btnCancelProd))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Categoría");

        jLabel5.setText("Descripción");

        jLabel6.setText("Precio");

        jLabel7.setText("Imagen");

        txtProdPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtProdPrecioKeyTyped(evt);
            }
        });

        txtProdDesc.setColumns(20);
        txtProdDesc.setRows(5);
        txtProdDesc.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtProdDesc);

        comboProdCat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboProdCatActionPerformed(evt);
            }
        });

        lbImagen.setText("       Selecciona Archivo");
        lbImagen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbImagenMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idprod)
                .addGap(45, 45, 45)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProdNombre)
                            .addComponent(txtProdPrecio)
                            .addComponent(comboProdCat, 0, 204, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(jLabel7)
                                .addGap(37, 37, 37)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane2)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(lbImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(101, Short.MAX_VALUE))))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7)
                    .addComponent(comboProdCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtProdNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addGap(23, 23, 23))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(txtProdPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(idprod)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Consulta de Productos"));
        jPanel7.setPreferredSize(new java.awt.Dimension(650, 210));

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
                .addGap(89, 89, 89)
                .addComponent(jLabel4)
                .addGap(87, 87, 87)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jPanel8.setPreferredSize(new java.awt.Dimension(452, 150));

        tbProductos.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tbProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbProductos.setComponentPopupMenu(MenuDespl);
        jScrollPane1.setViewportView(tbProductos);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                    .addComponent(logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    }//GEN-LAST:event_btnproductosActionPerformed

    private void btnusuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnusuariosActionPerformed
    dispose();
    Usuarios c = new Usuarios(ide);
    }//GEN-LAST:event_btnusuariosActionPerformed

    private void btncerrarsesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncerrarsesionActionPerformed
        dispose();
        Login l = new Login();
    }//GEN-LAST:event_btncerrarsesionActionPerformed

    private void txtProdNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProdNombreKeyTyped

    }//GEN-LAST:event_txtProdNombreKeyTyped

    private void btnGuardarProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProdActionPerformed
    JFileChooser filech = new JFileChooser();
          File archivo = filech.getSelectedFile();
          if(this.longitudBytes>0){
              guardarProd();
          }else{
              guardarProd();
          }
    }//GEN-LAST:event_btnGuardarProdActionPerformed

    private void btnNuevoProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoProdActionPerformed
        btnNuevo();
        controlCampos(false);
    }//GEN-LAST:event_btnNuevoProdActionPerformed

    private void btnModProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModProdActionPerformed
       btnModProd();
    }//GEN-LAST:event_btnModProdActionPerformed

    private void btnCancelProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelProdActionPerformed
        btnCancel();
    }//GEN-LAST:event_btnCancelProdActionPerformed

    private void comboProdCatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboProdCatActionPerformed

    }//GEN-LAST:event_comboProdCatActionPerformed

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed
        
    }//GEN-LAST:event_txtBuscarKeyPressed

    private void txtProdPrecioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProdPrecioKeyTyped
       char caracter = evt.getKeyChar();
      // Verificar si la tecla pulsada no es un digito
      if(((caracter < '0') ||
         (caracter > '9')) &&
         (caracter != '\b' /*corresponde a BACK_SPACE*/)){     
         evt.consume();  // ignorar el evento de teclado
      } 
    }//GEN-LAST:event_txtProdPrecioKeyTyped

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
       String b = txtBuscar.getText();
       listadoProd(b);
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void MenuModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuModActionPerformed
        popupMod();
    }//GEN-LAST:event_MenuModActionPerformed

    private void MenuDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuDelActionPerformed
        popupDel();
    }//GEN-LAST:event_MenuDelActionPerformed

    private void lbImagenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbImagenMouseClicked
     if(lbImagen.isEnabled()){
        //creamos un selector de archivos
        JFileChooser filech = new JFileChooser();
        //solo archivos, no carpetas
        filech.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int estado = filech.showOpenDialog(null);
        if(estado == JFileChooser.APPROVE_OPTION){
          //si se acepta
            try {
                //capturamos el archivo y la longitud
                Stream = new FileInputStream(filech.getSelectedFile());
                this.longitudBytes =(int)filech.getSelectedFile().length();
                //redimencionamos la imagen y la añadimos al area como icono
                Image icono = ImageIO.read(filech.getSelectedFile()).getScaledInstance(lbImagen.getWidth(),lbImagen.getHeight(), Image.SCALE_DEFAULT);
                lbImagen.setText("");
                lbImagen.setIcon(new ImageIcon(icono));
                lbImagen.updateUI();//       
 
            } catch (FileNotFoundException e) {
                 JOptionPane.showMessageDialog(null, e.getMessage(),"Mensaje",JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                 JOptionPane.showMessageDialog(null, e.getMessage(),"Mensaje",JOptionPane.INFORMATION_MESSAGE);

            }
        }
      }
    }//GEN-LAST:event_lbImagenMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MenuDel;
    private javax.swing.JPopupMenu MenuDespl;
    private javax.swing.JMenuItem MenuMod;
    private javax.swing.JButton btnCancelProd;
    private javax.swing.JButton btnGuardarProd;
    private javax.swing.JButton btnModProd;
    private javax.swing.JButton btnNuevoProd;
    private javax.swing.JButton btncategoria;
    private javax.swing.JButton btncerrarsesion;
    private javax.swing.JButton btnhome;
    private javax.swing.JButton btnproductos;
    private javax.swing.JButton btnusuarios;
    private javax.swing.JComboBox comboProdCat;
    private javax.swing.JLabel idprod;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbImagen;
    private javax.swing.JLabel lblogo;
    private javax.swing.JPanel logo;
    private javax.swing.JTable tbProductos;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextArea txtProdDesc;
    private javax.swing.JTextField txtProdNombre;
    private javax.swing.JTextField txtProdPrecio;
    // End of variables declaration//GEN-END:variables
}
