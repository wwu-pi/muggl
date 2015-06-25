package test.problematic;

/*
 * In the valid test case, the output array equals the input array whereas it should be different.
 */

public class Blur {
	public static int[] simpleBlur(int[] picture) throws IllegalArgumentException{
		
		// Abbrechen, falls Bild leer.
		
		if (picture == null){throw new IllegalArgumentException("Das Bild ist leer");};
		
		int[] ausgabe = picture;
		
		double[] uebergang = new double[ausgabe.length];
		
		// Groesse des Bildes bestimmen und abbrechen, falls kleiner als 4x4
		
		int groesse = ausgabe.length;
		if (groesse < 4){throw new IllegalArgumentException("Das Bild ist zu klein!");};
		
		// Seitenlaenge des Bildes bestimmen und abbrechen, falls nicht quadratisch
		
		int laenge = 1;
		
		for(int i = 0; i<groesse; i++){
			laenge = i * i;
			if (laenge == groesse){
				laenge = i;
				i = (groesse);
			}
			if (laenge > groesse){
				throw new IllegalArgumentException("Das Bild ist nicht quadratisch!");
			}
		}
				
		// Matrix erstellen. Erster Index: Vertikal. Zweiter Index: Horizontal.
		
		double[][] Matrix = new double[laenge][laenge];
	
		int aktuelleReihe = 0;
		for(int i = 0; i<laenge; i++){
			for(int q = 0; q<laenge; q++){
				Matrix[i][q] = ausgabe[q+aktuelleReihe];
			}
			aktuelleReihe = aktuelleReihe + laenge;
		}
	
		// Pixel verändern
		
		aktuelleReihe = 0;
		
		double selbst = 0;
		double links = 0;
		double rechts = 0;
		double oben = 0;
		double unten = 0;
		
		for(int i = 0; i<laenge; i++){
			for(int q = 0; q<laenge; q++){
				
				selbst = (Matrix[i][q])*(0.5);
	
				if (i != 0){oben = (Matrix[i-1][q])*(0.125);}
				else oben = (Matrix[i][q])*(0.125);
				
				if (q != 0){links = (Matrix[i][q-1])*(0.125);}
				else links = (Matrix[i][q])*(0.125);
				
				if (i < (laenge - 1)){unten = (Matrix[i+1][q])*(0.125);}
				else unten = (Matrix[i][q])*(0.125);
				
				if (q < (laenge - 1)){rechts = (Matrix[i][q+1])*(0.125);}
				else rechts = (Matrix[i][q])*(0.125);
						
				uebergang[q+aktuelleReihe] = (selbst + links + rechts + oben + unten);
			}
			aktuelleReihe = aktuelleReihe + laenge;
		}

		// Pixelwerte bei 0,5 aufrunden:
		
		double aktuell = 0;
		
		for(int i = 0; i < uebergang.length; i++){
			aktuell = uebergang[i];
			aktuell = aktuell * 10;
			aktuell = aktuell % 10;
			if (aktuell >= 5.0){
				uebergang[i] = uebergang[i] + 1;
			}
			ausgabe[i] = (int) uebergang[i];
		}
		
		// Ausgeben:
		
		return ausgabe;
	}
	
}
