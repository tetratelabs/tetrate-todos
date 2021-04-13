package io.todos.postgres;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class TodosAPI {

    @Autowired
    private TodosRepo _repo;

    @GetMapping("/")
    public List<Todo> retrieve() {
        // Create an empty list
        List<Todo> list = new ArrayList<Todo>();
        _repo.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public Todo create(@RequestBody Todo todo) {
        Todo obj = new Todo();
        if(ObjectUtils.isEmpty(todo.getId())) {
            obj.setId(UUID.randomUUID().toString());
        } else {
            obj.setId(todo.getTitle());
        }
        if(!ObjectUtils.isEmpty(todo.getTitle())) {
            obj.setTitle(todo.getTitle());
        }
        if(!ObjectUtils.isEmpty(todo.isComplete())) {
            obj.setComplete(todo.isComplete());
        }
        return _repo.save(obj);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}")
    public Todo put(@PathVariable String id, @RequestBody Todo todo) {
        if(todo.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "todos.id cannot be null on put");
        }
        if(todo.getId() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "todos.id ${todo.id} and id $id are inconsistent");
        }
        return _repo.save(todo);
    }

    @PostMapping("/load/{size}")
    public void load(@PathVariable("size") int size) {
        for(int i=0; i < size; i++) {
            Todo todo = new Todo();
            todo.setId(UUID.randomUUID().toString());
            todo.setTitle("make bacon pancakes $i");
            todo.setComplete(false);
            _repo.save(todo);
        }
    }

    @DeleteMapping("/")
    public void deleteAll() {
        _repo.deleteAll();
    }

    @GetMapping("/{id}")
    public Todo retrieve(@PathVariable("id") String id) {
        Optional<Todo> t =  _repo.findById(id);
        if(!t.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "todo.id=${id}");
        }
        return t.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id){
        _repo.deleteById(id);
    }

    @PatchMapping("/{id}")
    public Todo update(@PathVariable("id") String id, @RequestBody Todo todo) {
        Todo existing = retrieve(id);
        if (!ObjectUtils.isEmpty(todo.isComplete())) {
            existing.setComplete(todo.isComplete());
        }
        if (!StringUtils.isEmpty(todo.getTitle())) {
            existing.setTitle(todo.getTitle());
        }
        return _repo.save(existing);
    }
}
