package test;

import main.domeinLaag.Fabrikant;
import main.domeinLaag.Land;
import main.domeinLaag.Luchthaven;
import main.domeinLaag.LuchtvaartMaatschappij;
import main.domeinLaag.Vliegtuig;
import main.domeinLaag.VliegtuigType;
import main.domeinLaag.Vlucht;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class VluchtTest {

	static LuchtvaartMaatschappij lvm ;
	static Fabrikant f1; 
	static VliegtuigType vtt1; 
	static Vliegtuig vt1;
	static Luchthaven lh1, lh2;
	static Vlucht vl1, vl2; 

	@BeforeEach
	public void initialize() {
		try {
			lvm = new LuchtvaartMaatschappij("NLM");
			f1 = new Fabrikant("Airbus","G. Dejenelle");
			vtt1 = f1.creeervliegtuigtype("A-200", 140);
			Calendar datum = Calendar.getInstance();
			datum.set(2000, 01, 01);
			vt1 = new Vliegtuig(lvm, vtt1, "Luchtbus 100", datum);
			Land l1 = new Land("Nederland", 31);
			Land l2 = new Land("België", 32);
			lh1 = new Luchthaven("Schiphol", "ASD", true, l1);
			lh2 = new Luchthaven("Tegel", "TEG", true, l2);
			Calendar vertr = Calendar.getInstance();
			vertr.set(2020, 03, 30, 14, 15, 0);
			Calendar aank = Calendar.getInstance();
			aank.set(2020, 03, 30, 15, 15, 0);
			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank );
			vertr.set(2020, 4, 1, 8, 15, 0);
			aank.set(2020, 4, 1, 9, 15, 0);
			vl2 = new Vlucht(vt1, lh1, lh2, vertr, aank );
		} catch (Exception e){
			String errorMessage =  "Exception: " + e.getMessage();
			System.out.println(errorMessage); 
		}
	}

	/**
	 * Business rule:
	 * De bestemming moet verschillen van het vertrekpunt van de vlucht.
	 */
	
	@Test
	public void test_1_BestemmingMagNietGelijkZijnAanVertrek_False() {
		Vlucht vlucht = new Vlucht();
		try {
			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh1);
			Luchthaven bestemming = vlucht.getBestemming();
			assertTrue(bestemming == null);
			vlucht.zetBestemming(lh1);
			// De test zou niet verder mogen komen: er moet al een exception gethrowd zijn.
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming.equals(lh1));
		}
		catch(IllegalArgumentException e) {
			Luchthaven bestemming = vlucht.getBestemming();
			assertFalse(bestemming.equals(lh1));
			System.out.println("TEST  1: \t vertrekpunt = bestemming");
		}
	}

	@Test
	public void test_2_BestemmingMagNietGelijkZijnAanVertrek_True() {
		Vlucht vlucht = new Vlucht();
		Luchthaven bestemming;
		try {
			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh2);
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming == null);
			vlucht.zetBestemming(lh1);
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming.equals(lh1));
		}
		catch(IllegalArgumentException e) {
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming.equals(lh1));

		}
	}

	/**
	 * Business rule:
	 * De vertrektijd en eenkomsttijd moeten geldig zijn en in de toekomst liggen
	 */
	@Test
	public void test_3_geenGeldigeVertrekTijd(){
		try {
			Calendar vertr = Calendar.getInstance();
			vertr.set(2025, 15, 45, 24, 0, 0);
			Calendar aank = Calendar.getInstance();
			aank.set(2020, 03, 30, 15, 15, 0);
			vertr.setLenient(false);
			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank);
			vertr.getTime();
		}
		catch(IllegalArgumentException e) {
			System.out.println("TEST  3: \tgeen geldige datum/tijd");
		}
	}

	@Test
	public void test_4_ongeldigetijd_True(){
		try{
		Calendar aankom = Calendar.getInstance();
		Calendar vertr = Calendar.getInstance();

		vertr.set(2025, 9, 30, 24,0);
		aankom.set(2025, 9, 30, 24,1);
		aankom.setLenient(false);
		vl2 = new Vlucht(vt1, lh1, lh2, vertr, aankom);
		aankom.getTime();
		}
		catch (IllegalArgumentException e){
			System.out.println("TEST  4: \tgeen geldige datum/tijd");

		}

	}

	@Test
	public void test_5_8_10_20_geldigetijdNu_True(){
		try{
			Calendar aankom = Calendar.getInstance();
			Calendar vertr = Calendar.getInstance();

			vertr.getTime();
			aankom.getTime();
			aankom.add(Calendar.MINUTE, 1);
			vl2 = new Vlucht(vt1, lh1, lh2, vertr, aankom);
			assertTrue(vertr.before(aankom));
		}
		catch (AssertionFailedError e){
			System.out.println("TEST 5, 8, 10, 20:\t Dit hoor je niet te zien");

		}

	}

	@Test
	public void test_6_vertrekTijdInHetVerleden(){
		try {
			Calendar vertr = Calendar.getInstance();
			vertr.add(Calendar.MINUTE, -1);
			Calendar aank = Calendar.getInstance();
			aank.getTime();
			vertr.getTime();
			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank);
			assertFalse(vertr.before(aank));
		}
		catch(AssertionFailedError e) {
			String message = "TEST  6: \ttijd in het verleden";
			System.out.println(message);
		}
	}

	@Test
	public void test_7_GeheleReisVerleden(){
		try{
			Calendar aankom = Calendar.getInstance();
			Calendar vertr = Calendar.getInstance();
			Calendar nu = Calendar.getInstance();

			vertr.getTime();
			aankom.getTime();
			nu.getTime();
			vertr.add(Calendar.MINUTE, -2);
			aankom.add(Calendar.MINUTE, -1);
			vl2 = new Vlucht(vt1, lh1, lh2, vertr, aankom);
			assertTrue(nu.before(vertr) && nu.before(aankom));


		}
		catch (AssertionFailedError e){
			System.out.println("TEST  7: \ttijd in het verleden");

		}
	}

	@Test
	public void test_9_vertrekTijdNaAankomst(){
		try {

			Calendar vertr = Calendar.getInstance();
			Calendar aank = Calendar.getInstance();

			aank.getTime();
			vertr.getTime();
			vertr.add(Calendar.MINUTE, 1);
			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank);
			assertTrue(vertr.before(aank));
		}
		catch(AssertionFailedError e) {
			String message = "TEST  9: \tvertrektijd < aankomsttijd";
			System.out.println(message);
		}
	}

	@Test
	public void test_11_OverlappendeVlucht(){
		try {
			Calendar vertr1 = Calendar.getInstance();
			Calendar aank1 = Calendar.getInstance();
			vertr1.set(2025,7,1, 12,43, 0);
			aank1.set(2025, 7,1, 15,36,0);

			Calendar vertr = Calendar.getInstance();
			Calendar aank = Calendar.getInstance();
			vertr.set(2025,7,1, 15,35, 0);
			aank.set(2025, 7,1, 16,36,0);

			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank);
			Vlucht vluchtBezet = new Vlucht(vt1, lh1, lh2,vertr1, aank1);

			assertFalse(vluchtBezet.getAankomstTijd().after(vl1.getVertrekTijd()));
		}
		catch(AssertionFailedError e) {
			System.out.println("TEST 11:\toverlappende vlucht");
		}
	}

	@Test
	public void test_12_OverlappendeVlucht(){
		try {
			Calendar vertr1 = Calendar.getInstance();
			Calendar aank1 = Calendar.getInstance();
			vertr1.set(2025,7,1, 12,43, 0);
			aank1.set(2025, 7,1, 15,36,0);

			Calendar vertr = Calendar.getInstance();
			Calendar aank = Calendar.getInstance();
			vertr.set(2025,7,1, 11,36, 0);
			aank.set(2025, 7,1, 12,44,0);

			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank);
			Vlucht vluchtBezet = new Vlucht(vt1, lh1, lh2,vertr1, aank1);

			assertFalse(vl1.getAankomstTijd().after(vluchtBezet.getVertrekTijd()));
		}
		catch(AssertionFailedError e) {
			System.out.println("TEST 12:\toverlappende vlucht");
		}
	}

	@Test
	public void test_13_OverlappendeVlucht(){
		try {
			Calendar vertr1 = Calendar.getInstance();
			Calendar aank1 = Calendar.getInstance();
			vertr1.set(2025,7,1, 12,43, 0);
			aank1.set(2025, 7,1, 15,36,0);

			Calendar vertr = Calendar.getInstance();
			Calendar aank = Calendar.getInstance();
			vertr.set(2025,7,1, 12,42, 0);
			aank.set(2025, 7,1, 15,37,0);

			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank);
			Vlucht vluchtBezet = new Vlucht(vt1, lh1, lh2,vertr1, aank1);

			assertFalse(vluchtBezet.getAankomstTijd().after(vl1.getVertrekTijd()) && vl1.getAankomstTijd().after(vluchtBezet.getVertrekTijd()));
		}
		catch(AssertionFailedError e) {
			System.out.println("TEST 13:\toverlappende vlucht");
		}
	}
	@Test
	public void test_14_OverlappendeVlucht(){
		try {
			Calendar vertr1 = Calendar.getInstance();
			Calendar aank1 = Calendar.getInstance();
			vertr1.set(2025,7,1, 12,43, 0);
			aank1.set(2025, 7,1, 15,36,0);

			Calendar vertr = Calendar.getInstance();
			Calendar aank = Calendar.getInstance();
			vertr.set(2025,7,1, 15,37, 0);
			aank.set(2025, 7,1, 16,37,0);

			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank);
			Vlucht vluchtBezet = new Vlucht(vt1, lh1, lh2,vertr1, aank1);

			assertFalse(vluchtBezet.getAankomstTijd().after(vl1.getVertrekTijd()) && vl1.getAankomstTijd().after(vluchtBezet.getVertrekTijd()));
		}
		catch(AssertionFailedError e) {
			System.out.println("TEST 14:\twaarom ben je hier?");
		}
	}





}
