import java.util.ArrayList;

import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;


public class Fish{

	private int ID = 0;
	private double birthday;
	private double age;
	private FishLifeState currentState;
	public double x;
	public double y;
	public double[] position = new double [2];
	private ArrayList<Rule> rules;
	private int fishID;
	private long currentTick;
	private Reach currentReach;
	private int counter = 0;
	private static Reach initialReach;

	public Fish(ArrayList<Rule> ruleTable){
		this.age = getAge();
//		this.birthday = currentTick;
		this.currentState = FishLifeState.EGG_INCUBATION;
		this.rules = ruleTable;
//		this.currentReach = getInitialReach;
		setPosition(this.currentReach, 0);
		this.fishID = ID;
		ID++;
		System.out.println("Fish reach = " + initialReach);
	}

	
	public static void setInitialReach(Reach reach) {
	initialReach = reach;
	System.out.println("Reach : " + reach.getReachID());
}

	public Fish(){
		GISDisplay display = GISDisplay.getInstance();
		this.x = display.getMinX() + Math.random() * (display.getMaxX() - display.getMinX());
		this.y = display.getMinY() + Math.random() * (display.getMaxY() - display.getMinY());
	}

	public void consultLifeTable(ArrayList<Rule> rules){
		int state = 0; double rand;
		int currentState = FishLifeState.valueOf(this.getState().toString()).ordinal(); //returns the fish's state as int
		double currentAge = getAge();
		for (int j = 0; j < rules.size(); j++){
			rand = Math.random();
			if (rules.get(j).currentState == currentState && rules.get(j).maxAge > currentAge){
				for (int i = currentState; i < FishLifeState.values().length; i++){
					if (rand < rules.get(j).getTransitionProbMatrix()[i]){
						state = i;
						this.setLifeState(FishLifeState.values()[state]);
						break;
					} else {
						state = currentState;
						rand -= rules.get(j).transitionProbMatrix[i];
					}
				}
			}
		}
		counter++;
		setPosition(this.currentReach, counter);
	}

	private double vy = Math.random() > 0.5 ? Math.random() * -20 : Math.random() * 20;
	private double vx = Math.random() > 0.5 ? Math.random() * -20 : Math.random() * 20;

	public void update(Long tick){
		setTick(tick);
		//consultLifeTable(this.rules);

		this.vy += vy > 0 ? 5 : -5;

		if (vy > 500) vy = 500;
		if (vy < -500) vy = -500;

		this.y -= vy;
		if (this.y < GISDisplay.getInstance().getMinY() || this.y > GISDisplay.getInstance().getMaxY()){
			this.vy *= -1;
		}

		this.vx += vx > 0 ? 5 : -5;

		if (vx > 500) vx = 500;
		if (vx < -500) vx = -500;

		this.x -= vx;
		if (this.x < GISDisplay.getInstance().getMinX() || this.x > GISDisplay.getInstance().getMaxX()) {
			this.vx *= -1;
		}

	}

	private void setTick(Long tick) {
		this.currentTick = tick;
	}

	private void setLifeState(FishLifeState state) {
		this.currentState = state;
	}

	public FishLifeState getState() {
		return currentState;
	}

	private double getAge() {
		return this.currentTick - this.birthday;
	}
	
	private void setPosition(Reach currentReach, int counter){
		PointData[] reachPoints = new PointData[(int) currentReach.length];
		
		reachPoints = currentReach.getPoints();
		
		if(counter < reachPoints.length){
		this.x = reachPoints[counter].getX();
		this.y = reachPoints[counter].getY();
		this.position[0] = this.x;
		this.position[1] = this.y;
		System.out.println("x : " + this.x + ", y : " + this.y);
		return;
		}
		
		else{
			counter = 0;
			ArrayList<Reach> nextReaches = currentReach.getNextReaches();
			int rand = (int) Math.random() * nextReaches.size();
			
			Reach newReach = nextReaches.get(rand);
			setPosition(newReach, counter);
		}
	}
	
	public String[] getCSVInfo(){

//    	String tick = Double.toString(RepastEssentials.GetTickCount());
		String tick = Long.toString(this.currentTick);
		String id = Integer.toString(this.ID);
		String age = Double.toString(getAge());
		String state = FishLifeState.valueOf(this.getState().toString()).toString();
		String location = "null";
		String distanceToOrigin = "null";
		String distanceToTerminal = "null";

		String info[] = {tick, id, age, state , location, distanceToOrigin, distanceToTerminal, "\n"};

		return info;
	}

}


