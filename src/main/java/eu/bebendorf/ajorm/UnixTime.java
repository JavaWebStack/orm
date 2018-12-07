package eu.bebendorf.ajorm;

public class UnixTime {

    private long millis;

    public UnixTime(long millis){
        this.millis = millis;
    }

    public long millis(){
        return millis;
    }

    public long seconds(){
        return millis/1000;
    }

}
