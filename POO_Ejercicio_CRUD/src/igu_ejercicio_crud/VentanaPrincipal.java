package igu_ejercicio_crud;

import poo_ejercicio_crud.ContactManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author Isabel Higuita Giraldo
 */

public class VentanaPrincipal extends JFrame implements ActionListener {

    // ATRIBUTOS
    
    // Instancia de ContactManager para manejar contactos
    private ContactManager contactManager;

    // Componentes gráficos
    private Container contenedor;
    private JLabel nombre, teléfono;
    private JTextField campoNombre, campoTeléfono;
    private JButton añadir, eliminar, editar; // Botón de edición agregado
    private JList<String> listaNombres;
    private DefaultListModel<String> modelo;
    private JScrollPane scrollLista;

    // Controlador del contacto que está siendo editado
    private int indiceEditar = -1;

    // MÉTODOS
    
    /**
     * Constructor de la clase VentanaPrincipal
     */
    public VentanaPrincipal() {
        contactManager = new ContactManager(); // Crear instancia de ContactManager
        inicio();
        setTitle("Contactos");
        setSize(270, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        cargarContactos(); // Cargar contactos al iniciar la ventana
    }

    /**
     * Método para iniciar la interfáz
     */
    private void inicio() {
        contenedor = getContentPane();
        contenedor.setLayout(null);

        nombre = new JLabel("Nombre:");
        nombre.setBounds(20, 20, 135, 23);
        campoNombre = new JTextField();
        campoNombre.setBounds(105, 20, 135, 23);

        teléfono = new JLabel("Teléfono:");
        teléfono.setBounds(20, 50, 135, 23);
        campoTeléfono = new JTextField();
        campoTeléfono.setBounds(105, 50, 135, 23);

        añadir = new JButton("Añadir");
        añadir.setBounds(105, 80, 80, 23);
        añadir.addActionListener(this);

        eliminar = new JButton("Eliminar");
        eliminar.setBounds(20, 280, 80, 23);
        eliminar.addActionListener(this);

        editar = new JButton("Editar"); // Nuevo botón de edición
        editar.setBounds(120, 280, 120, 23);
        editar.addActionListener(this);

        modelo = new DefaultListModel<>();
        listaNombres = new JList<>(modelo);
        listaNombres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scrollLista = new JScrollPane();
        scrollLista.setBounds(20, 120, 220, 150);
        scrollLista.setViewportView(listaNombres);

        contenedor.add(nombre);
        contenedor.add(campoNombre);
        contenedor.add(teléfono);
        contenedor.add(campoTeléfono);
        contenedor.add(añadir);
        contenedor.add(eliminar);
        contenedor.add(editar); // Se agrega el nuevo botón de edición
        contenedor.add(scrollLista);
    }

    /**
     * Método que carga los contactos existentes al iniciar la GUI
     */
    private void cargarContactos() {
        try {
            String[] contactos = contactManager.getContacts();
            for (String contacto : contactos) {
                modelo.addElement(contacto.replace("!", " - "));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar contactos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para definir la acción a ejecutar
     * @param evento
     */
    @Override
    public void actionPerformed(ActionEvent evento) {
        if (evento.getSource() == añadir) {
            if (indiceEditar == -1) {
                añadirContacto(); // Añadir nuevo contacto
            } else {
                editarContacto(); // Editar contacto existente
            }
        } else if (evento.getSource() == eliminar) {
            eliminarContacto();
        } else if (evento.getSource() == editar) {
            cargarParaEditar(); // Cargar contacto seleccionado para edición
        }
    }

    /**
     * Método para añadir un contacto al archivo
     */
    private void añadirContacto() {
        String nombre = campoNombre.getText();
        String teléfono = campoTeléfono.getText();

        if (!nombre.isEmpty() && !teléfono.isEmpty()) {
            try {
                contactManager.addContact(nombre, teléfono);
                modelo.addElement(nombre + " - " + teléfono);
                limpiarCampos();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al añadir contacto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe completar ambos campos", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Método para eliminar un contacto seleccionado
     */
    private void eliminarContacto() {
        int selectedIndex = listaNombres.getSelectedIndex();
        if (selectedIndex != -1) {
            String contacto = modelo.getElementAt(selectedIndex);
            String nombre = contacto.split(" - ")[0];

            try {
                contactManager.deleteContact(nombre);
                modelo.removeElementAt(selectedIndex);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar contacto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para eliminar", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Método para cargar la información del contacto a editar
     */
    private void cargarParaEditar() {
        int selectedIndex = listaNombres.getSelectedIndex();
        if (selectedIndex != -1) {
            String contacto = modelo.getElementAt(selectedIndex);
            String[] datos = contacto.split(" - ");
            campoNombre.setText(datos[0]);
            campoTeléfono.setText(datos[1]);

            // Establecer el índice que se va a editar
            indiceEditar = selectedIndex;
            añadir.setText("Guardar"); // Cambiar el texto del botón a "Guardar"
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para editar", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Método para editar un contacto
     */
    private void editarContacto() {
        String nuevoNombre = campoNombre.getText();
        String nuevoTeléfono = campoTeléfono.getText();

        if (!nuevoNombre.isEmpty() && !nuevoTeléfono.isEmpty()) {
            try {
                // Obtener el contacto actual antes de editar
                String contactoActual = modelo.getElementAt(indiceEditar);
                String nombreActual = contactoActual.split(" - ")[0];

                // Eliminar el contacto viejo
                contactManager.deleteContact(nombreActual);

                // Añadir el contacto actualizado
                contactManager.addContact(nuevoNombre, nuevoTeléfono);

                // Actualizar el modelo de la lista gráfica
                modelo.setElementAt(nuevoNombre + " - " + nuevoTeléfono, indiceEditar);

                limpiarCampos();
                añadir.setText("Añadir"); // Restaurar el texto del botón
                indiceEditar = -1; // Resetear el índice de edición

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al editar contacto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe completar ambos campos", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Método para limpiar los campos del formulario
     */
    private void limpiarCampos() {
        campoNombre.setText("");
        campoTeléfono.setText("");
    }
}
