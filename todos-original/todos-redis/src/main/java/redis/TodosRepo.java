package io.todos.redis;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("todosRepo")
public interface TodosRepo extends PagingAndSortingRepository<Todo,String> {
    public List<Todo> findByTitle(String title);
}
