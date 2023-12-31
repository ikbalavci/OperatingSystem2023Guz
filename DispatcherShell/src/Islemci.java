/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Islemci implements IIslemci {
    private IOzelProses calisanProses;

    public Islemci() {
        this.calisanProses = null;
    }

    private void prosesCalistir(IOzelProses process, int currentTime, String actionMessage) {
        String messageLeft = "\u001b[0m" + "\u001b[38;5;" + process.getPid() % 256 + "m"
                + String.format("%.4f", (float) currentTime) + " sn proses ";
        String messageMiddle = actionMessage;
        String messageRight = "\t(id:" + String.format("%04d", process.getPid()) + " oncelik:"
                + process.getPriority().ordinal() + " kalan sure:" + process.getPatlamaZamani() + " sn)";
        String message = messageLeft + messageMiddle + messageRight;

        try {
            ProcessBuilder processBuilder = process.getProcessBuilder();
            processBuilder.command().add(message);
            Process p = processBuilder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = "";
            while ((s = in.readLine()) != null) {
                System.out.println(s);
            }
            processBuilder.command().remove(3);
            p.destroyForcibly();
        } catch (Exception e) {
            // Hata durumu ele alınabilir veya loglanabilir
            System.err.println("runProcess metodunda bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Gönderilen prosesi çalıştırır.
    @Override
    public void run(IOzelProses process, int currentTime) {
        // Kuyrukta ve gelecek olan bütün prosesler bittiğinde en son çalışan prosesin
        // bittiğini göstermek için
        if (process == null && this.calisanProses.getStatement() == Ifade.Terminated) {
            this.prosesCalistir(this.calisanProses, currentTime, "sonlandi");
            return;
        }

        // Zaman aşımına uğrayan prosesleri ekrana çıkarmak için
        if (process.getStatement() == Ifade.TimeOut) {
            this.prosesCalistir(process, currentTime, "zamanasimi");
            return;
        }

        // İşlemci ilk defa çalıştığı zaman için
        if (this.calisanProses == null) {
            this.calisanProses = process;
            this.prosesCalistir(this.calisanProses, currentTime, "basladi");
            process.decreaseBurstTime();
            return;
        }

        if (this.calisanProses != process) {
            if (this.calisanProses.getStatement() == Ifade.Terminated) {
                this.prosesCalistir(this.calisanProses, currentTime, "sonlandi");
            } else if (this.calisanProses.getStatement() == Ifade.Ready) {
                this.prosesCalistir(this.calisanProses, currentTime, "askida ");
            }
            this.calisanProses = process;
            this.prosesCalistir(this.calisanProses, currentTime, "basladi");
        } else {
            this.prosesCalistir(process, currentTime, "yurutuluyor");
        }
        process.decreaseBurstTime();
    }
}
