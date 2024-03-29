package io.todos.redis;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("todosRepo")
public interface TodosRepo extends ListCrudRepository<Todo,String> {
    public List<Todo> findByTitle(String title);
}
