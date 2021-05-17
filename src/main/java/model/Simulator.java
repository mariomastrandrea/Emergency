package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import model.Event.EventType;
import model.Patient.ColorCode;
import model.Patient.State;

public class Simulator 
{
	// Coda degli eventi
	private PriorityQueue<Event> eventsQueue;
	
	// Modello del mondo
	Collection<Patient> allPatients;
	PriorityQueue<Patient> waitingRoom;
	private int freeStudios;
	
	// Parametri in input
	private int totStudios = 3;	 //default
	private int totPatients = 30;
	private Duration arrivalTime = Duration.ofMinutes(5);  //default
	private Duration triageDuration = Duration.ofMinutes(5);  //default
	
	private LocalTime startTime = LocalTime.of(8, 00);  //default
	private LocalTime endTime = LocalTime.of(20, 00);  //default
	
	// Parametri in output
	private int patientsTreated;
	private int patientsAbandoned;
	private int patientsDead;
		
	
	// inizializza il simulatore e crea gli eventi iniziali
	public void init() 
	{
		this.eventsQueue = new PriorityQueue<>();
		this.allPatients = new ArrayList<>();
		this.waitingRoom = new PriorityQueue<>();
		this.freeStudios = this.totStudios;
		
		this.patientsTreated = 0;
		this.patientsAbandoned = 0;
		this.patientsDead = 0;
		
		this.patientsCount = 0;
		
		//inietto gli eventi di input
		LocalTime actualTime = this.startTime;
		int insertedPatients = 0;
		
		while(actualTime.isBefore(this.endTime) 
				&& insertedPatients < this.totPatients)
		{
			Patient newPatient = new Patient(actualTime, Patient.State.NEW);
			Event arrivalEvent = new Event(actualTime, EventType.ARRIVAL, newPatient);
			
			this.eventsQueue.add(arrivalEvent);
			this.allPatients.add(newPatient);
			
			insertedPatients++;
			actualTime = actualTime.plus(this.arrivalTime);
		}
	}

	// ESEGUE LA SIMULAZIONE
	public void run() 
	{
		System.out.println("Simulation:\n");
		
		System.out.println(String.format("%-9s %-15s %2s %-47s | #queue  #freeStudios",
				"    TIME", "EVENT TYPE", "", "PATIENT"));
		System.out.println("-".repeat(100));
		
		LocalTime t = null;
		while(!this.eventsQueue.isEmpty())
		{
			this.uselessTimeout = false;
			
			Event newEvent = this.eventsQueue.poll();
			this.processEvent(newEvent);
			
			//print
			this.printEvent(newEvent, uselessTimeout, t);
			t = newEvent.getTime();
		}
		
		System.out.println("_".repeat(100));
		System.out.println();
	}
	
	private boolean uselessTimeout;
	
	private void processEvent(Event e)
	{		
		Patient patient = e.getPatient();
		LocalTime time = e.getTime();

		Event newEvent;
		Event newTimeoutEvent;
		
		switch(e.getType())
		{
			case ARRIVAL:
				
				newEvent = new Event(time.plus(triageDuration), EventType.FINISHED_TRIAGE, patient);
				this.eventsQueue.add(newEvent);
				break;
				
			case FINISHED_TRIAGE:
				
				ColorCode nextColor = this.nextColorCode();
				patient.setColor(nextColor);
				patient.setState(State.WAITING);
				
				if(this.waitingRoom.size() == 0 && this.freeStudios > 0)	//there is only this patient...
				{
					//... he enters directly
					newEvent = new Event(time, EventType.FREE_STUDIO, patient); 
					this.eventsQueue.add(newEvent);
					break;
				}
				
				this.waitingRoom.add(patient); //new patient in waiting room

				Duration timeout = patient.getColor().getTimeout();
				newTimeoutEvent = new Event(time.plus(timeout), EventType.TIMEOUT, patient);
				this.eventsQueue.add(newTimeoutEvent);
			
				break;
				
			case TIMEOUT:
				
				// this patient was already treated
				if(patient.getState().compareTo(State.WAITING) > 0)
				{
					uselessTimeout = true;
					break;
				}
				
				switch(patient.getColor())
				{
					case WHITE:
						patient.setState(State.OUT);
						this.waitingRoom.remove(patient);
						
						this.patientsAbandoned++;
						break;
						
					case YELLOW:
						this.waitingRoom.remove(patient);
						patient.setColor(ColorCode.RED);
						this.waitingRoom.add(patient);
						
						Duration newTimeout = patient.getColor().getTimeout();
						newTimeoutEvent = new Event(time.plus(newTimeout), EventType.TIMEOUT, patient);
						
						this.eventsQueue.add(newTimeoutEvent);
						break;
						
					case RED:
						patient.setColor(ColorCode.BLACK);
						patient.setState(State.DEAD);
						this.waitingRoom.remove(patient);
						
						this.patientsDead++;
						break;
						
					default:
						System.out.println("Errore: TIMEOUT con colore " + patient.getColor());
				}
				break;
				
			case FREE_STUDIO: //enter a new patient
				
				if(this.freeStudios == 0)	// no enough available studios available
					break;			
				
				if(patient == null)
					break; //no one in waiting room
				
				this.waitingRoom.remove(patient);
				patient.setState(State.TREATING);
				
				Duration treatment = patient.getColor().getTreatment();
				newEvent = new Event(time.plus(treatment), EventType.TREATED, patient);
				this.eventsQueue.add(newEvent);
				
				this.freeStudios--;
				break;
				
			case TREATED:	//exit a patient
				this.patientsTreated++;
				patient.setState(State.OUT);
				
				this.freeStudios++;
				
				//Which patient has to be treated?
				Patient nextPatient = this.waitingRoom.peek();
				
				newEvent = new Event(time, EventType.FREE_STUDIO, nextPatient);
				this.eventsQueue.add(newEvent);
				
				break;		
		}
	}
	
	private int patientsCount;
	private Patient.ColorCode nextColorCode()
	{
		int index = this.patientsCount % 3;
		ColorCode next = ColorCode.values()[index];
		this.patientsCount ++;
		
		return next;
	}
	
	private void printEvent(Event newEvent, boolean uselessTimeout, LocalTime t)
	{
		if(uselessTimeout)
			return;
		
		if(!newEvent.getTime().equals(t) && t != null)
			System.out.println();
		
		System.out.print(newEvent);
		System.out.print(" |    ");
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3d",this.waitingRoom.size())).append(" ".repeat(3)).append(this.freeStudios);
		
		System.out.println(sb.toString());
	}

	public void setTotStudios(int totStudios) 
	{
		this.totStudios = totStudios;
	}

	public void setNumPatients(int numPatients) 
	{
		this.totPatients = numPatients;
	}

	public void setArrivalTime(Duration arrivalTime) 
	{
		this.arrivalTime = arrivalTime;
		
	}

	public void setStartTime(LocalTime startTime) 
	{
		this.startTime = startTime;
	}

	public void setEndTime(LocalTime endTime) 
	{
		this.endTime = endTime;
	}

	public int getPatientsTreated()
	{
		return this.patientsTreated;
	}

	public int getPatientsAbandoned()
	{
		return this.patientsAbandoned;
	}

	public int getPatientsDead()
	{
		return this.patientsDead;
	}
}
