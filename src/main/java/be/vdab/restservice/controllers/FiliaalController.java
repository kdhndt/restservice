package be.vdab.restservice.controllers;

import be.vdab.restservice.domain.Filiaal;
import be.vdab.restservice.exceptions.FiliaalNietGevondenException;
import be.vdab.restservice.services.FiliaalService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

//Spring ziet in @RequestMapping dat de URL /filialen hoort bij Filiaal entities
@ExposesResourceFor(Filiaal.class)
@RestController //gebruik REST controller wanneer de responses geen HTML bevatten, maar XLM of JSON data
@RequestMapping("/filialen")
public class FiliaalController {
    private final FiliaalService filiaalService;
    private final TypedEntityLinks.ExtendedTypedEntityLinks<Filiaal> links;

    //fout mag genegeerd worden, EnityLinks bean wordt standaard aangemaakt door Spring
    public FiliaalController(FiliaalService filiaalService, EntityLinks links) {
        this.filiaalService = filiaalService;
        this.links = links.forType(Filiaal.class, Filiaal::getId);
    }

    //data returnen kan ook, vanwege de @RestController converteert Spring de returnwaarde naar XML of JSON
    @GetMapping("{id}")
    //er zijn verschillende manieren om data terug te geven in je response aan de client:
    //Filiaal --> Filiaal
    //EntityModel<Filiaal> --> Filiaal object met hyperlink(s)
    //EntityModel<FiliaalIdNaam> --> record ipv object (beknoptere info) met hyperlink(s)
    //CollectionModel<EntityModel<FiliaalIdNaaM>> -->  verzameling objecten/records met hyperlink(s)
    EntityModel<Filiaal> get(@PathVariable long id) {
        return filiaalService.findById(id)
                .map(filiaal -> EntityModel.of(filiaal,
                        links.linkToItemResource(filiaal),
                        links.linkForItemResource(filiaal)
                                .slash("werknemers").withRel("werknemers")))
                .orElseThrow(FiliaalNietGevondenException::new);
    }

    @ExceptionHandler(FiliaalNietGevondenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
        //geen response naar de browser, enkel status code
    void filiaalNietGevonden() {
    }

    @GetMapping
    //CollectionModel = verzameling van EntityModel objecten, EntityModel = object/record en hyperlink(s) in JSON/XML formaat -> FiliaalIdNaam + hyperlink
    CollectionModel<EntityModel<FiliaalIdNaam>> findAll() {
        return CollectionModel.of(
                filiaalService.findAll().stream()
                        .map(filiaal -> EntityModel.of(new FiliaalIdNaam(filiaal),
                                links.linkToItemResource(filiaal)))
                //Iterable<EntityModel> obv de stream van EntityModel objecten
                ::iterator,
                //voeg de URL van alle filialen toe aan je CollectionModel
                links.linkToCollectionResource());
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        filiaalService.delete(id);
    }

    @PostMapping
    //status code 201 (Created)
    @ResponseStatus(HttpStatus.CREATED)
    //converteer inkomende JSON data naar een object
    //HttpHeaders datatype kan response headers opbouwen
    HttpHeaders post(@RequestBody @Valid Filiaal filiaal) {
        filiaalService.create(filiaal);
    //stuur een response na het toevoegen van de entity
        var headers = new HttpHeaders();
        //maak een Link object met JSON data die de URL van de parameter filiaal bevat
        //we sturen een Location header met de URL van de toegevoegde Entity
        headers.setLocation(links.linkToItemResource(filiaal).toUri());
        return headers;
    }

    //bovenstaande POST-request throwt een MethodArgumentNotValidException indien het Filiaal object niet @Valid is
    //hieronder wordt de Exception opgevangen
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //Spring maakt een response met deze status code
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //deze method stuurt een response naar de browser als de Exception optreedt
    //de Exception paramater bevat de fouten die in de POST-request zaten
    Map<String, String> verkeerdeData(MethodArgumentNotValidException ex) {
        //geeft een verzameling FieldError objecten
        return ex.getBindingResult().getFieldErrors().stream()
                //maak een Map met K = naam van attribuut + V = validatiefout
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    @PutMapping("{id}")
    void put(@PathVariable long id, @RequestBody @Valid Filiaal filiaal) {
        //filiaal bevat reeds naam, gemeente en omzet
        //filiaal heeft nog geen id, we maken een kopie van het huidig filiaal en voegen het id toe zodat dit filiaal kan worden geüpdatete
        filiaalService.update(filiaal.withId(id));
    }

    private record FiliaalIdNaam(long id, String naam) {
        //maak een eigen constructor binnen je record en gebruik de constructor die het record automatisch bevat om het record in te vullen
        //dit is een alternatief voor new FiliaalIdNaam(filiaal.getId(), filiaal.getNaam()) te gebruiken in je lambda
        FiliaalIdNaam(Filiaal filiaal) {
            this(filiaal.getId(), filiaal.getNaam());
        }
    }
}