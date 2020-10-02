package facades;

import exceptions.PersonNotFoundException;
import dtos.PersonDTO;
import dtos.PersonsDTO;
import entities.Address;
import entities.Person;
import facades.IPersonFacade;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import utils.EMF_Creator;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public PersonDTO deletePerson(Integer id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();

        try {
            Person person = em.find(Person.class, id);

            if (person != null) {
                em.getTransaction().begin();
                em.remove(person);
                em.getTransaction().commit();
                return new PersonDTO(person);

            } else {
                throw new PersonNotFoundException(String.format("Person with id (%d) not found", id));
            }
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException {

        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, p.getId());

        //Checks if person exists
        if (person == null) {
            Integer id = p.getId();
            throw new PersonNotFoundException(String.format("Person with id (%d) not found", id));
        }

        try {
            em.getTransaction().begin();

            //Only sets values for the fields if any value was received:
            if (p.getfName() != null) {
                person.setfName(p.getfName());
            }

            if (p.getlName() != null) {
                person.setlName(p.getlName());
            }

            if (p.getPhone() != null) {
                person.setPhone(p.getPhone());
            }

            if (p.getStreet() != null) {
                person.getAddress().setStreet(p.getStreet());
            }

            if (p.getZip() != null) {
                person.getAddress().setZip(p.getZip());
            }

            if (p.getCity() != null) {
                person.getAddress().setCity(p.getCity());
            }

            person.setLastEdited();

            em.getTransaction().commit();
            return new PersonDTO(person);

        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone, String street, String zip, String city) {
        EntityManager em = getEntityManager();
        Person person = new Person(fName, lName, phone);

        try {
            em.getTransaction().begin();

            //Checks in adress already exists in DB:
            Query query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.zip = :zip AND a.city = :city");
            query.setParameter("street", street);
            query.setParameter("zip", zip);
            query.setParameter("city", city);
            List<Address> addresses = query.getResultList();

            //If address already exists then get it from DB and set it:
            if (addresses.size() > 0) {
                person.setAddress(addresses.get(0));
                //If address does not exists then create it at set it:
            } else {
                person.setAddress(new Address(street, zip, city));
            }
            em.persist(person);
            em.getTransaction().commit();
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = getEntityManager();
        try {
            return new PersonsDTO(em.createNamedQuery("Person.getAllRows").getResultList());
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO getPerson(Integer id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        try {
            Person person = em.find(Person.class, id);
            if (person != null) {
                return new PersonDTO(person);
            } else {
                throw new PersonNotFoundException(String.format("Person with id (%d) not found", id));
            }
        } finally {
            em.close();
        }

    }
    
    /*
    public static void main(String[] args) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createQuery("DELETE from Person").executeUpdate();
            em.persist(new Person("Søren", "Jensen", "11111111"));
            em.persist(new Person("Peter", "Hansen", "22222222"));
            em.persist(new Person("Tove", "Ditlevsen", "33333333"));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    */
    
}