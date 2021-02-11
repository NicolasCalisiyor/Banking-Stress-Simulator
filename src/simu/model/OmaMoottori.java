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

		palvelupisteet = new Palvelupiste[6];
		palvelupisteet[0] = new Palvelupiste(new Normal(5, 2), tapahtumalista, TapahtumanTyyppi.DAILY_RECEPTION);
		palvelupisteet[1] = new Palvelupiste(new Normal(5, 2), tapahtumalista, TapahtumanTyyppi.LOAN_RECEPTION);
		palvelupisteet[2] = new Palvelupiste(new Normal(20, 5), tapahtumalista, TapahtumanTyyppi.BILLING);
		palvelupisteet[3] = new Palvelupiste(new Normal(10, 7), tapahtumalista, TapahtumanTyyppi.WITHDRAW_DEPOSIT);
		palvelupisteet[4] = new Palvelupiste(new Normal(75, 25), tapahtumalista, TapahtumanTyyppi.HOUSE_LOAN);
		palvelupisteet[5] = new Palvelupiste(new Normal(50, 15), tapahtumalista, TapahtumanTyyppi.STUDENT_LOAN);

		saapumisprosessi = new Saapumisprosessi(new Negexp(15, 5), tapahtumalista, TapahtumanTyyppi.ARRIVAL);

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
			int distribution = random.nextInt(100 - 1) + 1;

			if (distribution <= 80) {
				palvelupisteet[0].lisaaJonoon(new Asiakas()); // Dailyyn
			} else {
				palvelupisteet[1].lisaaJonoon(new Asiakas()); // Loaniin
			}
			saapumisprosessi.generoiSeuraava(); // Generoidaan uusi saapuminen
			break;

		case DAILY_RECEPTION:
			a = palvelupisteet[0].otaJonosta();
			int distribution_daily = random.nextInt(100 - 1) + 1;

			if (distribution_daily <= 70) {
				palvelupisteet[2].lisaaJonoon(a); // Billing
			} else {
				palvelupisteet[3].lisaaJonoon(a); // Depo/With
			}
			break;

		case BILLING:
			a = palvelupisteet[2].otaJonosta();
			a.setPoistumisaika(Kello.getInstance().getAika());
			a.raportti();
			break;

		case WITHDRAW_DEPOSIT:
			a = palvelupisteet[3].otaJonosta();
			a.setPoistumisaika(Kello.getInstance().getAika());
			a.raportti();
			break;

		case LOAN_RECEPTION:
			a = palvelupisteet[1].otaJonosta();
			int distribution_loan = random.nextInt(100 - 1) + 1;

			if (distribution_loan <= 75) {
				palvelupisteet[4].lisaaJonoon(a); // House
			} else {
				palvelupisteet[5].lisaaJonoon(a); // Student
			}
			break;

		case HOUSE_LOAN:
			a = palvelupisteet[4].otaJonosta();
			a.setPoistumisaika(Kello.getInstance().getAika());
			a.raportti();
			break;

		case STUDENT_LOAN:
			a = palvelupisteet[5].otaJonosta();
			a.setPoistumisaika(Kello.getInstance().getAika());
			a.raportti();
			break;
		}
	}

	@Override
	protected void tulokset() {
		/*
		 * 0 = DAILY_RECEPTION 1 = LOAN_RECEPTION 2 = BILLING 3 = WITHDRAW_DEPOSIT 4 =
		 * HOUSE_LOAN 5 = STUDENT_LOAN
		 */

		int saapuneetDaily = palvelupisteet[0].getSaapuneetAsiakkaat();
		int saapuneetLoan = palvelupisteet[1].getSaapuneetAsiakkaat();
		int saapuneetBilling = palvelupisteet[2].getSaapuneetAsiakkaat();
		int saapuneetWithDepo = palvelupisteet[3].getSaapuneetAsiakkaat();
		int saapuneetHouse = palvelupisteet[4].getSaapuneetAsiakkaat();
		int saapuneetStudent = palvelupisteet[5].getSaapuneetAsiakkaat();
		
		System.out.println("Saapuneiden asiakkaiden jakaumat:");
		System.out.println("Päivittäispalvelut: " + saapuneetDaily);
		System.out.println("\tLaskutus: " + saapuneetBilling + "\n\tNosto ja talletus: " + saapuneetWithDepo);
		
		System.out.println("Lainapalvelut: " + saapuneetLoan);
		System.out.println("\tAsuntolaina: " + saapuneetHouse + "\n\tOpintolaina: " + saapuneetStudent);
		
		System.out.println("\nPalvelupisteiden käyttöasteiden jakaumat:");
		System.out.println("\tPäivittäisasioiden respa: " + palvelupisteet[0].getUtilization() + " %");
		System.out.println("\tLaina-asioiden respa: " + palvelupisteet[1].getUtilization() + " %");
		System.out.println("\tLaskutus: " + palvelupisteet[2].getUtilization() + " %");
		System.out.println("\tNosto ja talletus: " + palvelupisteet[3].getUtilization() + " %");
		System.out.println("\tAsuntolaina: " + palvelupisteet[4].getUtilization() + " %");
		System.out.println("\tOpintolaina: " + palvelupisteet[5].getUtilization() + " %");
		
		System.out.println("\nPalvelematta jääneet asiakkaat palvelupisteittäin:");
		System.out.println("\tLaskutus: " + palvelupisteet[2].getPalvelemattomatAsiakkaat() + "\n\tNosto ja talletus: " + palvelupisteet[3].getPalvelemattomatAsiakkaat());
		System.out.println("\tAsuntolaina: " + palvelupisteet[4].getPalvelemattomatAsiakkaat() + "\n\tOpintolaina: " + palvelupisteet[5].getPalvelemattomatAsiakkaat());
		
		System.out.println("\nSimulointi päättyi kello " + Kello.getInstance().getAika() + "\n");
	}

}
