package org.wso2.appserver.sample.ee.cdi.jpa.jaxws;

import javax.ejb.Stateful;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateful
@Named
public class ContactManagerImpl implements ContactManager {

    @PersistenceContext(name = "ContactsUnit", type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    @Override
    public String addContact(Contact contact) throws Exception {
        try {
            entityManager.persist(contact);
            return "Contact was saved successfully.";
        } catch (ConstraintViolationException e) {
            throw new Exception("Field constraints violated, please recheck and try again." + e.getCause().getMessage(), e);
        }
    }

    @Override
    public List<Contact> getContacts() {
        Query query = entityManager.createQuery("SELECT Contact contact FROM Contact contact");
        return query.getResultList();
    }
}
