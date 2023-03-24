import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientLog {
    protected List<String[]> list = new ArrayList<>();

    //сохранение корзины
    public void log(int productNum, int amount) {
        list.add(new String[]{String.valueOf(productNum + 1), String.valueOf(amount)});
    }

    //сохранение журнала в файл log.csv
    public void exportAsCSV(File fileCsv) {
        try (FileWriter fr = new FileWriter(fileCsv, false)) {
            fr.write("productNum,amount\n");
            for (String[] line : list) {
                fr.write(line[0] + "," + line[1] + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
