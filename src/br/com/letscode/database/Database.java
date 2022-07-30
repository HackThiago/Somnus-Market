package br.com.letscode.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import br.com.letscode.exception.FalhaInicializandoBancoDeDadosError;

public class Database<K extends Serializable, E extends Serializable> {
    final Class<E> DAOClass;

    static final String DATABASE_PATH = "./db";
    static final String DATABASE_FILE_EXTENSION = "csv";
    static final String DATABASE_SEPARATOR = ",";

    public Database(Class<E> DAOClass) {
        final String EXCEPTION_MESSAGE = "Não foi possível criar o arquivo de dados.";

        this.DAOClass = DAOClass;
        if (!new File(getDataBaseTablePath()).isFile()) {
            try {
                File directory = new File(DATABASE_PATH);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                new File(getDataBaseTablePath()).createNewFile();
            } catch (IOException e) {
                throw new FalhaInicializandoBancoDeDadosError(EXCEPTION_MESSAGE, e);
            }
        }
    }

    public String getDataBaseTablePath() {
        return DATABASE_PATH + "/" + DAOClass.getSimpleName().toUpperCase() + "." + DATABASE_FILE_EXTENSION;
    }

    @SuppressWarnings("unchecked")
    private void editLine(K key, String newLine, boolean addLineIfNotFound) throws IOException, ClassNotFoundException {
        final String EXCEPTION_MESSAGE = "Não foi possível editar a tabela de dados.";

        try {
            BufferedReader databaseTableFile = new BufferedReader(new FileReader(getDataBaseTablePath()));
            StringBuilder inputBuilder = new StringBuilder();
            String line;
            boolean found = false;
            while ((line = databaseTableFile.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }

                byte[] idData = Base64.getDecoder().decode(line.split(DATABASE_SEPARATOR)[0]);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(idData));
                K entityId = (K) ois.readObject();
                ois.close();

                if (entityId.equals(key)) {
                    line = newLine;
                    found = true;
                }

                if (line != null) {
                    inputBuilder.append(line + System.lineSeparator());
                }
            }
            databaseTableFile.close();

            if (addLineIfNotFound && !found) {
                inputBuilder.append(newLine);
            }

            if (inputBuilder.toString().endsWith(System.lineSeparator().repeat(2))) {
                inputBuilder.delete(inputBuilder.length() - System.lineSeparator().length(), inputBuilder.length());
            }

            String prefix = DAOClass.getSimpleName().toUpperCase() + "_";
            String suffix = ".csv.tmp";
            File newFile = File.createTempFile(prefix, suffix, new File(DATABASE_PATH));

            BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            writer.write(inputBuilder.toString());
            writer.close();

            new File(getDataBaseTablePath()).delete();
            newFile.renameTo(new File(getDataBaseTablePath()));

        } catch (IOException e) {
            throw new IOException(EXCEPTION_MESSAGE, e);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(EXCEPTION_MESSAGE, e);
        }
    }

    public void save(K key, E entity) throws IOException, ClassNotFoundException {
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

        editLine(key, dbLine.toString(), true);
    }

    public void delete(K id) throws IOException, ClassNotFoundException {
        editLine(id, null, false);
    }

    @SuppressWarnings("unchecked")
    public E get(K id) throws IOException, ClassNotFoundException {
        final String EXCEPTION_MESSAGE = "Não foi possível carregar a entidade.";

        try {
            E entity = null;
            FileReader inputFile = new FileReader(this.getDataBaseTablePath());
            BufferedReader br = new BufferedReader(inputFile);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }

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
            throw new IOException(EXCEPTION_MESSAGE, e);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(EXCEPTION_MESSAGE, e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<K, E> listFilter(Predicate<E> filter) throws IOException, ClassNotFoundException {
        final String EXCEPTION_MESSAGE = "Não foi possível carregar a entidade.";

        Map<K, E> entities = new HashMap<>();

        try {
            FileReader inputFile = new FileReader(this.getDataBaseTablePath());
            BufferedReader br = new BufferedReader(inputFile);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }

                String[] entityDataBase64 = line.split(DATABASE_SEPARATOR);
                byte[] idData = Base64.getDecoder().decode(entityDataBase64[0]);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(idData));
                K entityId = (K) ois.readObject();
                ois.close();

                byte[] entityData = Base64.getDecoder().decode(entityDataBase64[1]);
                ois = new ObjectInputStream(new ByteArrayInputStream(entityData));
                E entity = (E) ois.readObject();
                ois.close();

                if (filter.test(entity)) {
                    entities.put(entityId, entity);
                }
            }
            br.close();
            inputFile.close();

            return entities;
        } catch (IOException e) {
            throw new IOException(EXCEPTION_MESSAGE, e);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(EXCEPTION_MESSAGE, e);
        }
    }

    public Map<K, E> listAll() throws IOException, ClassNotFoundException {
        return listFilter(entity -> true);
    }
}
