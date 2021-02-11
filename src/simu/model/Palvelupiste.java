package simu.model;

import java.text.DecimalFormat;
import java.util.LinkedList;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Kello;
import simu.framework.Tapahtuma;
import simu.framework.Tapahtumalista;

public class Palvelupiste {

	private LinkedList<Asiakas> jono = new LinkedList<Asiakas>(); // Tietorakennetoteutus

	private ContinuousGenerator generator;
	private Tapahtumalista tapahtumalista;
	private TapahtumanTyyppi skeduloitavanTapahtumanTyyppi;
	private int saapuneetAsiakkaat = 0;
	private int poistuneetAsiakkaat = 0;
	private double busyTime = 0;
	private boolean varattu = false;

	public Palvelupiste(ContinuousGenerator generator, Tapahtumalista tapahtumalista, TapahtumanTyyppi tyyppi) {
		this.tapahtumalista = tapahtumalista;
		this.generator = generator;
		this.skeduloitavanTapahtumanTyyppi = tyyppi;
	}

	public void lisaaJonoon(Asiakas a) { // Jonon 1. asiakas aina palvelussa
		saapuneetAsiakkaat++;
		jono.add(a);

	}

	public Asiakas otaJonosta() { // Poistetaan palvelussa ollut
		this.poistuneetAsiakkaat++;
		varattu = false;
		return jono.poll();
	}

	public void aloitaPalvelu() { // Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
		varattu = true;
		double palveluaika = generator.sample();
		busyTime += palveluaika;
		tapahtumalista.lisaa(new Tapahtuma(skeduloitavanTapahtumanTyyppi, Kello.getInstance().getAika() + palveluaika));
	}

	public boolean onVarattu() {
		return varattu;
	}

	public boolean onJonossa() {
		return jono.size() != 0;
	}

	public int getSaapuneetAsiakkaat() {
		return saapuneetAsiakkaat;
	}

	public int getPoistuneetAsiakkaat() {
		return poistuneetAsiakkaat;
	}

	public int getPalvelemattomatAsiakkaat() {
		return saapuneetAsiakkaat - poistuneetAsiakkaat;
	}

	public double getBusyTime() {
		return busyTime;
	}
	
	public String getUtilization() {
		DecimalFormat df = new DecimalFormat("#.00");
		double result = (getBusyTime() / Kello.getInstance().getAika() * 100.0);
		String formatted = df.format(result);
		return formatted;
	}

}
