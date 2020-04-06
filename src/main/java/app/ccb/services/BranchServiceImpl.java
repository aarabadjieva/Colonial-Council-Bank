package app.ccb.services;

import app.ccb.domain.dtos.BranchImportDto;
import app.ccb.domain.entities.Branch;
import app.ccb.repositories.BranchRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import com.google.gson.Gson;
import org.aspectj.apache.bcel.classfile.SourceFile;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    private final static String BRANCHES_JSON_FILE_PATH = "C:\\Users\\lin\\Documents\\Programming\\6.Hibernate\\11.EXAM PREPARATION\\ColonialCouncilBankNEW\\src\\main\\resources\\files\\json\\branches.json";

    private final BranchRepository branchRepository;
    private final ModelMapper mapper;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;
    private final Gson gson;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, ModelMapper mapper, FileUtil fileUtil, ValidationUtil validator, Gson gson) {
        this.branchRepository = branchRepository;
        this.mapper = mapper;
        this.fileUtil = fileUtil;
        this.validator = validator;
        this.gson = gson;
    }

    @Override
    public Boolean branchesAreImported() {
        return this.branchRepository.count() != 0;
    }

    @Override
    public String readBranchesJsonFile() throws IOException {
        return this.fileUtil.readFile(BRANCHES_JSON_FILE_PATH);
    }

    @Override
    public String importBranches(String branchesJson) throws IOException {
        branchesJson = readBranchesJsonFile();
        BranchImportDto[] branchImportDtos = this.gson.fromJson(branchesJson, BranchImportDto[].class);
        StringBuilder sb = new StringBuilder();
        for (BranchImportDto branchImportDto : branchImportDtos) {
            Branch branch = this.mapper.map(branchImportDto, Branch.class);
            if (!validator.isValid(branch)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }
            this.branchRepository.saveAndFlush(branch);
            sb.append(String.format("Successfully imported: Branch - %s.", branch.getName()))
                    .append(System.lineSeparator());
            ;
        }
        return sb.toString().trim();
    }
}
