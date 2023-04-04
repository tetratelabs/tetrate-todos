package io.todos.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("todosRepo")
public interface TodosRepo extends CrudRepository<Todo,String> {
    public List<Todo> findByTitle(String title);
}
