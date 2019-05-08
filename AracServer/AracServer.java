/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
/**
 *
 * @author INSECT
 */

public class AracServer{

    //server soketi eklemeliyiz
    public static ServerSocket serverSocket;
    // Serverýn dileyeceði port
    public static int port = 0;

    public static void Start(int openport) {
            final GpioController gpio = GpioFactory.getInstance(); //pinleri tanýmlama 
            final GpioPinDigitalOutput out0 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
            final GpioPinDigitalOutput out2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
            final GpioPinDigitalOutput out3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
            final GpioPinDigitalOutput out4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW);
            final GpioPinDigitalOutput sagLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04,PinState.LOW);
            final GpioPinDigitalOutput solLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05,PinState.LOW);
            final GpioPinDigitalOutput sensorTriggerPin =  gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06); // Trigger pin as OUTPUT
		    final GpioPinDigitalInput sensorEchoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07,PinPullResistance.PULL_DOWN); // Echo pin as INPUT

            int temp1=1;
            //Gpio.pinMode (06, Gpio.INPUT) ; 
            try {
                AracServer.port = openport;

                // serversoket nesnesi
                AracServer.serverSocket = new ServerSocket(AracServer.port);
                
                
                AracServer.Display("Client Bekleniyor...");

                Socket clientSocket = AracServer.serverSocket.accept();

                AracServer.Display("Client Geldi...");

                ObjectOutputStream sOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream sInput = new ObjectInputStream(clientSocket.getInputStream());
                while(clientSocket.isConnected()){
                
                
                String message = sInput.readObject().toString(); 
                AracServer.Display(message);
                //en.setPwm(1000);
                if(message!=null && message.equals("mesafe")){
                    while(true){
                        
                            Thread.sleep(2000);
                            sensorTriggerPin.high(); // Make trigger pin HIGH
                            Thread.sleep((long) 0.01);// Delay for 10 microseconds
                            sensorTriggerPin.low(); //Make trigger pin LOW
                            while(sensorEchoPin.isLow()){ //Wait until the ECHO pin gets HIGH
				
                            }
                            long startTime= System.nanoTime(); // Store the surrent time to calculate ECHO pin HIGH time.
                            while(sensorEchoPin.isHigh()){ //Wait until the ECHO pin gets LOW
				
                            }    
                            long endTime= System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.
                            sOutput.writeObject(((((endTime-startTime)/1e3)/2) / 29.1));
                            System.out.println("Distance :"+((((endTime-startTime)/1e3)/2) / 29.1) +" cm"); //Printing out the distance in cm  
                            Thread.sleep(1000);
                            
                    }    
                }    
                if(message!=null && message.equals("ses")){
                    while(true){
                if(Gpio.digitalRead(06) == 1){
                        temp1=temp1+1;
                        System.out.println("Ses Geldi");
                        
                        if(temp1%2==0){
                        System.out.println("Ses Geldi");
                            out0.high();
                            out4.high();
                            out2.low();
                            out3.low();
                             
                             
                        }
                        else{
                            
                         System.out.println("2.Ses Geldi");
                            out4.low();
                            out2.low();
                            out3.low();
                            out0.low();
                        }
                        
                }
                Thread.sleep(500);
                }
              }     
                if(message!=null && message.equals("ileri")){
        //AracServer.Display("ileri");
    
                    out0.high();
            out4.high();
            out2.low();
                    out3.low();
            }
                else if(message!=null && message.equals("geri")){
                   out0.low();
            out4.low();
            out2.high();
                    out3.high();
                }
        else if(message!=null && message.equals("sol")){
                  out0.low();
           out2.low();
                    out3.low();
            out4.high();
            Thread.sleep(500);
            out4.low();
            
                }
        else if(message!=null && message.equals("sag")){
            out4.low();
            out2.low();
                    out3.low();
            out0.high();
            Thread.sleep(500);
            out0.low();
                }
        
        else if(message!=null && message.equals("dur")){
           out4.low();
            out2.low();
                    out3.low();
            out0.low();
           
                }
                else if(message!=null && message.equals("sagLed")){
                  for(int i=0; i<50;i++){
                       
                      sagLed.high();

                      Thread.sleep(500);
                      sagLed.low();

                      Thread.sleep(500);
                   }
                }
                else if(message!=null && message.equals("solLed")){
                   for(int i=0; i<5;i++){
                       
                       sagLed.low();
                      solLed.high();
                      Thread.sleep(500);
                      
                      solLed.low();
                      Thread.sleep(500);
                   }
                }
                else if(message!=null && message.equals("dortLed")){
                   for(int i=0; i<5;i++){
                       
                      sagLed.high();
                      solLed.high();
                      Thread.sleep(500);
                      sagLed.low();
                      solLed.low();
                      Thread.sleep(500);
                   }
                }
            }
        
            } catch (IOException ex) {
                Logger.getLogger(AracServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AracServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception e) {
                Logger.getLogger(AracServer.class.getName()).log(Level.SEVERE, null, e);
            }
        
    }

    public static void Display(String msg) {

        System.out.println(msg);

    }
    public static void main(String[] args){
    AracServer.Start(4000);
}
}
