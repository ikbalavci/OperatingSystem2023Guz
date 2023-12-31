/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
public class Dagitici implements IDagitici {
    private IIslemci islemci;
    private IProcessReader processReader;
    private IProsesKuyruk gercekZamanliKuyruk;
    private IProsesKuyruk yuksekOncelikliKuyruk;
    private IProsesKuyruk ortaOncelikliKuyruk;
    private IProsesKuyruk dusukOncelikliKuyruk;
    private int currentTime;

    // Kaynak için gerekli değişkenlerin teşis edilmesi
    private int memoryExisting;
    private int printersExisting;
    private int scannersExisting;
    private int modemsExisting;
    private int disksExisting;
    private IProsesKuyruk feedbackKuyruk; // bekleyen kuyruk

    public Dagitici(IIslemci processor, IProcessReader processReader) {
        this.islemci = processor;
        this.processReader = processReader;
        this.currentTime = 0;
        this.gercekZamanliKuyruk = new ProsesKuyruk();
        this.yuksekOncelikliKuyruk = new ProsesKuyruk();
        this.ortaOncelikliKuyruk = new ProsesKuyruk();
        this.dusukOncelikliKuyruk = new ProsesKuyruk();
        // kuyruk oluşturuyoruz
        this.feedbackKuyruk = new ProsesKuyruk();
    }

	// Prosesleri uygun kuyruga yerlestirme
	private void queueProcess(IOzelProses process) {
		if (process.getPriority() == Oncelik.RealTime)
			gercekZamanliKuyruk.enqueue(process);
		else if (process.getPriority() == Oncelik.Highest)
			yuksekOncelikliKuyruk.enqueue(process);
		else if (process.getPriority() == Oncelik.Medium)
			ortaOncelikliKuyruk.enqueue(process);
		else // (process.getPriority()==Priority.Lowest)
			dusukOncelikliKuyruk.enqueue(process);
	}

	private IOzelProses getAppropriateProcess() {
		if (!gercekZamanliKuyruk.isEmpty())
			return gercekZamanliKuyruk.getFirstItem();
		else if (!yuksekOncelikliKuyruk.isEmpty())
			return yuksekOncelikliKuyruk.dequeue();
		else if (!ortaOncelikliKuyruk.isEmpty())
			return ortaOncelikliKuyruk.dequeue();
		else if (!dusukOncelikliKuyruk.isEmpty())
			return dusukOncelikliKuyruk.dequeue();
		else if (!feedbackKuyruk.isEmpty())  // Bekleyen kuyruk kontrolü
			return feedbackKuyruk.dequeue();
		else
			return null;
	}
	

	public void prosesBaslatici(IOzelProses process) {
        // kaynakların kontrol edilmesi
        boolean areResourcesAvailable = kaynakKontrolu(process);

        if (areResourcesAvailable) {
            // Gerekli kaynaklar mevcut, prosesi işleme al
            process.setStatement(Ifade.Ready);

            // Prosedure parametrelerini ekrana yazdır
            prosesYazici(process);

            islemci.run(process, currentTime);
            // Kaynakları kullan ve mevcut durumu güncelle
            kaynakKullanimi(process);
            // Diğer işlemler...
        } else {
            // Gerekli kaynaklar mevcut değil, prosesi bekleyen kuyruğa ekle
            process.setStatement(Ifade.Waiting);
            feedbackKuyruk.enqueue(process);
        }
    }

	private void prosesYazici(IOzelProses process) {
        System.out.println("Proses Başlatıldı:");
        System.out.println("Kimlik: " + process.getPid());
        System.out.println("Öncelik: " + process.getPriority());
        System.out.println("Kalan Süre: " + process.getPatlamaZamani());
        System.out.println("Bellek Konumu: " + process.getMemorySize());
        System.out.println("-----------------------------");
    }



    private boolean kaynakKontrolu(IOzelProses process) {
        // Gerekli kaynakları kontrol et ve uygun durumu belirle
        if (process.getMemorySize() <= memoryExisting
                && process.getPrinterCount() <= printersExisting
                && process.getScannerCount() <= scannersExisting
                && process.getModemCount() <= modemsExisting
                && process.getCdCount() <= disksExisting) {
            // Gerekli kaynaklar mevcut
            return true;
        } else {
            // Gerekli kaynaklar mevcut değil
            return false;
        }
    }

    private void kaynakKullanimi(IOzelProses process) {
        // Gerekli kaynakları kullan ve mevcut durumu güncelle
        memoryExisting -= process.getMemorySize();
        printersExisting -= process.getPrinterCount();
        scannersExisting -= process.getScannerCount();
        modemsExisting -= process.getModemCount();
        disksExisting -= process.getCdCount();
    }

	// Bütün kuyrukları gezip 20 saniye bekleme süresini aşan prosesleri
	// sonlandırır.
	private void bitirmeTimeOut() {
		IProsesKuyruk deletedProcesses;

		deletedProcesses = this.yuksekOncelikliKuyruk.increaseWaitingTime();
		while (!deletedProcesses.isEmpty()) {
			var process = deletedProcesses.dequeue();
			process.setStatement(Ifade.TimeOut);
			islemci.run(process, currentTime);
			this.yuksekOncelikliKuyruk.delete(process);
		}

		deletedProcesses = this.ortaOncelikliKuyruk.increaseWaitingTime();
		while (!deletedProcesses.isEmpty()) {
			var process = deletedProcesses.dequeue();
			process.setStatement(Ifade.TimeOut);
			islemci.run(process, currentTime);
			this.ortaOncelikliKuyruk.delete(process);
		}

		deletedProcesses = this.dusukOncelikliKuyruk.increaseWaitingTime();
		while (!deletedProcesses.isEmpty()) {
			var process = deletedProcesses.dequeue();
			process.setStatement(Ifade.TimeOut);
			islemci.run(process, currentTime);
			this.dusukOncelikliKuyruk.delete(process);
		}

		deletedProcesses = this.feedbackKuyruk.increaseWaitingTime();
		while (!deletedProcesses.isEmpty()) {
			var process = deletedProcesses.dequeue();
			process.setStatement(Ifade.TimeOut);
			islemci.run(process, currentTime);
			this.feedbackKuyruk.delete(process);
		}
	}

	// Kaynakları serbest bırak
    private void releaseResources(IOzelProses process) {
        memoryExisting += process.getMemorySize();
        printersExisting += process.getPrinterCount();
        scannersExisting += process.getScannerCount();
        modemsExisting += process.getModemCount();
        disksExisting += process.getCdCount();
    }

	private void terminateProcess(IOzelProses process) {
        process.setStatement(Ifade.Terminated);

        // İlgili kaynakları serbest bırak
        releaseResources(process);

        // Prosedure parametrelerini ekrana yazdır
        printTerminationParameters(process);

        // Diğer işlemler...
    }
	
    private void suspendProcess(IOzelProses process) {
        try {
            process.setStatement(Ifade.Suspended);
    
            // İlgili kaynakları serbest bırak
            releaseResources(process);
    
            // Prosedure parametrelerini ekrana yazdır
            printSuspensionParameters(process);
    
            // Diğer işlemler...
        } catch (Exception e) {
            // Hata durumunu ele alın veya inceleme için kaydedin
            System.err.println("suspendProcess metodunda bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
	
	private void printSuspensionParameters(IOzelProses process) {
		System.out.println("Proses Askıya Alındı:");
		System.out.println("Kimlik: " + process.getPid());
		System.out.println("Öncelik: " + process.getPriority());
		System.out.println("Kalan Süre: " + process.getPatlamaZamani());
		System.out.println("Bellek Konumu: " + process.getMemorySize());
		System.out.println("İşlem askıya alındı.");  // Bu satırı ekledik
		System.out.println("-----------------------------");
	}
	

    private void printTerminationParameters(IOzelProses process) {
		System.out.println("Proses Sonlandırıldı:");
		System.out.println("Kimlik: " + process.getPid());
		System.out.println("Öncelik: " + process.getPriority());
		System.out.println("Kalan Süre: " + process.getPatlamaZamani());
		System.out.println("Bellek Konumu: " + process.getMemorySize());
		System.out.println("İşlem sonlandırıldı.");
		System.out.println("-----------------------------");
	}

	 // Gelen prosesleri kuyruklara ekler ve uygun öncelik sırasına göre işlemleri başlatır.
	 private void checkQueuesForProcesses() {
        IProsesKuyruk receivedProcesses = processReader.getProcesses(currentTime);
        IOzelProses process;

        while (!receivedProcesses.isEmpty()) {
            process = receivedProcesses.dequeue();
            process.setStatement(Ifade.Ready);
            queueProcess(process);
        }

        process = getAppropriateProcess();

        if (process == null && processReader.isEmpty()) {
            this.islemci.run(null, currentTime);
        } else {
            process.setStatement(Ifade.Running);
            islemci.run(process, currentTime);
            ++this.currentTime;
            bitirmeTimeOut();
            process.resetWaitingTime();
            process.decreasePriority();

            if (process.getPatlamaZamani() == 0) {
                if (process.getPriority() == Oncelik.RealTime)
                    gercekZamanliKuyruk.dequeue();
                terminateProcess(process);
            } else {
                process.setStatement(Ifade.Ready);
                if (process.getPriority() != Oncelik.RealTime)
                    queueProcess(process);
            }
        }
    }

    // Prosess listesinde başka proses olup olmadığını kontrol eder.
    private boolean checkForOtherProcesses() {
        return !gercekZamanliKuyruk.isEmpty() || !yuksekOncelikliKuyruk.isEmpty() ||
                !ortaOncelikliKuyruk.isEmpty() || !dusukOncelikliKuyruk.isEmpty() || !feedbackKuyruk.isEmpty();
    }	


	@Override
public void start() {
    while (true) {
        // Yeni prosesleri kuyruklara ekler
        IProsesKuyruk receivedProcesses = processReader.getProcesses(currentTime);
        while (!receivedProcesses.isEmpty()) {
            IOzelProses process = receivedProcesses.dequeue();
            process.setStatement(Ifade.Ready);
            queueProcess(process);
        }

        // Kuyruklarda uygun olan prosesi alır.
        IOzelProses process = getAppropriateProcess();

        // Kuyruklarda proses olmadığı durumları kontrol eder
        if (process == null) {
            // Hem kuyruklarda hem de gelecek başka proses olmadığı durumda işlemleri sonlandırır.
            if (processReader.isEmpty()) {
                this.islemci.run(null, currentTime);
                break;
            } else {
                // Yeni bir zaman dilimine geçer ve bekleme süresi aşan prosesleri sonlandırır.
                ++this.currentTime;
                bitirmeTimeOut();
                continue;
            }
        }

        // Prosess çalıştırma ve kontrol işlemleri
        process.setStatement(Ifade.Running);
        islemci.run(process, currentTime);
        ++this.currentTime;
        bitirmeTimeOut();
        process.resetWaitingTime();
        process.decreasePriority();

        if (process.getPatlamaZamani() == 0) {
            if (process.getPriority() == Oncelik.RealTime)
                gercekZamanliKuyruk.dequeue();
            terminateProcess(process);
        } else {
            process.setStatement(Ifade.Ready);
            if (process.getPriority() != Oncelik.RealTime)
                queueProcess(process);
        }

        // Diğer prosesleri kontrol eder ve gerekirse görevlendirmeyi sonlandırır.
        checkQueuesForProcesses();

        if (!checkForOtherProcesses()) {
            System.out.println("\nGörevlendiriciyi sonlandırma: Prosess listesinde başka proses bulunmuyor.");
            break;
        }
    }
    System.out.println("\nBütün prosesler listelendi.");
}

	

}
