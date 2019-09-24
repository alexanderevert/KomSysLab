
public class CallStateWaitTRO extends CallStateBusy{
    public CallStateWaitTRO(){
      waitTRO();
      troReceived();
    }

    public CallEvent troReceived(){
      sendAck();


    }
    waitTro();


}
