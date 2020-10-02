package facades;

import dtos.PersonDTO;
import dtos.PersonsDTO;
import entities.Address;
import utils.EMF_Creator;
import entities.Person;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled

public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person p1, p2, p3;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    //Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
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

    @AfterEach
    public void tearDown() {
    // Remove any data after each test was run
    }

    @Test
    public void testGetPersonFacade() {
        EntityManagerFactory _emf = null;
        PersonFacade expectedResult = null;
        PersonFacade result = PersonFacade.getPersonFacade(_emf);
        assertNotEquals(expectedResult, result);
    }

    @Test
    public void testAddPerson() throws Exception {
        String fName = "Jonas";
        String lName = "Sthur";
        String phone = "33777744";
        String street = "Engelsborgvej";
        String zip = "2800";
        String city = "Kgs. Lyngby";
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getPersonFacade(_emf);
        PersonDTO result = instance.addPerson(fName, lName, phone, street, zip, city);
        PersonDTO expectedResult = new PersonDTO(fName, lName, phone, street, zip, city);
        expectedResult.setId(expectedResult.getId());
        assertEquals(expectedResult.getfName(), result.getfName());
        assertEquals(expectedResult.getlName(), result.getlName());
        assertEquals(expectedResult.getPhone(), result.getPhone());
    }
    
    @Test
    public void testEditPerson() throws Exception {
        PersonDTO personDTO = new PersonDTO(p1);
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getPersonFacade(_emf);
        PersonDTO expectedResult = new PersonDTO(p1);
        expectedResult.setfName("Jonas");
        personDTO.setfName("Jonas");
        PersonDTO result = instance.editPerson(personDTO);
        assertEquals(expectedResult.getfName(), result.getfName());
    }
    
    @Test
    public void testGetPerson() throws Exception {
        Integer id = p3.getId();
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getPersonFacade(_emf);
        PersonDTO expectedResult = new PersonDTO(p3);
        PersonDTO result = instance.getPerson(id);
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void testGetAllPersons() {
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getPersonFacade(_emf);
        int expectedResult = 3;
        PersonsDTO result = instance.getAllPersons();
        assertEquals(expectedResult, result.getAll().size());
    }
    
    @Test
    public void testDeletePerson() throws Exception {
        Integer id = p3.getId();
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getPersonFacade(_emf);
        PersonDTO expectedResult = new PersonDTO(p3);
        PersonDTO result = instance.deletePerson(id);
        assertEquals(expectedResult, result);
    }
}