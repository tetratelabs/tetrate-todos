package io.todos.postgres;

import org.springframework.data.repository.CrudRepository;

public interface TodosRepo extends CrudRepository<Todo, String> {
}
