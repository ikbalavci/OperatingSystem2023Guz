/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
public class OzelProses implements IOzelProses {
    private ProcessBuilder process;
    private int pid;
    private int destinationTime;
    private Oncelik priority;
    private int burstTime;
    private int waitingTime;
    private Ifade statement;
    private int memorySize;
    private int printerCount;
    private int scannerCount;
    private int modemCount;
    private int cdCount;

    public OzelProses(int pid, int destinationTime, Oncelik priority, int burstTime,
            int memorySize, int printerCount, int scannerCount, int modemCount, int cdCount) {
        this.pid = pid;
        this.destinationTime = destinationTime;
        this.priority = priority;
        this.burstTime = burstTime;
        this.waitingTime = 0;
        this.statement = Ifade.New;
        this.memorySize = memorySize;
        this.printerCount = printerCount;
        this.scannerCount = scannerCount;
        this.modemCount = modemCount;
        this.cdCount = cdCount;

        process = new ProcessBuilder("java", "-jar", "./process.jar");
    }

    @Override
    public ProcessBuilder getProcessBuilder() {
        return this.process;
    }

    @Override
    public int getPid() {
        return this.pid;
    }

    @Override
    public int getDestinationTime() {
        return this.destinationTime;
    }

    @Override
    public Oncelik getPriority() {
        return this.priority;
    }

    @Override
    public void decreasePriority() {
        if (this.priority == Oncelik.Highest) {
            this.priority = Oncelik.Medium;
        } else if (this.priority == Oncelik.Medium) {
            this.priority = Oncelik.Lowest;
        }
    }

    @Override
    public int getPatlamaZamani() {
        return this.burstTime;
    }

    @Override
    public void decreaseBurstTime() {
        if (this.burstTime > 0)
            --this.burstTime;
    }

    @Override
    public boolean increaseWaitingTime() {
        this.waitingTime++;
        return this.waitingTime < 20 ? false : true;
    }

    @Override
    public void resetWaitingTime() {
        this.waitingTime = 0;
    }

    @Override
    public Ifade getStatement() {
        return this.statement;
    }

    @Override
    public void setStatement(Ifade statement) {
        if (statement != null)
            this.statement = statement;
    }

    // Yeni eklenen özelliklere ait getter metotları
    public int getMemorySize() {
        return this.memorySize;
    }

    public int getPrinterCount() {
        return this.printerCount;
    }

    public int getScannerCount() {
        return this.scannerCount;
    }

    public int getModemCount() {
        return this.modemCount;
    }

    public int getCdCount() {
        return this.cdCount;
    }

	
}
