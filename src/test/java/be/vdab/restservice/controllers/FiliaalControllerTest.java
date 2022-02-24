package be.vdab.restservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
//static methods nodig
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//maakt alle beans (controller, repo, service)
@SpringBootTest
@AutoConfigureMockMvc
@Sql("/insertFiliaal.sql")
//integration test
class FiliaalControllerTest extends AbstractTransactionalJUnit4SpringContextTests {
    //object om HTTP requests te sturen
    private final MockMvc mvc;

    public FiliaalControllerTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    private long idVanTestFiliaal() {
        return jdbcTemplate.queryForObject("select id from filialen where naam = 'test'", Long.class);
    }

    @Test
    void onbestaandFiliaalLezen() throws Exception {
        mvc.perform(get("/filialen/{id}", -1))
                //404 controle
                .andExpect(status().isNotFound());
    }

    @Test void filiaalLezen() throws Exception {
        var id = idVanTestFiliaal();
        mvc.perform(get("/filialen/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id));
    }
}