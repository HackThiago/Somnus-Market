package br.com.letscode.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class Database<E extends Serializable, K extends Serializable> {
    final Class<E> DAOClass;

    static final String DATABASE_PATH = "./db";
    static final String DATABASE_FILE_EXTENSION = "csv";
    static final String DATABASE_SEPARATOR = ",";

    public Database(Class<E> DAOClass) {
        this.DAOClass = DAOClass;
    }

    public String getDataBaseTablePath() {
        return DATABASE_PATH + "/" + DAOClass.getSimpleName().toUpperCase() + "." + DATABASE_FILE_EXTENSION;
    }

    public void save(K key, E entity) throws IOException {
        final String IO_EXCEPTION_MESSAGE = "Não foi possível salvar a entidade.";

        try {
            StringBuilder dbLine = new StringBuilder();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(key);
            dbLine.append(Base64.getEncoder().encodeToString(baos.toByteArray()));
            baos.close();
            oos.close();

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(entity);
            dbLine.append(DATABASE_SEPARATOR);
            dbLine.append(Base64.getEncoder().encodeToString(baos.toByteArray()));
            dbLine.append(System.lineSeparator());
            baos.close();
            oos.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(this.getDataBaseTablePath(), true));
            writer.write(dbLine.toString());
            writer.close();
        } catch (IOException e) {
            throw new IOException(IO_EXCEPTION_MESSAGE, e);
        }
    }

    @SuppressWarnings("unchecked")
    public E search(K id) throws IOException, ClassNotFoundException {
        final String IO_EXCEPTION_MESSAGE = "Não foi possível carregar a entidade.";
        final String CLASS_NOT_FOUND_EXCEPTION_MESSAGE = "Não foi possível carregar a entidade.";

        try {
            E entity = null;
            FileReader inputFile = new FileReader(this.getDataBaseTablePath());
            BufferedReader br = new BufferedReader(inputFile);
            String line;
            while ((line = br.readLine()) != null) {
                String[] entityDataBase64 = line.split(DATABASE_SEPARATOR);
                byte[] idData = Base64.getDecoder().decode(entityDataBase64[0]);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(idData));
                K entityId = (K) ois.readObject();
                ois.close();

                if (!entityId.equals(id)) {
                    continue;
                }

                byte[] entityData = Base64.getDecoder().decode(entityDataBase64[1]);
                ois = new ObjectInputStream(new ByteArrayInputStream(entityData));
                entity = (E) ois.readObject();
                ois.close();
                break;
            }
            br.close();
            inputFile.close();

            return entity;
        } catch (IOException e) {
            throw new IOException(IO_EXCEPTION_MESSAGE, e);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(CLASS_NOT_FOUND_EXCEPTION_MESSAGE, e);
        }

    }

    // public List<E> listAll(E entity) {

    // }
}
