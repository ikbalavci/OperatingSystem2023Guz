/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
public interface IOzelProses {

	ProcessBuilder getProcessBuilder();

	int getPid();

	int getDestinationTime();

	Oncelik getPriority();

	// Prosesin önceliğini bir derece düşürür.
	void decreasePriority();

	int getPatlamaZamani();

	// Prosesin çalışma süresini bir azaltır.
	void decreaseBurstTime();

	// Prosesin bekleme süresini bir artırır. Eğer 20 olmuşsa true döndürür.
	boolean increaseWaitingTime();

	// Prosesin bekleme süresini sıfırlar.
	void resetWaitingTime();

	int getMemorySize();
    int getPrinterCount();
    int getScannerCount();
    int getModemCount();
    int getCdCount();

	Ifade getStatement(); // Eklenen satır
    void setStatement(Ifade statement); // Eklenen satır
}
