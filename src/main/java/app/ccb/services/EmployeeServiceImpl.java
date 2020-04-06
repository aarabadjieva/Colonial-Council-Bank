package app.ccb.services;

import app.ccb.domain.dtos.EmployeeImportDto;
import app.ccb.domain.entities.Branch;
import app.ccb.domain.entities.Employee;
import app.ccb.repositories.BranchRepository;
import app.ccb.repositories.EmployeeRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final static String EMPLOYEE_JSON_FILE_PATH = "C:\\Users\\lin\\Documents\\Programming\\6.Hibernate\\11.EXAM PREPARATION\\ColonialCouncilBankNEW\\src\\main\\resources\\files\\json\\employees.json";

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, BranchRepository branchRepository, ModelMapper mapper, Gson gson, FileUtil fileUtil, ValidationUtil validator) {
        this.employeeRepository = employeeRepository;
        this.branchRepository = branchRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.fileUtil = fileUtil;
        this.validator = validator;
    }

    @Override
    public Boolean employeesAreImported() {
        return this.employeeRepository.count() != 0;
    }

    @Override
    public String readEmployeesJsonFile() throws IOException {
        return this.fileUtil.readFile(EMPLOYEE_JSON_FILE_PATH);
    }

    @Override
    public String importEmployees(String employees) throws IOException {
        employees = readEmployeesJsonFile();
        EmployeeImportDto[] employeeImportDtos = gson.fromJson(employees, EmployeeImportDto[].class);
        StringBuilder sb = new StringBuilder();
        for (EmployeeImportDto employeeImportDto : employeeImportDtos) {
            Branch branch = this.branchRepository.findByName(employeeImportDto.getBranchName()).orElse(null);
            Employee employee = mapper.map(employeeImportDto, Employee.class);
            employee.setFirstName(employeeImportDto.getFullName().split(" ")[0]);
            employee.setLastName(employeeImportDto.getFullName().split(" ")[1]);
            employee.setStartedOn(LocalDate.parse(employeeImportDto.getStartedOn(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            employee.setBranch(branch);
            if (!validator.isValid(employee) || branch == null) {
                sb.append("Error: Invalid Data!").append(System.lineSeparator());
                continue;
            }
            this.employeeRepository.saveAndFlush(employee);
            sb.append(String.format("Successfully imported: Employee - %s %s.", employee.getFirstName(), employee.getLastName()))
                    .append(System.lineSeparator());
        }
        return sb.toString().trim();
    }

    @Override
    public String exportTopEmployees() {
        List<Employee> employees = this.employeeRepository.findAllByClientsCount();
        StringBuilder sb = new StringBuilder();
        for (Employee employee : employees) {
            sb.append(String.format("Full Name: %s %s\n" +
                    "Salary: %.2f\n" +
                    "Started on: %s\n" +
                    "Clients:\n", employee.getFirstName(),
                    employee.getLastName(), employee.getSalary(),
                    employee.getStartedOn()));
            employee.getClients().forEach(c-> sb.append(c.getFullName()).append(System.lineSeparator()));
        }
        return sb.toString();
    }
}
