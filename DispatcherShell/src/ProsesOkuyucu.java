/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProsesOkuyucu implements IProcessReader {
    private IProsesKuyruk prosesler;

    public ProsesOkuyucu(String path) {
        this.prosesler = new ProsesKuyruk();
        readFile(path);
    }

    private void readFile(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                Scanner dosya = new Scanner(file);
                int count = 0;
                while (dosya.hasNextLine()) {
                    String line = dosya.nextLine();
                    line = line.replaceAll("\\s+", "");
                    var processInformations = line.split(",");

                    int pid = count;
                    int destinationTime = Integer.parseInt(processInformations[0]);

                    Oncelik priority = switch (Integer.parseInt(processInformations[1])) {
                        case 0 -> Oncelik.RealTime;
                        case 1 -> Oncelik.Highest;
                        case 2 -> Oncelik.Medium;
                        case 3 -> Oncelik.Lowest;
                        default -> throw new IllegalArgumentException(
                                "Unexpected value: " + Integer.parseInt(processInformations[1]));
                    };

                    int burstTime = Integer.parseInt(processInformations[2]);
                    int memorySize = Integer.parseInt(processInformations[3]);
                    int printerCount = Integer.parseInt(processInformations[4]);
                    int scannerCount = Integer.parseInt(processInformations[5]);
                    int modemCount = Integer.parseInt(processInformations[6]);
                    int cdCount = Integer.parseInt(processInformations[7]);

                    IOzelProses process = new OzelProses(pid, destinationTime, priority, burstTime,
                            memorySize, printerCount, scannerCount, modemCount, cdCount);
                    this.prosesler.enqueue(process);
                    ++count;
                }
                dosya.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IProsesKuyruk getProcesses(int destinationTime) {
        IProsesKuyruk foundedProcesses = new ProsesKuyruk();
        IProsesKuyruk tmpQueue = this.prosesler.search(destinationTime);
        IOzelProses tmpProcess;
        while (!tmpQueue.isEmpty()) {
            tmpProcess = tmpQueue.dequeue();
            this.prosesler.delete(tmpProcess);
            foundedProcesses.enqueue(tmpProcess);
        }
        return foundedProcesses;
    }

    @Override
    public boolean isEmpty() {
        return prosesler.isEmpty();
    }
}
