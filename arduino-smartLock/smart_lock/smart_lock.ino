#include <Servo.h>
#include <IRremote.h>
#include <IRremoteInt.h>
#include <MeetAndroid.h>

#define recv_pin 2
#define emmiter_pin 3
#define red 5
#define blue 6
#define green 9
#define trig 11
#define echo 12

MeetAndroid meetAndroid;
IRrecv irrecv(recv_pin);
Servo myservo;

String masterUser;
String masterPassword;
String masterMacAddress;

int pos = 0;
bool isNotificationSended = false;

void setup() {
  Serial.begin(9600);
  irrecv.enableIRIn();
  myservo.attach(4);
  pinMode(red, OUTPUT);
  pinMode(blue, OUTPUT);
  pinMode(green, OUTPUT);
  pinMode(trig,OUTPUT);
  digitalWrite(trig,LOW);
  delayMicroseconds(10); 
  pinMode(echo,INPUT);
  
  meetAndroid.registerFunction(authenticate, 'A');
  meetAndroid.registerFunction(signup, 'B');
}

void loop() {  
  meetAndroid.receive();
  //checkDoorOpen();
  delay(500);
}

String split(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void signup(byte flag, byte numOfValues){
    
    int length = meetAndroid.stringLength();
    char data[length];
    meetAndroid.getString(data);

    masterUser = split(String(data), ';', 0);
    masterPassword  = split(String(data), ';', 1 );
    masterMacAddress  = split(String(data), ';', 2 );
}

void authenticate(byte flag, byte numOfValues){
    
    int length = meetAndroid.stringLength();
    char data[length];
    meetAndroid.getString(data);

    String user = split(String(data), ';', 0);
    String password = split(String(data), ';', 1 );
    String mac = split(String(data), ';', 2 );
    
    String returnStatus;
    if(user == "adm" && password == "123"){
      returnStatus = "SignUp";
    }
    else if(user == masterUser && password == masterPassword){
      if(mac == masterMacAddress){
        returnStatus = "SignIn"; 
      } 
      else{
        returnStatus = "InvalidMac";
      }
    }
    else{
      returnStatus = "InvalidUserOrPassword";
    }

    char charReturnStatus[25];
    returnStatus.toCharArray(charReturnStatus, 25);

    meetAndroid.send(charReturnStatus);
}

void openLock(){
  myservo.write(180);
  analogWrite(red, 0);
  analogWrite(blue, 0);
  analogWrite(green, 255);
}

void closeLock(){
  myservo.write(90);
  analogWrite(red, 0);
  analogWrite(blue, 255);
  analogWrite(green, 0);
}

void sendNotificationOpenDoor(){
  if(!isNotificationSended){
    //sendNotification
  }
  analogWrite(red, 255);
  analogWrite(blue, 255);
  analogWrite(green, 255);
  isNotificationSended = true;
}

void checkDoorOpen(){
  digitalWrite(trig, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig, LOW);
  float distancia = pulseIn(echo, HIGH);
  Serial.println(distancia);
  if(distancia > 290){
    sendNotificationOpenDoor();
  }
  else{
    turnOffLed();
    isNotificationSended = false;
  }
  delay(500);
}

void turnOffLed(){
  analogWrite(red, 0);
  analogWrite(blue, 0);
  analogWrite(green, 0);  
}