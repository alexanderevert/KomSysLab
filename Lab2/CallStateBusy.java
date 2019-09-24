public class CallStateBusy extends CallState{

  public CallState busy(){
    return this;
  }
  public CallState free(){
    System.out.println("Busy");
    return this;
  }

}
