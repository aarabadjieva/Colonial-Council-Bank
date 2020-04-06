package app.ccb.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bank_accounts")
public class BankAccount extends BaseEntity{

    @Column
    @NotNull
    private String accountNumber;

    @Column
    private BigDecimal balance;

    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @OneToMany(mappedBy = "bankAccount")
    private List<Card> cards;
}
