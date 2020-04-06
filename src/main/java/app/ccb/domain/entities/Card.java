package app.ccb.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card extends BaseEntity{

    @Column
    @NotNull
    private String cardNumber;

    @Column
    @NotNull
    private String cardStatus;

    @ManyToOne
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id")
    private BankAccount bankAccount;
}
