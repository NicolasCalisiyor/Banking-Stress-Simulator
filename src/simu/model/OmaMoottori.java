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

		palvelupisteet = new Palvelupiste[5];
		/*
		 * paivittaispalvelut = new Palvelupiste[3];
		 * lainapalvelut = new Palvelupiste[2];
		 * 
		*/

		palvelupisteet[0] = new Palvelupiste(new Normal(5, 2), tapahtumalista, TapahtumanTyyppi.RECEPTION);		// Ilmoittautuminen
		palvelupisteet[1] = new Palvelupiste(new Normal(10, 10), tapahtumalista, TapahtumanTyyppi.DAILY_QUEUE);	// Päivittäisraha-asiat
		palvelupisteet[2] = new Palvelupiste(new Normal(5, 3), tapahtumalista, TapahtumanTyyppi.LOAN_QUEUE);		// Lainaraha-asiat
		palvelupisteet[3] = new Palvelupiste(new Normal(30, 25), tapahtumalista, TapahtumanTyyppi.DAILY);		// Lainaraha-asiat
		palvelupisteet[4] = new Palvelupiste(new Normal(50, 10), tapahtumalista, TapahtumanTyyppi.LOAN);		// Lainaraha-asiat

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

			 if (distribution < 75) {
			 	palvelupisteet[1].lisaaJonoon(a);			// Päivittäisraha-asioihin
			 } else {
			 	palvelupisteet[2].lisaaJonoon(a);			// Lainaraha-asioihin
			 }
			break;
		case DAILY_QUEUE:									// Päivittäisraha-asiat jono
			a = palvelupisteet[1].otaJonosta();
			palvelupisteet[3].lisaaJonoon(a);
			break;
		case LOAN_QUEUE:									// Päivittäisraha-asiat jono
			a = palvelupisteet[2].otaJonosta();
			palvelupisteet[4].lisaaJonoon(a);
			break;
		case DAILY:											// Päivittäisraha-asiat
			a = palvelupisteet[3].otaJonosta();
			a.setPoistumisaika(Kello.getInstance().getAika());
			a.raportti();
			break;
		case LOAN:											// Lainaraha-asiat
			a = palvelupisteet[4].otaJonosta();
			a.setPoistumisaika(Kello.getInstance().getAika());
			a.raportti();
		}
	}

	@Override
	protected void tulokset() {
		System.out.println("\nSimulointi päättyi kello " + Kello.getInstance().getAika());
		System.out.println("Tulokset ... puuttuvat vielä");
	}

}
