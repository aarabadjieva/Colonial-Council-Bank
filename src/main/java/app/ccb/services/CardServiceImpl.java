package app.ccb.services;

import app.ccb.domain.dtos.CardImportDto;
import app.ccb.domain.dtos.CardRootDto;
import app.ccb.domain.entities.BankAccount;
import app.ccb.domain.entities.Card;
import app.ccb.repositories.BankAccountRepository;
import app.ccb.repositories.CardRepository;
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
public class CardServiceImpl implements CardService {

    private final static String CARDS_XML_FILE_PATH = "C:\\Users\\lin\\Documents\\Programming\\6.Hibernate\\11.EXAM PREPARATION\\ColonialCouncilBankNEW\\src\\main\\resources\\files\\xml\\cards.xml";

    private final CardRepository cardRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper mapper;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, BankAccountRepository bankAccountRepository, ModelMapper mapper, FileUtil fileUtil, ValidationUtil validator) {
        this.cardRepository = cardRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.mapper = mapper;
        this.fileUtil = fileUtil;
        this.validator = validator;
    }

    @Override
    public Boolean cardsAreImported() {
        return this.cardRepository.count() != 0;
    }

    @Override
    public String readCardsXmlFile() throws IOException {
        return this.fileUtil.readFile(CARDS_XML_FILE_PATH);
    }

    @Override
    public String importCards() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CardRootDto.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        CardRootDto cardRootDto = (CardRootDto) unmarshaller.unmarshal(new File(CARDS_XML_FILE_PATH));
        StringBuilder sb = new StringBuilder();
        for (CardImportDto cardImportDto : cardRootDto.getCards()) {
            BankAccount bankAccount = this.bankAccountRepository.findByAccountNumber(cardImportDto.getAccountNumber()).orElse(null);
            Card card = mapper.map(cardImportDto, Card.class);
            card.setBankAccount(bankAccount);
            if (!validator.isValid(card)||bankAccount==null){
                sb.append("Error: Invalid Data!").append(System.lineSeparator());
                continue;
            }
            this.cardRepository.saveAndFlush(card);
            bankAccount.getCards().add(card);
            sb.append(String.format("Successfully imported: Card - %s", card.getCardNumber()))
                    .append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
