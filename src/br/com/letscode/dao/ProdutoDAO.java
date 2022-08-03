package br.com.letscode.dao;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import br.com.letscode.database.Database;
import br.com.letscode.database.FileDatabase;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.model.produto.Produto;

public abstract class ProdutoDAO {
    private static final Database<UUID, Produto> database = new FileDatabase<>(Produto.class, true);

    public static void save(UUID key, Produto entity) throws DatabaseException {
        database.save(key, entity);
    }

    public static void delete(UUID key) throws DatabaseException {
        database.delete(key);
    }

    public static void delete(Produto entity) throws DatabaseException {
        database.delete(entity.getID());
    }

    public static Produto get(UUID key) throws DatabaseException {
        return database.get(key);
    }

    public static Map<UUID, Produto> listAll() throws DatabaseException {
        return database.listAll();
    }

    public static Map<UUID, Produto> listFilter(Predicate<Produto> filter) throws DatabaseException {
        return database.listFilter(filter);
    }
}
