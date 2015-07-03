package org.wso2.appserver.sample.ee.cdi.jpa.jaxws;

import java.util.List;
import javax.ejb.Stateful;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateful
@Named
public class ContactManagerImpl implements ContactManager {
    private static final Log log = LogFactory.getLog(ContactManagerImpl.class);

    @PersistenceContext(name = "ContactsUnit", type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    @Override
    public String addContact(Contact contact) throws Exception {
        try {
            entityManager.persist(contact);
            return "Contact was saved successfully.";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Error Occurred : " + e.getMessage();
        }
    }

    @Override
    public List<Contact> getContacts() {
        Query query = entityManager.createQuery("SELECT Contact contact FROM Contact contact");
        return query.getResultList();
    }

    /*@PostConstruct
    private void addDefaultContacts() {
        Calendar calendar = new GregorianCalendar();
        Contact contact1 = new Contact("Bob", "0711234567", 20, "bob@bob.com", new Date());
        try {
            addContact(contact1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }*/
}
