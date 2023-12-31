/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
public interface IProcessReader {
	// Verilen varış zamanında gelen prosesleri döndürür.
	IProsesKuyruk getProcesses(int destinationTime);

	// Gelecekte başka proses olup olmadığını döndürür.
	boolean isEmpty();
}