package crm.service;

import crm.entity.Category;
import crm.entity.Customer;
import crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Long getMaxId() {
        return customerRepository.getMaxId();
    }

    @Override
    public Iterable<Customer> listAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer showCustomer(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<Customer> findAllByEnabledTrue() {
        return customerRepository.findAllByEnabled(true);
    }

    @Override
    public Iterable<Customer> findAllByEnabledFalse() {
        return customerRepository.findAllByEnabled(false);
    }

    @Override
    public Customer findOneByEnabledTrueAndName(String name) {
        return customerRepository.findOneByEnabledAndName(true, name);
    }

    @Override
    public Customer findOneByEnabledFalseAndName(String name) {
        return customerRepository.findOneByEnabledAndName(false, name);
    }

    @Override
    public Customer findOneByName(String name) {
        return customerRepository.findOneByName(name);
    }

    @Override
    public Iterable<Customer> findByEnabledTrueAndEmail(String email) {
        return customerRepository.findByEnabledAndEmail(true, email);
    }

    @Override
    public Iterable<Customer> findByEnabledFalseAndEmail(String email) {
        return customerRepository.findByEnabledAndEmail(false, email);
    }

    @Override
    public Iterable<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Iterable<Customer> findByEnabledTrueAndPhone(String phone) {
        return customerRepository.findByEnabledAndPhone(true, phone);
    }

    @Override
    public Iterable<Customer> findByEnabledFalseAndPhone(String phone) {
        return customerRepository.findByEnabledAndPhone(false, phone);
    }

    @Override
    public Iterable<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

    @Override
    public Iterable<Customer> findByEnabledTrueAndCategories(Set<Category> category) {
        return customerRepository.findByEnabledAndCategories(true, category);
    }
//
    @Override
    public Iterable<Customer> findByEnabledFalseAndCategories(Set<Category> category) {
        return customerRepository.findByEnabledAndCategories(false, category);
    }

    @Override
    public Iterable<Customer> findByCategories(Set<Category> category) {
        return customerRepository.findByCategories(category);
    }

    @Override
    public Iterable<Customer> findByEnabledTrueAndFirstName(String firstName) {
        return customerRepository.findByEnabledAndFirstName(true, firstName);
    }

    @Override
    public Iterable<Customer> findByEnabledFalseAndFirstName(String firstName) {
        return customerRepository.findByEnabledAndFirstName(false, firstName);
    }

    @Override
    public Iterable<Customer> findByFirstName(String firstName) {
        return customerRepository.findByFirstName(firstName);
    }

    @Override
    public Iterable<Customer> findByEnabledTrueAndLastName(String lastName) {
        return customerRepository.findByEnabledAndLastName(true, lastName);
    }

    @Override
    public Iterable<Customer> findByEnabledFalseAndLastName(String lastName) {
        return customerRepository.findByEnabledAndLastName(false, lastName);
    }

    @Override
    public Iterable<Customer> findByLastName(String lastName) {
        return customerRepository.findByLastName(lastName);
    }

    @Override
    public Iterable<Customer> findByEnabledTrueAndFirstNameAndLastName(String firstName, String lastName) {
        return customerRepository.findByEnabledAndFirstNameAndLastName(true, firstName, lastName);
    }

    @Override
    public Iterable<Customer> findByEnabledFalseAndFirstNameAndLastName(String firstName, String lastName) {
        return customerRepository.findByEnabledAndFirstNameAndLastName(false, firstName, lastName);
    }

    @Override
    public Iterable<Customer> findByFirstNameAndLastName(String firstName, String lastName) {
        return customerRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public Iterable<Customer> findByEnabledTrueAndCity(String city) {
        return customerRepository.findByEnabledAndCity(true, city);
    }

    @Override
    public Iterable<Customer> findByEnabledFalseAndCity(String city) {
        return customerRepository.findByEnabledAndCity(false, city);
    }

    @Override
    public Iterable<Customer> findByCity(String city) {
        return customerRepository.findByCity(city);
    }

    @Override
    public Iterable<Customer> findByEnabledTrueAndCityAndAddress(String city, String address) {
        return customerRepository.findByEnabledAndCityAndAddress(true, city, address);
    }

    @Override
    public Iterable<Customer> findByEnabledFalseAndCityAndAddress(String city, String address) {
        return customerRepository.findByEnabledAndCityAndAddress(false, city, address);
    }

    @Override
    public Iterable<Customer> findByCityAndAddress(String city, String address) {
        return customerRepository.findByCityAndAddress(city, address);
    }

    @Override
    public void saveCustomer(Customer customer) {
        customer.setEnabled(true);
        customerRepository.save(customer);
    }
//
//    @Override
//    public void editCustomer(Customer customer) {
//        customerRepository.save(customer);
//    }
//
//    @Override
//    public void deleteCustomer(Customer customer) {
//        customer.setEnabled(false);
//        customerRepository.save(customer);
//    }

}
