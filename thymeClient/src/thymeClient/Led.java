package thymeClient;


public class Led {
	private String name;
	private String power;
	private int illuminance;
	
	public Led(String name, String power, int illuminance)
	{
		this.name = name;
		this.power = power;
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
	
	public int getIlluminance()
	{
		return this.illuminance;
	}
	
	public String toString()
	{
		return "myName : " + this.name + ", myPower : " + this.power + ", myIlluminance : " + this.illuminance;
	}
}
