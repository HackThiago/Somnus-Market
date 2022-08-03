package br.com.letscode.dao;

import java.util.Map;
import java.util.function.Predicate;

import br.com.letscode.database.Database;
import br.com.letscode.database.FileDatabase;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.model.produto.ProdutoTipo;
import br.com.letscode.model.produto.Promocao;

public abstract class PromocaoDAO {
    private static final Database<ProdutoTipo, Promocao> database = new FileDatabase<>(Promocao.class, true);

    public static void save(ProdutoTipo key, Promocao entity) throws DatabaseException {
        database.save(key, entity);
    }

    public static void delete(ProdutoTipo key) throws DatabaseException {
        database.delete(key);
    }

    public static Promocao get(ProdutoTipo key) throws DatabaseException {
        return database.get(key);
    }

    public static Map<ProdutoTipo, Promocao> listAll() throws DatabaseException {
        return database.listAll();
    }

    public static Map<ProdutoTipo, Promocao> listFilter(Predicate<Promocao> filter) throws DatabaseException {
        return database.listFilter(filter);
    }
}
