package br.com.letscode.dao;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import br.com.letscode.database.Database;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.model.produto.Carrinho;

public abstract class CarrinhoDAO {
    private static Database<UUID, Carrinho> database;

    public static void setDatabase(Database<UUID, Carrinho> newDatabase) {
        database = newDatabase;
    }

    public static void save(UUID key, Carrinho entity) throws DatabaseException {
        database.save(key, entity);
    }

    public static void delete(UUID key) throws DatabaseException {
        database.delete(key);
    }

    public static Carrinho get(UUID key) throws DatabaseException {
        return database.get(key);
    }

    public static Map<UUID, Carrinho> listAll() throws DatabaseException {
        return database.listAll();
    }

    public static Map<UUID, Carrinho> listFilter(Predicate<Carrinho> filter) throws DatabaseException {
        return database.listFilter(filter);
    }
}
