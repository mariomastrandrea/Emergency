package model;

import java.time.LocalTime;

public class Event implements Comparable<Event>
{
	public enum EventType 
	{
		ARRIVAL,		// arriva un nuovo paziente
		FINISHED_TRIAGE, // è finito il triage, entro in sala d'attesa
		TIMEOUT, 		// passa un certo tempo in sala d'attesa
		FREE_STUDIO,	// si è liberato uno studio, chiamiamo qualcun altro
		TREATED			// paziente curato
	}
	
	private LocalTime time;
	private EventType type;
	private Patient patient;
	
	
	public Event(LocalTime time, EventType type, Patient patient)
	{
		this.time = time;
		this.type = type;
		this.patient = patient;
	}

	public LocalTime getTime()
	{
		return this.time;
	}

	public EventType getType()
	{
		return this.type;
	}

	public Patient getPatient()
	{
		return this.patient;
	}

	@Override
	public int compareTo(Event other)
	{
		return this.time.compareTo(other.time);
	}
	
	@Override
	public String toString()
	{
		String result;
		
		if(this.patient != null)
			result = String.format("At %s: %-15s => %-47s", this.time, this.type, this.patient);
		else
			result = String.format("At %s: %-15s => %-47s", this.time, this.type, "no queue");
		
		return result;
	}
	
}
