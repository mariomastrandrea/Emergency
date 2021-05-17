package model;

import java.time.Duration;
import java.time.LocalTime;

public class Patient implements Comparable<Patient>
{
	public static final Duration WHITE_TIMEOUT = Duration.ofMinutes(90);
	public static final Duration YELLOW_TIMEOUT = Duration.ofMinutes(40);
	public static final Duration RED_TIMEOUT = Duration.ofMinutes(15);

	public static final Duration WHITE_TREATMENT = Duration.ofMinutes(10);
	public static final Duration YELLOW_TREATMENT = Duration.ofMinutes(20);
	public static final Duration RED_TREATMENT = Duration.ofMinutes(45);

	private static int nextNum = 1;
	
	public enum ColorCode 
	{
		WHITE(WHITE_TIMEOUT, WHITE_TREATMENT), 
		YELLOW(YELLOW_TIMEOUT, YELLOW_TREATMENT), 
		RED(RED_TIMEOUT, RED_TREATMENT), 
		BLACK(Duration.ZERO, Duration.ZERO);
		
		private Duration timeout;
		private Duration treatment;
		
		private ColorCode(Duration timeout, Duration treatment)
		{
			this.timeout = timeout;
			this.treatment = treatment;
		}
		
		public Duration getTimeout() { return this.timeout; }
		public Duration getTreatment() { return this.treatment; }
	}
	
	public enum State
	{
		NEW,
		WAITING,
		TREATING,
		OUT,
		DEAD
	}
	
	private final int ID;
	private final LocalTime arrivalTime;
	private ColorCode color;
	private State state;
	
	
	public Patient(LocalTime arrivalTime, State state)
	{
		this.ID = nextNum++;
		this.arrivalTime = arrivalTime;
		this.state = state;
	}
	
	public ColorCode getColor()
	{
		return this.color;
	}
	
	public void setColor(ColorCode color) 
	{
		this.color = color;
	}
	
	public LocalTime getArrivalTime()
	{
		return this.arrivalTime;
	}
	
	public State getState()
	{
		return this.state;
	}
	
	public void setState(State newState)
	{
		this.state = newState;
	}
	
	@Override
	public String toString()
	{
		String result = "Patient #" + this.ID + ": state -> " + this.state;
			
		if(this.color != null)
			result += "; color -> " + this.color;
	
		return result;
	}

	@Override
	public int compareTo(Patient other)
	{
		if(this.color != other.color)
			return other.color.compareTo(this.color);
		
		//equal colours
		return this.arrivalTime.compareTo(other.arrivalTime);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (ID != other.ID)
			return false;
		return true;
	}
	
}
