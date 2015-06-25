package test.papers.bahn;


/**
 * Deutsche Bahn price calculation.
 * 
 * @author Tim Majchrzak
 */
@SuppressWarnings("all")
public class BahnSimple {
	// Constants.
	private final static double basePriceRegional = 0.145;
	private final static double basePriceIC = 0.184;
	private final static double basePriceICE = 0.231;
	
	/**
	 * Inner class representation for the travel price.
	 * @author Tim Majchrzak
	 */
	private class TravelPrice {
		private String name;
		private double price;
		
		public TravelPrice(String name, double price) {
			this.name = name;
			this.price = price;
		}
		
		public String getName() {
			return this.name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public double getPrice() {
			return this.price;
		}
		
		public void setPrice(double price) {
			this.price = price;
		}
	}
	
	/**
	 * train:
	 * 0	Reginonal
	 * 1	IC
	 * 2	ICE
	 * 
	 * bahnCard:
	 * 0	noBC
	 * 25	25
	 * 50	50
	 * 250	25 first
	 * 500	50 first
	 * 
	 * @param classNo
	 * @param distance
	 * @param bahnCard
	 * @param weekendIncluded
	 * @param returnTicket
	 * @param persons
	 * @param duration
	 * @param train
	 * @param flexible
	 * @param corporate
	 * @param daysBeforeTravel
	 * @param insideNRW
	 * @param startTime
	 * @param departureFrom
	 * @param destination
	 * @param days
	 * @return The TravelPrice.
	 */
	public double calculateTravelPriceValue (
			int classNo,
			int distance,
			int bahnCard,
			boolean weekendIncluded,
			boolean returnTicket,
			int persons,
			int duration,
			int train,
			boolean flexible,
			boolean corporate,
			int daysBeforeTravel,
			boolean insideNRW,
			int startTime,
			String departureFrom,
			String destination,
			int days) throws IllegalArgumentException {
		// Checking preconditions.
		if (classNo != 1 && classNo != 2)
			throw new IllegalArgumentException("Choose first or second class.");
		if (distance < 1)
			throw new IllegalArgumentException("Distance must be a positive integer.");
		if (bahnCard != 0 && bahnCard != 25 && bahnCard != 50 && bahnCard != 250 && bahnCard != 500)
			throw new IllegalArgumentException("");
		if (persons < 1)
			throw new IllegalArgumentException("Persons must be a positive integer.");
		if (duration < 1)
			throw new IllegalArgumentException("Duration must be a positive integer.");
		if (train != 0 && train != 1 && train != 2)
			throw new IllegalArgumentException("Train must be 0, 1 or 2.");
		if (daysBeforeTravel < 0)
			throw new IllegalArgumentException("Dys before travel must be zero or a positive integer.");
		if (startTime < 1)
			throw new IllegalArgumentException("Start time must be a positive integer.");
		if (days < 0)
			throw new IllegalArgumentException("Days must be zero or a positive integer.");

		// Main algorithm.
		TravelPrice[] tpList = new TravelPrice[6];
		if (train == 0 && classNo == 2) {
			if (weekendIncluded && days == 1) tpList[0] = weekendPrice(persons);
			if (insideNRW && days == 1 && (weekendIncluded || startTime >= 9)) tpList[1] = schoenerTagNRW(persons);
			if (insideNRW && days == 1 && duration <= 120) tpList[2] = schoeneFahrtNRW(persons);
		}
		if (daysBeforeTravel >= 3 && !flexible && classNo == 2) {
			tpList[3] = new TravelPrice("DauerSpezial", dauerSpezial(persons, returnTicket));
		}
		TravelPrice normalPriceTp = normalPrice(classNo, distance, returnTicket, persons, train);
		normalPriceTp = bahnCardReduction(normalPriceTp, classNo, bahnCard);
		normalPriceTp = corporateReduction(normalPriceTp, corporate);
		tpList[4] = normalPriceTp;
		
		if (classNo == 2 && train >= 1 && days == 1 && departureFrom == "Hamburg" && destination == "Bremen") { 
			tpList[5] = new TravelPrice("SpecialOfferHH2HB", 19.0);
		}
		
		// Determine the cheapest price and return that connection.
		TravelPrice cheapest = null;
		for (int a = 0; a < tpList.length; a++)
		{
			if (cheapest == null || (tpList[a] != null && cheapest.getPrice() > tpList[a].getPrice()))
				cheapest = tpList[a];
		}
		return cheapest.getPrice();
	}
	
	/**
	 * train:
	 * 0	Reginonal
	 * 1	IC
	 * 2	ICE
	 * 
	 * bahnCard:
	 * 0	noBC
	 * 25	25
	 * 50	50
	 * 250	25 first
	 * 500	50 first
	 * 
	 * @param classNo
	 * @param distance
	 * @param bahnCard
	 * @param weekendIncluded
	 * @param returnTicket
	 * @param persons
	 * @param duration
	 * @param train
	 * @param flexible
	 * @param corporate
	 * @param daysBeforeTravel
	 * @param insideNRW
	 * @param startTime
	 * @param departureFrom
	 * @param destination
	 * @param days
	 * @return The TravelPrice.
	 */
	public TravelPrice calculateTravelPrice (
			int classNo,
			int distance,
			int bahnCard,
			boolean weekendIncluded,
			boolean returnTicket,
			int persons,
			int duration,
			int train,
			boolean flexible,
			boolean corporate,
			int daysBeforeTravel,
			boolean insideNRW,
			int startTime,
			String departureFrom,
			String destination,
			int days) {
		TravelPrice[] tpList = new TravelPrice[6];
		if (train == 0 && classNo == 2) {
			if (weekendIncluded && days == 1) tpList[0] = weekendPrice(persons);
			if (insideNRW && days == 1 && (weekendIncluded || startTime >= 9)) tpList[1] = schoenerTagNRW(persons);
			if (insideNRW && days == 1 && duration <= 120) tpList[2] = schoeneFahrtNRW(persons);
		}
		if (daysBeforeTravel >= 3 && !flexible && classNo == 2) {
			tpList[3] = new TravelPrice("DauerSpezial", dauerSpezial(persons, returnTicket));
		}
		TravelPrice normalPriceTp = normalPrice(classNo, distance, returnTicket, persons, train);
		normalPriceTp = bahnCardReduction(normalPriceTp, classNo, bahnCard);
		normalPriceTp = corporateReduction(normalPriceTp, corporate);
		tpList[4] = normalPriceTp;
		
		if (classNo == 2 && train >= 1 && days == 1 && departureFrom == "Hamburg" && destination == "Bremen") { 
			tpList[5] = new TravelPrice("SpecialOfferHH2HB", 19.0);
		}
		
		// Determine the cheapest price and return that connection.
		TravelPrice cheapest = null;
		for (int a = 0; a < tpList.length; a++)
		{
			if (cheapest == null || (tpList[a] != null && cheapest.getPrice() > tpList[a].getPrice()))
				cheapest = tpList[a];
		}
		return cheapest;
	}
	
	/**
	 * Calculate the normal traveling price.
	 * @param classNo
	 * @param distance
	 * @param returnTicket
	 * @param persons
	 * @param train
	 * @return
	 */
	private TravelPrice normalPrice(int classNo, int distance, boolean returnTicket, int persons, int train) {
		if (returnTicket) distance *= 2;
		
		double classNo2Price = 0.0;
		if (train == 0) classNo2Price = distance * basePriceRegional * persons;
		else if (train == 1) classNo2Price = distance * basePriceIC * persons;
		else if (train == 2) classNo2Price = distance * basePriceICE * persons;
		
		if (classNo == 2) {
			return new TravelPrice("Normalpreis, 2.Klasse", classNo2Price);
		}
		return new TravelPrice("Normalpreis, 1.Klasse", classNo2Price * 1.6);
	}
	
	/**
	 * Calculate the reduction if a BahnCard is available.
	 * @param tp
	 * @param classNo
	 * @param bahnCard
	 * @return
	 */
	private TravelPrice bahnCardReduction(TravelPrice tp, int classNo, int bahnCard) {
		if (classNo == 2 && bahnCard == 25) {
			tp.setName(tp.getName() + " mit BC25");
			tp.setPrice(tp.getPrice() * 0.75);
		} else if (classNo == 2 && bahnCard == 50) {
			tp.setName(tp.getName() + " mit BC50");
			tp.setPrice(tp.getPrice() * 0.50);
		} else if (bahnCard == 250) {
			tp.setName(tp.getName() + " mit BC50first");
			tp.setPrice(tp.getPrice() * 0.75);
		} else if (bahnCard == 500) {
			tp.setName(tp.getName() + " mit BC50first");
			tp.setPrice(tp.getPrice() * 0.50);
		}
		
		return tp;
	}
	
	/**
	 * Calculate a corporate reduction.
	 * @param tp
	 * @param corporate
	 * @return
	 */
	private TravelPrice corporateReduction(TravelPrice tp, boolean corporate) {
		if (corporate) {
			tp.setName(tp.getName() + " mit Corporate Reduction");
			tp.setPrice(tp.getPrice() * 0.91);
		}
		return tp;
	}
	
	/**
	 * 
	 * @param tp
	 * @param daysBeforeTravel
	 * @param returnTicket
	 * @param flexible
	 * @param classNo
	 * @return
	 */
	/*
	private TravelPrice save25Reduction(TravelPrice tp, int daysBeforeTravel, boolean returnTicket, boolean flexible, int classNo) {
		if (daysBeforeTravel >= 3 && returnTicket && !flexible && (classNo == 2 && tp.getPrice() >= 38.0) || (classNo == 1 && tp.getPrice() >= 57.0)) {
			tp.setName("Sparpreis25");
			tp.setPrice(tp.getPrice() * 0.75);
		}
		return tp;
	}*/
	
	/**
	 * 
	 * @param tp
	 * @param daysBeforeTravel
	 * @param returnTicket
	 * @param flexible
	 * @param classNo
	 * @return
	 */
	/*
	private TravelPrice save50Reduction(TravelPrice tp, int daysBeforeTravel, boolean returnTicket, boolean flexible, int classNo) {
		if (daysBeforeTravel >= 3 && returnTicket && !flexible && (classNo == 2 && tp.getPrice() >= 38.0) || (classNo == 1 && tp.getPrice() >= 57.0)) {
			tp.setName("Sparpreis50");
			tp.setPrice(tp.getPrice() * 0.5);
		}
		return tp;
	}*/
	
	/**
	 * Calculate the weekend price.
	 * @param persons
	 * @return
	 */
	private TravelPrice weekendPrice(int persons) {
		return new TravelPrice("SchoenesWochenende", 35.0 * (((persons - 1) / 5) + 1));
	}
	
	/**
	 * Calculate the "Schoener Tag NRW" price.
	 * @param persons
	 * @return
	 */
	private TravelPrice schoenerTagNRW(int persons) {
		if (persons == 1) {
			return new TravelPrice("SchoenerTagNRW", 23.50);
		}
		if (persons > 5) {
			TravelPrice tp = schoenerTagNRW(persons - 5);
			return new TravelPrice("SchoenerTagNRW", 33.0 + tp.getPrice());
		}
		return new TravelPrice("SchoenerTagNRW", 33.0);
	}
	
	/**
	 * Calculate the "Schoene Fahrt NRW" price.
	 * @param persons
	 * @return
	 */
	private TravelPrice schoeneFahrtNRW(int persons) {
		return new TravelPrice("SchoeneFahrtNRW", persons * 15.20);
	}
	
	/**
	 * Calculate the "dauer spezial" price.
	 * @param persons
	 * @param returnTicket
	 * @return
	 */
	private double dauerSpezial(int persons, boolean returnTicket) {
		double price = dauerSpezialOneWay(persons);
		if (returnTicket) price *= 2.0;
		return price;
	}
	
	/**
	 * Calculate the "dauer spezial" price for one way.
	 * @param persons
	 * @return
	 */
	private double dauerSpezialOneWay(int persons) {
		if (persons < 0) return 0.0; // Problem, will lead to endless looping.
		if (persons > 5) {
			return 29.0 + 4*20.0 + dauerSpezialOneWay(persons - 5);
		}
		return 29.0 + (persons - 1) * 20.0; 
	}
	
	/**
	 * Test the price calculation with a couple of examples
	 * @param args
	 */
	public static void main(String[] args) {
		BahnSimple bahn = new BahnSimple();
		TravelPrice tp1 = bahn.calculateTravelPrice(2, 100, 25, true, true, 3, 119, 1, true, true, 5, true, 10, "Muenster", "Koeln", 1);
		TravelPrice tp2 = bahn.calculateTravelPrice(2, 100, 0, true, true, 3, 119, 1, true, true, 5, true, 10, "Muenster", "Koeln", 1);
		TravelPrice tp3 = bahn.calculateTravelPrice(2, 100, 0, true, true, 3, 119, 1, true, false, 5, true, 10, "Muenster", "Koeln", 1);
		TravelPrice tp4 = bahn.calculateTravelPrice(2, 100, 0, true, true, 3, 119, 0, false, false, 5, true, 10, "Muenster", "Koeln", 1);
		TravelPrice tp5 = bahn.calculateTravelPrice(1, 100, 0, true, true, 3, 119, 0, false, false, 5, true, 10, "Muenster", "Koeln", 1);
		TravelPrice tp6 = bahn.calculateTravelPrice(2, 100, 25, true, true, 1, 119, 0, false, true, 5, true, 10, "Muenster", "Koeln", 1);
		TravelPrice tp7 = bahn.calculateTravelPrice(2, 100, 25, true, true, 1, 119, 1, false, true, 5, true, 10, "Muenster", "Koeln", 1);
		TravelPrice tp8 = bahn.calculateTravelPrice(2, 100, 25, true, true, 1, 119, 0, false, true, 5, false, 10, "Hamburg", "Bremen", 1);
		TravelPrice tp9 = bahn.calculateTravelPrice(2, 100, 25, true, true, 1, 119, 1, false, true, 5, false, 10, "Hamburg", "Bremen", 1);
		TravelPrice tp10 = bahn.calculateTravelPrice(2, 150, 25, true, true, 1, 119, 1, false, true, 5, false, 10, "Muenster", "Koeln", 1);
		TravelPrice tp11 = bahn.calculateTravelPrice(2, 300, 25, true, true, 1, 119, 1, false, true, 5, false, 10, "Muenster", "Frankfurt", 1);
		System.out.println(tp1.getName() + ": " + tp1.getPrice());
		System.out.println(tp2.getName() + ": " + tp2.getPrice());
		System.out.println(tp3.getName() + ": " + tp3.getPrice());
		System.out.println(tp4.getName() + ": " + tp4.getPrice());
		System.out.println(tp5.getName() + ": " + tp5.getPrice());
		System.out.println(tp6.getName() + ": " + tp6.getPrice());
		System.out.println(tp7.getName() + ": " + tp7.getPrice());
		System.out.println(tp8.getName() + ": " + tp8.getPrice());
		System.out.println(tp9.getName() + ": " + tp9.getPrice());
		System.out.println(tp10.getName() + ": " + tp10.getPrice());
		System.out.println(tp11.getName() + ": " + tp11.getPrice());
	}
	
}
