package be.vdab.restservice.controllers;

import be.vdab.restservice.domain.Filiaal;
import be.vdab.restservice.exceptions.FiliaalNietGevondenException;
import be.vdab.restservice.services.FiliaalService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController //gebruik REST controller wanneer de responses geen HTML bevatten, maar XLM of JSON data
@RequestMapping("/filialen")
public class FiliaalController {
    private final FiliaalService filiaalService;

    public FiliaalController(FiliaalService filiaalService) {
        this.filiaalService = filiaalService;
    }

    //data returnen kan ook, vanwege de @RestController converteert Spring de returnwaarde naar XML of JSON
    @GetMapping("{id}")
    Filiaal get(@PathVariable long id) {
        return filiaalService.findById(id)
                .orElseThrow(FiliaalNietGevondenException::new);
    }

    @ExceptionHandler(FiliaalNietGevondenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void filiaalNietGevonden() {
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        filiaalService.delete(id);
    }

    @PostMapping
    //converteer inkomende JSON data naar een object
    void post(@RequestBody Filiaal filiaal) {
        filiaalService.create(filiaal);
    }
}