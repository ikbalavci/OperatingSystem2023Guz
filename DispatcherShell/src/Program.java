/*
 * 1C B201210070 MUSTAFA İKBAL AVCI
1C B201210052 MUSTAFA KURT
2B G201210558 FUAD GARIBLI
2B G211210087 SELİN ŞAHİN
2B G211210093 BAŞAK MASKAR
 */
public class Program {

	public static void main(String[] args) {
		IProcessReader processReader = new ProsesOkuyucu(args[0]);
		IIslemci processor = new Islemci();
		IDagitici dispatcher = new Dagitici(processor, processReader);

		dispatcher.start();
	}
}
