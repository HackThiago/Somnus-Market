package br.com.letscode.dao;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import br.com.letscode.database.Database;
import br.com.letscode.model.produto.Produto;

public abstract class ProdutoDAO {
    private static final Database<UUID, Produto> database = new Database<>(Produto.class, true);

    public static void save(UUID key, Produto entity) throws ClassNotFoundException, IOException {
        database.save(key, entity);
    }

    public static void delete(UUID key) throws ClassNotFoundException, IOException {
        database.delete(key);
    }

    public static void delete(Produto entity) throws ClassNotFoundException, IOException {
        database.delete(entity.getID());
    }

    public static Produto get(UUID key) throws ClassNotFoundException, IOException {
        return database.get(key);
    }

    public static Map<UUID, Produto> listAll() throws ClassNotFoundException, IOException {
        return database.listAll();
    }

    public static Map<UUID, Produto> listFilter(Predicate<Produto> filter)
            throws ClassNotFoundException, IOException {
        return database.listFilter(filter);
    }
}
