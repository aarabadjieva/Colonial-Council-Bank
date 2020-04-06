package app.ccb.util;

import java.io.*;

public class FileUtilImpl implements FileUtil {
    @Override
    public String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line=br.readLine())!=null){
            sb.append(line).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
