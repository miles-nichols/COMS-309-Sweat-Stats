public class Exercise
{
    //Stores the workout being performed
    private Workout workout;

    //Stores the reps complete. The index will save the set and the number stored in
    //each index will be the reps completed.
    private int[] reps;

    //Base instructor not to be used
    public Exercise () {}

    //Constructor
    public Exercise (Workout workout, int[] reps)
    {
        this.workout = workout;
        this.reps = reps;
    }

    public Workout getWorkout() {return workout;}

    public void setWorkout(Workout workout) {this.workout = workout;}

    public int[] getReps() {return reps;}

    public void setReps(int[] reps) {this.reps = reps;}
}
