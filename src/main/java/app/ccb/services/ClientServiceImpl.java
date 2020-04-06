package app.ccb.services;

import app.ccb.domain.dtos.ClientImportDto;
import app.ccb.domain.entities.Client;
import app.ccb.domain.entities.Employee;
import app.ccb.repositories.ClientRepository;
import app.ccb.repositories.EmployeeRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ClientServiceImpl implements ClientService {

    private final static String CLIENTS_JSON_FILE_PATH = "C:\\Users\\lin\\Documents\\Programming\\6.Hibernate\\11.EXAM PREPARATION\\ColonialCouncilBankNEW\\src\\main\\resources\\files\\json\\clients.json";

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, EmployeeRepository employeeRepository, ModelMapper mapper, Gson gson, FileUtil fileUtil, ValidationUtil validator) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.fileUtil = fileUtil;
        this.validator = validator;
    }

    @Override
    public Boolean clientsAreImported() {
        return this.clientRepository.count() != 0;
    }

    @Override
    public String readClientsJsonFile() throws IOException {
        return this.fileUtil.readFile(CLIENTS_JSON_FILE_PATH);
    }

    @Override
    public String importClients(String clients) throws IOException {
        clients = readClientsJsonFile();
        ClientImportDto[] clientImportDtos = this.gson.fromJson(clients, ClientImportDto[].class);
        StringBuilder sb = new StringBuilder();
        for (ClientImportDto clientImportDto : clientImportDtos) {
            Employee employee = this.employeeRepository.findByFirstNameAndLastName(clientImportDto.getEmployee().split("\\s")[0], clientImportDto.getEmployee().split("\\s")[1]).orElse(null);
            Client client = this.clientRepository.findByFullName(clientImportDto.getFirstName() + " " + clientImportDto.getLastName()).orElse(null);
            if (client==null) {
                client = mapper.map(clientImportDto, Client.class);
                client.setFullName(clientImportDto.getFirstName() + " " + clientImportDto.getLastName());
            }
            client.getEmployees().add(employee);
            if (!validator.isValid(client)||employee==null){
                sb.append("Error: Invalid Data!").append(System.lineSeparator());
                continue;
            }
            this.clientRepository.saveAndFlush(client);
            employee.getClients().add(client);
            sb.append(String.format("Successfully imported: Client - %s.", client.getFullName()))
                    .append(System.lineSeparator());
        }
        return sb.toString().trim();
    }

    @Override
    public String exportFamilyGuy() {
        return this.clientRepository.findByBankAccount_CardsCount().get(0).getFullName();
    }
}
