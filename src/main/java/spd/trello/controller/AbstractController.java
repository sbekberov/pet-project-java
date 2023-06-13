package spd.trello.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.common.Domain;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.service.CommonService;

import java.util.List;
import java.util.UUID;
@Log4j2
public class AbstractController< E extends Domain, S extends CommonService<E>>
        implements CommonController<E> {
    S service;

    public AbstractController(S service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<E> create(@RequestBody E resource) {
        E result = service.create(resource);
        log.debug("Creating entity", result);
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @Override
    public ResponseEntity<E> update(@PathVariable UUID id, @RequestBody E resource) {
        E entity = service.findById(id);
        if (entity == null) throw new ResourceNotFoundException();
        resource.setId(id);
        E result = service.update(resource);
        log.debug("Updating entity", result);
        return new ResponseEntity(result, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @Override
    public HttpStatus delete(@PathVariable UUID id) {
        service.delete(id);
        log.debug("Deleting entity", id);
        return HttpStatus.OK;
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<E> findById(@PathVariable UUID id) {
        E result = service.findById(id);
        log.debug("Finding  entity by id", result);
        return new ResponseEntity(result, HttpStatus.OK);
    }


    @GetMapping()
    @Override
    public List<E> getAll() {
        List<E> result = service.getAll();
        return result;

    }
}
