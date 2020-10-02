package rest;

import dtos.PersonDTO;
import entities.Address;
import entities.Person;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled

public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1, p2, p3;
    
    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        
        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }
    
    @AfterAll
    public static void closeTestServer(){
        //System.in.read();
         //Don't forget this, if you called its counterpart in @BeforeAll
         EMF_Creator.endREST_TestWithDB();
         httpServer.shutdownNow();
    }
    
    @BeforeEach
    public void setUp() {
        Address a1 = new Address("Kobbervej 14", "8405", "Vemdrup");
        Address a2 = new Address("Tolstrupvej", "2100", "København");
        Address a3 = new Address("Ryevej", "9000", "Rostrup");
        
        EntityManager em = emf.createEntityManager(); 
        
        p1 = new Person("Søren", "Hansen", "11111111");
        p1.setAddress(a1);
        p2 = new Person("Franz", "Wilhelm Andersen", "22222222");
        p2.setAddress(a2);
        p3 = new Person("Tove", "Ditlevsen", "33333333");
        p3.setAddress(a3);

        try {
            em.getTransaction().begin();
                em.createNamedQuery("Person.deleteAllRows").executeUpdate(); 
                em.persist(p1);
                em.persist(p2);
                em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }   
    
    @Test
    public void getAllPersons(){
            List<PersonDTO> personDTOList;
        
            personDTOList = given()
                    .contentType("application/json")
                    .when()
                    .get("/person/all")
                    .then()
                    .extract().body().jsonPath().getList("all", PersonDTO.class);
                      
            PersonDTO personDTO1 = new PersonDTO(p1);
            PersonDTO personDTO2 = new PersonDTO(p2);
            PersonDTO personDTO3 = new PersonDTO(p3);
            
            assertThat(personDTOList, containsInAnyOrder(personDTO1, personDTO2, personDTO3));
    }
    
    @Test
    public void addPerson(){
        given()
                .contentType(ContentType.JSON)
                .body(new PersonDTO("Jonas", "Sthur", "33777744", "Engelsborgvej", "2800", "Kgs. Lyngby"))
                .when()
                .post("person")
                .then()
                .body("fName", equalTo("Jonas"))
                .body("lName", equalTo("Sthur"))
                .body("phone", equalTo("33777744"))
                .body("street", equalTo("Engelsborgvej"))
                .body("zip", equalTo("2800"))
                .body("city", equalTo("Kgs. Lyngby"))
                .body("id", notNullValue());
    }
    
    @Test
    public void updatePerson(){
        PersonDTO person = new PersonDTO(p1);
        person.setPhone("44444444");
 
        given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .put("person/"+ person.getId())
                .then()
                .body("phone", equalTo("44444444"))
                .body("id", equalTo((int)person.getId()));
    }
    
      @Test
      public void testDelete() throws Exception {
        
        PersonDTO personDTO = new PersonDTO(p1);
        
        given()
            .contentType("application/json")
            .delete("/person/" + personDTO.getId())
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK_200.getStatusCode());
    }
}