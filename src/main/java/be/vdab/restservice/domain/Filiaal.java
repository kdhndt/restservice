package be.vdab.restservice.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
//JAXB vereist standaard getters en setters, op deze manier is dat niet nodig
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "filialen")
public class Filiaal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    private String naam;
    @NotBlank
    private String gemeente;
    @NotNull @PositiveOrZero
    private BigDecimal omzet;

    public Filiaal(String naam, String gemeente, BigDecimal omzet) {
        this.naam = naam;
        this.gemeente = gemeente;
        this.omzet = omzet;
    }

    protected Filiaal() {
    }

    public Filiaal withId(long id) {
        var filiaalMetId = new Filiaal(naam, gemeente, omzet);
        filiaalMetId.id = id;
        return filiaalMetId;
    }


    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public String getGemeente() {
        return gemeente;
    }

    public BigDecimal getOmzet() {
        return omzet;
    }
}
