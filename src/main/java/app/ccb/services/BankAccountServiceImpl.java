package app.ccb.services;

import app.ccb.domain.dtos.BankAccountImportDtos;
import app.ccb.domain.dtos.BankAccountsRootDto;
import app.ccb.domain.entities.BankAccount;
import app.ccb.domain.entities.Client;
import app.ccb.repositories.BankAccountRepository;
import app.ccb.repositories.ClientRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final static String BANK_ACCOUNTS_XML_FILE_PATH = "C:\\Users\\lin\\Documents\\Programming\\6.Hibernate\\11.EXAM PREPARATION\\ColonialCouncilBankNEW\\src\\main\\resources\\files\\xml\\bank-accounts.xml";

    private final BankAccountRepository bankAccountRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper mapper;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, ClientRepository clientRepository, ModelMapper mapper, FileUtil fileUtil, ValidationUtil validator) {
        this.bankAccountRepository = bankAccountRepository;
        this.clientRepository = clientRepository;
        this.mapper = mapper;
        this.fileUtil = fileUtil;
        this.validator = validator;
    }

    @Override
    public Boolean bankAccountsAreImported() {
        return this.bankAccountRepository.count() != 0;
    }

    @Override
    public String readBankAccountsXmlFile() throws IOException {
        return this.fileUtil.readFile(BANK_ACCOUNTS_XML_FILE_PATH);
    }

    @Override
    public String importBankAccounts() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(BankAccountsRootDto.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        BankAccountsRootDto bankAccountsRootDto = (BankAccountsRootDto) unmarshaller.unmarshal(new File(BANK_ACCOUNTS_XML_FILE_PATH));
        StringBuilder sb = new StringBuilder();
        for (BankAccountImportDtos bankAccountImportDto : bankAccountsRootDto.getBankAccountImportDtos()) {
            Client client = this.clientRepository.findByFullName(bankAccountImportDto.getClient()).orElse(null);
            BankAccount bankAccount = mapper.map(bankAccountImportDto, BankAccount.class);
            bankAccount.setClient(client);
            if (!validator.isValid(bankAccount)||client==null){
                sb.append("Error: Invalid Data!").append(System.lineSeparator());
                continue;
            }
            this.bankAccountRepository.saveAndFlush(bankAccount);
            client.setBankAccount(bankAccount);
            sb.append(String.format("Successfully imported: Bank account - %s", bankAccount.getAccountNumber()))
                    .append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
