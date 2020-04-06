package app.ccb.domain.dtos;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchImportDto {

    @Expose
    private String name;
}
