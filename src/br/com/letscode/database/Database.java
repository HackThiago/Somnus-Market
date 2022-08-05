package br.com.letscode.database;

import java.util.Map;
import java.util.function.Predicate;

import br.com.letscode.exception.DatabaseException;

public interface Database<K, E> {
    public void save(K key, E entity) throws DatabaseException;

    public void delete(K key) throws DatabaseException;

    public E get(K key) throws DatabaseException;

    public Map<K, E> listFilter(Predicate<E> filter) throws DatabaseException;

    public Map<K, E> listAll() throws DatabaseException;
}
