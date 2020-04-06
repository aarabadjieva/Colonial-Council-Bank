package app.ccb.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client extends BaseEntity{

    @Column
    @NotNull
    private String fullName;

    @Column
    private int age;

    @OneToOne
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id")
    private BankAccount bankAccount;

    @ManyToMany
    @JoinTable(name = "employees_clients",
    joinColumns = {@JoinColumn(name = "employee_id", referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name = "client_id", referencedColumnName = "id")})
    private List<Employee> employees;

    public Client() {
        this.employees = new ArrayList<>();
    }
}
