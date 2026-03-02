package crm.repository;

import crm.entity.Category;
import crm.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * NOTICE
 * <p>
 * When some declaration
 * does NOT HAVE Enabled param
 * searching works for ALL customers
 * also for NOT enabled (inactive) ones
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // PostgreSQL compatible query - uses COALESCE for null safety
    @Query(value = "SELECT COALESCE(MAX(id), 0) FROM customer", nativeQuery = true)
    Long getMaxId();

    Iterable<Customer> findAllByEnabled(boolean enabled);

    Customer findOneByEnabledAndName(boolean enabled, String name);
    Customer findOneByName(String name);

    Iterable<Customer> findByEnabledAndEmail(boolean enabled, String email);
    Iterable<Customer> findByEmail(String email);

    Iterable<Customer> findByEnabledAndCity(boolean enabled, String city);
    Iterable<Customer> findByCity(String city);

    Iterable<Customer> findByEnabledAndCityAndAddress(boolean enabled, String city, String address);
    Iterable<Customer> findByCityAndAddress(String city, String address);

    Iterable<Customer> findByEnabledAndPhone(boolean enabled, String phone);
    Iterable<Customer> findByPhone(String phone);

    Iterable<Customer> findByEnabledAndFirstName(boolean enabled, String firstName);
    Iterable<Customer> findByFirstName(String firstName);

    Iterable<Customer> findByEnabledAndLastName(boolean enabled, String lastName);
    Iterable<Customer> findByLastName(String lastName);

    Iterable<Customer> findByEnabledAndFirstNameAndLastName(boolean enabled, String firstName, String lastName);
    Iterable<Customer> findByFirstNameAndLastName(String firstName, String lastName);

    Iterable<Customer> findByEnabledAndCategories(boolean enabled, Set<Category> category);
    Iterable<Customer> findByCategories(Set<Category> category);

}
