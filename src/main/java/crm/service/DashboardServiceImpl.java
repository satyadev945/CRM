package crm.service;

import crm.dto.ContractSummaryDTO;
import crm.dto.CustomerSummaryDTO;
import crm.dto.DashboardStatsDTO;
import crm.entity.Contract;
import crm.entity.Customer;
import crm.entity.Status;
import crm.repository.ContractRepository;
import crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final ContractRepository contractRepository;

    public DashboardServiceImpl(CustomerRepository customerRepository, ContractRepository contractRepository) {
        this.customerRepository = customerRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        // Get active customers count
        Long activeCustomersCount = countActiveCustomers();

        // Get all contracts
        List<Contract> allContracts = (List<Contract>) contractRepository.findAll();

        // Total contracts count
        Long totalContractsCount = (long) allContracts.size();

        // Contracts by status
        Map<Status, Long> contractsByStatus = getContractsByStatus(allContracts);

        // Recent contracts (last 5)
        List<ContractSummaryDTO> recentContracts = getRecentContracts(allContracts);

        // Top customers by contract count (top 5)
        List<CustomerSummaryDTO> topCustomers = getTopCustomers(allContracts);

        return DashboardStatsDTO.builder()
                .activeCustomersCount(activeCustomersCount)
                .totalContractsCount(totalContractsCount)
                .contractsByStatus(contractsByStatus)
                .recentContracts(recentContracts)
                .topCustomers(topCustomers)
                .build();
    }

    private Long countActiveCustomers() {
        Iterable<Customer> activeCustomers = customerRepository.findAllByEnabled(1);
        return StreamSupport.stream(activeCustomers.spliterator(), false).count();
    }

    private Map<Status, Long> getContractsByStatus(List<Contract> contracts) {
        Map<Status, Long> statusMap = contracts.stream()
                .collect(Collectors.groupingBy(Contract::getStatus, Collectors.counting()));

        // Ensure all statuses are present with 0 if not found
        for (Status status : Status.ALL) {
            statusMap.putIfAbsent(status, 0L);
        }

        return statusMap;
    }

    private List<ContractSummaryDTO> getRecentContracts(List<Contract> contracts) {
        return contracts.stream()
                .sorted(Comparator.comparing(Contract::getBeginDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::mapToContractSummary)
                .collect(Collectors.toList());
    }

    private List<CustomerSummaryDTO> getTopCustomers(List<Contract> contracts) {
        // Group contracts by customer and count
        Map<Customer, Long> customerContractCounts = contracts.stream()
                .filter(contract -> contract.getCustomer() != null)
                .collect(Collectors.groupingBy(Contract::getCustomer, Collectors.counting()));

        // Sort by count descending and take top 5
        return customerContractCounts.entrySet().stream()
                .sorted(Map.Entry.<Customer, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> mapToCustomerSummary(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private ContractSummaryDTO mapToContractSummary(Contract contract) {
        return ContractSummaryDTO.builder()
                .id(contract.getId())
                .name(contract.getName())
                .customerName(contract.getCustomer() != null ? contract.getCustomer().getName() : "N/A")
                .status(contract.getStatus())
                .value(contract.getValue())
                .beginDate(contract.getBeginDate())
                .build();
    }

    private CustomerSummaryDTO mapToCustomerSummary(Customer customer, Long contractCount) {
        return CustomerSummaryDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .contractCount(contractCount)
                .build();
    }

}
