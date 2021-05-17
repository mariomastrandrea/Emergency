package model;

public class TestSimulator 
{
	public static void main(String[] args) 
	{
		Simulator s = new Simulator();
		
		//setting parameters
		// .......
		
		//run
		s.init();
		s.run();
		
		//print results
		int numTreated = s.getPatientsTreated();
		int numAbandoned = s.getPatientsAbandoned();
		int numDeads = s.getPatientsDead();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Treated patients: #").append(numTreated).append("\n");
		sb.append("Abandoned patients: #").append(numAbandoned).append("\n");
		sb.append("Dead patients: #").append(numDeads);

		System.out.println(sb.toString());
	}
}
