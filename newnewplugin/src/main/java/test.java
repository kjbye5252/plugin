
import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;

import java.io.IOException;
import java.util.logging.Logger;

public class test{
    public static void main(String[] args) throws IOException{
        IODevice device = new FirmataDevice("COM4");
        try{
            device.start();
            device.ensureInitializationIsDone();
            System.out.println("yes");
        } catch(Exception e){
            System.out.println("couldn't do it");
        }
    }
}
