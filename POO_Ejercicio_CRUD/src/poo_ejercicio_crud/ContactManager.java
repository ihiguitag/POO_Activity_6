package poo_ejercicio_crud;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Isabel Higuita Giraldo
 */

public class ContactManager {

    // ATRIBUTOS
    
    // Atributo que almacena el nombre del archivo
    private static final String FILE_NAME = "friendsContact.txt";

    // MÉTODOS
    
    /**
     * Método para añadir un contacto al archivo
     * @param name Parámetro que define el nombre del contacto
     * @param phone Parámetro que define el teléfono del contacto
     * @throws IOException
     */
    public void addContact(String name, String phone) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            boolean found = false;
            while (raf.getFilePointer() < raf.length()) {
                String[] contact = raf.readLine().split("!");
                if (contact[0].equals(name) || contact[1].equals(phone)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                raf.seek(file.length());
                raf.writeBytes(name + "!" + phone + System.lineSeparator());
            }
        }
    }

    /**
     * Método para leer todos los contactos del archivo
     * @throws IOException
     * @return String[]
     */
    public String[] getContacts() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new String[0];
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            StringBuilder contacts = new StringBuilder();
            while (raf.getFilePointer() < raf.length()) {
                contacts.append(raf.readLine()).append("\n");
            }
            return contacts.toString().split("\n");
        }
    }

    /**
     * Método para eliminar un contacto (buscar por nombre)
     * @param name Parámetro que define el nombre del contacto
     * @throws IOException
     */
    public void deleteContact(String name) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        File tempFile = new File("temp.txt");
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             RandomAccessFile tempRaf = new RandomAccessFile(tempFile, "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                String contact = raf.readLine();
                String[] contactSplit = contact.split("!");
                if (!contactSplit[0].equals(name)) {
                    tempRaf.writeBytes(contact + System.lineSeparator());
                }
            }
        }
        file.delete();
        tempFile.renameTo(file);
    }
}
