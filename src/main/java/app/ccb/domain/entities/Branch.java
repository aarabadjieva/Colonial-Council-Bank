package app.ccb.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "branches")
public class Branch extends BaseEntity{

    @Column
    @NotNull
    private String name;
}
