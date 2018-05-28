package thymeClient;

public class Led {
	private String name;
	private String power;
	private String color;
	private int term;
	private int illuminance;
	
	/* constructor */
	public Led(String name, String power, String color, int term, int illuminance)
	{
		this.name = name;
		this.power = power;
		this.color = color;
		this.term = term;
		this.illuminance = illuminance;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setPower(String power)
	{
		this.power = power;
	}
	
	public void setColor(String color)
	{
		this.color = color;
	}
	
	public void setTerm(int term)
	{
		this.term = term;
	}
	
	public void setIlluminance(int illuminance)
	{
		this.illuminance = illuminance;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getPower()
	{
		return this.power;
	}
	
	public String getColor()
	{
		return this.color;
	}
	
	public int getTerm()
	{
		return this.term;
	}
	
	public int getIlluminance()
	{
		return this.illuminance;
	}
	
	public String toString()
	{
		return "myName : " + this.name + ", myPower : " + this.power + ", myColor : " + this.color + ", myTerm : " + this.term + ", myIlluminance : " + this.illuminance;
	}
}
