package br.com.letscode.dao;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

import br.com.letscode.database.Database;
import br.com.letscode.model.produto.ProdutoTipo;
import br.com.letscode.model.produto.Promocao;

public abstract class PromocaoDAO {
    private static final Database<ProdutoTipo, Promocao> database = new Database<>(Promocao.class, true);

    public static void save(ProdutoTipo key, Promocao entity) throws ClassNotFoundException, IOException {
        database.save(key, entity);
    }

    public static void delete(ProdutoTipo key) throws ClassNotFoundException, IOException {
        database.delete(key);
    }

    public static Promocao get(ProdutoTipo key) throws ClassNotFoundException, IOException {
        return database.get(key);
    }

    public static Map<ProdutoTipo, Promocao> listAll() throws ClassNotFoundException, IOException {
        return database.listAll();
    }

    public static Map<ProdutoTipo, Promocao> listFilter(Predicate<Promocao> filter)
            throws ClassNotFoundException, IOException {
        return database.listFilter(filter);
    }
}
