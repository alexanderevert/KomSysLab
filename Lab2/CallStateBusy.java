import java.util.*;
import java.net.*;
import java.io.*;

public abstract class CallStateBusy extends CallState{

  public boolean busy(){
    return true;
  }

}
