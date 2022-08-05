package br.com.letscode.database;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import br.com.letscode.exception.DatabaseException;

public class MemoryDatabase<K, E> implements Database<K, E> {

    private final Map<K, E> database = new HashMap<>();

    @Override
    public void save(K key, E entity) throws DatabaseException {
        this.database.put(key, entity);
    }

    @Override
    public void delete(K key) throws DatabaseException {
        this.database.remove(key);
    }

    @Override
    public E get(K key) throws DatabaseException {
        return this.database.get(key);
    }

    @Override
    public Map<K, E> listFilter(Predicate<E> filter) throws DatabaseException {
        Map<K, E> filteredDatabase = new HashMap<>();
        this.database.forEach((key, entity) -> {
            if (filter.test(entity)) {
                filteredDatabase.put(key, entity);
            }
        });

        return filteredDatabase;
    }

    @Override
    public Map<K, E> listAll() throws DatabaseException {
        return new HashMap<>(this.database);
    }
}
