package simu.model;

import java.util.Random;

import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.Kello;
import simu.framework.Moottori;
import simu.framework.Saapumisprosessi;
import simu.framework.Tapahtuma;

public class OmaMoottori extends Moottori {

	private Saapumisprosessi saapumisprosessi;
	private Random random = new Random();

	public OmaMoottori() {

		palvelupisteet = new Palvelupiste[3];
		//Palvelupiste[] daily = new Palvelupiste[3];
		//Palvelupiste[] loan = new Palvelupiste[3];
		
		//daily[0] = new Palvelupiste(new Normal(5, 2), tapahtumalista, TapahtumanTyyppi.DAILY);		// Ilmoittautuminen
		//daily[1] = new Palvelupiste(new Normal(10, 10), tapahtumalista, TapahtumanTyyppi.DAILY);	// Päivittäisraha-asiat
		//daily[2] = new Palvelupiste(new Normal(5, 3), tapahtumalista, TapahtumanTyyppi.DAILY);		// Lainaraha-asiat

		//loan[0] = new Palvelupiste(new Normal(5, 2), tapahtumalista, TapahtumanTyyppi.LOAN);		// Ilmoittautuminen
		//loan[1] = new Palvelupiste(new Normal(10, 10), tapahtumalista, TapahtumanTyyppi.LOAN);	// Päivittäisraha-asiat

		palvelupisteet[0] = new Palvelupiste(new Normal(5, 2), tapahtumalista, TapahtumanTyyppi.RECEPTION);		// Ilmoittautuminen
		palvelupisteet[1] = new Palvelupiste(new Normal(16, 8), tapahtumalista, TapahtumanTyyppi.DAILY);		// Päivittäisraha-asiat
		palvelupisteet[2] = new Palvelupiste(new Normal(48, 22), tapahtumalista, TapahtumanTyyppi.LOAN);		// Lainaraha-asiat

		saapumisprosessi = new Saapumisprosessi(new Negexp(15, 5), tapahtumalista, TapahtumanTyyppi.ARRIVAL);	// Asiakkaan saapuminen

	}

	@Override
	protected void alustukset() {
		saapumisprosessi.generoiSeuraava(); // Ensimmäinen saapuminen järjestelmään
	}

	@Override
	protected void suoritaTapahtuma(Tapahtuma t) { // B-vaiheen tapahtumat

		Asiakas a;
		switch (t.getTyyppi()) {

		case ARRIVAL:
			palvelupisteet[0].lisaaJonoon(new Asiakas());	// Lisätään asiakkaita ilmoittautumisjonoon
			saapumisprosessi.generoiSeuraava();				// Generoidaan uusi saapuminen
			break;
		case RECEPTION:										// Vastaanotto
			a = palvelupisteet[0].otaJonosta();				
			int distribution = random.nextInt(100 - 1) + 1;	// Asiakkaiden jakauma 1-100

			 if (distribution <= 75) {
			 	palvelupisteet[1].lisaaJonoon(a);			// Päivittäisraha-asioihin
			 } else {
			 	palvelupisteet[2].lisaaJonoon(a);			// Lainaraha-asioihin
			 }
			break;
		case DAILY:											// Päivittäisraha-asiat
			a = palvelupisteet[1].otaJonosta();
			a.setPoistumisaika(Kello.getInstance().getAika());
			a.raportti();
			break;
		case LOAN:											// Lainaraha-asiat
			a = palvelupisteet[2].otaJonosta();
			a.setPoistumisaika(Kello.getInstance().getAika());
			a.raportti();
		}
	}

	@Override
	protected void tulokset() {
		int saapuneetDaily = palvelupisteet[1].getSaapuneetAsiakkaat();				// Daily puolelle menneet
		int poistuneetDaily = palvelupisteet[1].getPoistuneetAsiakkaat();			// Daily puolelta poistuneet
		int dailyPalvelemattomat = palvelupisteet[1].getPalvelemattomatAsiakkaat();	// Daily asiakkaat, jotka eivät ehtineet palveltavaksi
		int saapuneetLoan = palvelupisteet[2].getSaapuneetAsiakkaat();				// Loan puolelle menneet
		int poistuneetLoan = palvelupisteet[2].getPoistuneetAsiakkaat();			// Loan puolelta poistuneet
		int loanPalvelemattomat = palvelupisteet[2].getPalvelemattomatAsiakkaat();	// Loan asiakkaat, jotka eivät ehtineet palveltavaksi
		
		int totalSaapuneet = saapuneetDaily + saapuneetLoan;						// Kaikki saapuneet asiakkaat
		int totalPoistuneet = poistuneetDaily + poistuneetLoan;						// Kaikki poistuneet asiakkaat
		int totalPalvelemattomat = totalSaapuneet - totalPoistuneet;				// Kaikki palvelemattomat asiakkaat
		
		double saapuneetPerDaily = (1.0 * saapuneetDaily) / totalSaapuneet * 100;	// Daily saapuneet prosenteissa
		double saapuneetPerLoan = (1.0 * saapuneetLoan) / totalSaapuneet * 100;		// Loan saapuneet prosenteissa
		
		double dailyBusytime = palvelupisteet[1].getBusyTime();						// Daily busytime
		double loanBusytime = palvelupisteet[2].getBusyTime();						// Loan busytime
		
		double dailyUtilization = (dailyBusytime / Kello.getInstance().getAika() * 100.0);
		double loanUtilization = (loanBusytime / Kello.getInstance().getAika()) * 100.0;
		
		System.out.println("\nSimulointi päättyi kello " + Kello.getInstance().getAika() + "\n");

		System.out.println("Asiakkaita saapui yhteensä: " + totalSaapuneet);
		System.out.println("Saapuneiden asiakkaiden jakauma:\n\tDaily: " + Math.round(saapuneetPerDaily * 100.0) / 100.0 + " %\tLoan: " + Math.round(saapuneetPerLoan *100.0) / 100.0 + " %\n");
		System.out.println("Asiakkaita palveltiin yhteensä: " + totalPoistuneet + "\n\tDaily: " + poistuneetDaily + "\tLoan: " + poistuneetLoan + "\n");
		System.out.println("Asiakkaita jäi palvelematta yhteensä: " + totalPalvelemattomat + "\n\tDaily: " + dailyPalvelemattomat + "\tLoan: " + loanPalvelemattomat + "\n");
		System.out.println("Daily käyttöaste: " + Math.round(dailyUtilization * 100.0) / 100.0 + " %\nLoan käyttöaste: " + Math.round(loanUtilization * 100.0) / 100.0 + " %");
	}

}
