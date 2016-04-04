package com.ev112.codeblack.simpleclient.unsorted;

import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TimezonesTest {

	private String[] tzs;
	private TimeZone tz_sthlm = TimeZone.getTimeZone("Europe/Stockholm");
	private TimeZone tz2 = TimeZone.getDefault();
	private TimeZone tz_ny = TimeZone.getTimeZone("America/New_York");
	
	@Before
	public final void setup() {
		tzs = TimeZone.getAvailableIDs();
		Arrays.sort(tzs);
	}
	
	@Test
	public final void TZTest1() {
		for (String tz:tzs) {
			System.out.println("TimeZone:" + tz + " Raw Offset:" + TimeZone.getTimeZone(tz).getRawOffset());
		}
	}

	@Test
	public final void CurrentDateTest() {
		Date date = new Date();
		System.out.println("Year:" + (date.getYear()+1900) + " Month:" + date.getMonth() + " Day:" + date.getDate());
	}
	
	@Test
	public final void DateTest() {
		Date date2 = new Date(1397730547365L);
		System.out.println("Year:" + (date2.getYear()+1900) + " Month:" + date2.getMonth() + " Day:" + date2.getDate() 
				+ " Hour:" + date2.getHours() + " Min:" + date2.getMinutes() + " Sec:" + date2.getSeconds() + " TZOFFSET:" + date2.getTimezoneOffset());
	}

	@Test
	public final void TimezoneTest() {
		// 
		// Correct way!
		// 
		DateTimeZone dtz = DateTimeZone.forID("America/New_York");
		DateTime dt = new DateTime(1397730547365L,dtz);
		
		System.out.println("JODA: Year:" + (dt.getYear()) + " Month:" + dt.getMonthOfYear() + " Day:" + dt.getDayOfMonth() 
				+ " Hour:" + dt.getHourOfDay() + " Min:" + dt.getMinuteOfHour() + " Sec:" + dt.getSecondOfMinute() + " TZOFFSET_IN_MS:" + dt.getZone().getOffset(dt));
	}
	
	@Test
	public final void somethingTest() {
		long time_diff_ms = tz_ny.getOffset(new Date().getTime()) - tz_sthlm.getOffset(new Date().getTime());   
		double time_diff_hours = ((double)time_diff_ms) / (1000.0 * 60.0 * 60.0); 
		
		System.out.println("Time diff between Stockholm and New York:" + time_diff_hours);
		
		time_diff_ms = tz_ny.getOffset(new Date().getTime()) - TimeZone.getDefault().getOffset(new Date().getTime());   
		time_diff_hours = ((double)time_diff_ms) / (1000.0 * 60.0 * 60.0); 
		
		System.out.println("Time diff between Default TZ and New York:" + time_diff_hours);
	}
	
	
	@Ignore
	public final void TimezonesTest() {
	
		System.out.println("Europe/Stockholm");
		
		System.out.println("TZ    :" + tz_sthlm.getDisplayName());
		System.out.println("DST   :" + tz_sthlm.getDSTSavings());
		System.out.println("OFS   :" + tz_sthlm.getOffset(new Date().getTime()));
		System.out.println("RAWOFS:" + tz_sthlm.getRawOffset());
		System.out.println("(secs):" + tz_sthlm.getOffset(new Date().getTime()) / 1000);
		System.out.println("(mins):" + tz_sthlm.getOffset(new Date().getTime()) / 1000 / 60);
		System.out.println("(hrs ):" + tz_sthlm.getOffset(new Date().getTime()) / 1000 / 60 / 60);
		
		System.out.println("Default");
		
		System.out.println("TZ    :" + tz2.getDisplayName());
		System.out.println("DST   :" + tz2.getDSTSavings());
		System.out.println("OFS   :" + tz2.getOffset(new Date().getTime())       );
		System.out.println("RAWOFS:" + tz2.getRawOffset());
		System.out.println("(secs):" + tz2.getOffset(new Date().getTime()) / 1000);
		System.out.println("(mins):" + tz2.getOffset(new Date().getTime()) / 1000 / 60);
		System.out.println("(hrs ):" + tz2.getOffset(new Date().getTime()) / 1000 / 60 / 60);
		
		
		System.out.println("America/New York");
		
		System.out.println("TZ    :" + tz_ny.getDisplayName());
		System.out.println("DST   :" + tz_ny.getDSTSavings());
		System.out.println("OFS   :" + tz_ny.getOffset(new Date().getTime()));
		System.out.println("RAWOFS:" + tz_ny.getRawOffset());
		System.out.println("(secs):" + tz_ny.getOffset(new Date().getTime()) / 1000);
		System.out.println("(mins):" + tz_ny.getOffset(new Date().getTime()) / 1000 / 60);
		System.out.println("(hrs ):" + tz_ny.getOffset(new Date().getTime()) / 1000 / 60 / 60);
	}
	
	public static void main(String[] args) {
		new TimezonesTest();
	}

}
