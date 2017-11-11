#include <Servo.h>
#include <MeetAndroid.h>

#define red 5
#define blue 6
#define green 9
#define trig 11
#define echo 12
#define photodiode 13

MeetAndroid meetAndroid;
Servo myservo;

String masterUser = "adm";
String masterPassword = "123";
String masterMacAddress;

bool isNotificationSended = false;
bool isLocked = false;

void setup() {
  Serial.begin(9600);
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
  meetAndroid.registerFunction(getStatusDoorLockFlag, 'C');
  meetAndroid.registerFunction(lockDoor, 'D');
  closeLock();
}

void loop() {  
  meetAndroid.receive();
  checkDoorOpen();
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

  return found>  index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void getStatusDoorLockFlag(byte flag, byte numOfValues){
  String returnStatus;
  
  if(isLocked){
    returnStatus = "T";
  }
  else{
    returnStatus = "F";
  }
  
  char charReturnStatus[2];
  returnStatus.toCharArray(charReturnStatus, 2);
  
  meetAndroid.send(charReturnStatus);
}

void lockDoor(byte flag, byte numOfValues){
  int length = meetAndroid.stringLength();
  char data[length];
  meetAndroid.getString(data);
  String dataString = String(data);
     
  if(dataString == "Lock"){
    closeLock();
  }
  else{
    openLock();
  }  

  delay(200);

  int photodiodeStatus = digitalRead(photodiode);
  String returnStatus = "Success";

  if(dataString == "Lock" && photodiodeStatus == 0 
  || dataString == "Unlock" && photodiodeStatus == 1){
    returnStatus = "ErrorCloseDoor"; 
  }
  
  char charReturnStatus[25];
  returnStatus.toCharArray(charReturnStatus, 25);

  meetAndroid.send(charReturnStatus);
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
  myservo.write(149);
  isLocked = false;  
  turnOnLedGreen();
}

void closeLock(){
  myservo.write(163);
  isLocked = true;
  turnOnLedRed();
}

void sendNotificationOpenDoor(){
  if(!isNotificationSended){
    String message = "DoorOpened";
    char messageChar[11];
    message.toCharArray(messageChar, 11);
    meetAndroid.send(messageChar);
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
  if(distancia > 450){
    sendNotificationOpenDoor();
  }
  else{
    if(isLocked){
      turnOnLedRed();
    }
    else{
      turnOnLedGreen();
    }
    isNotificationSended = false;
  }
  delay(500);
}

void turnOnLedRed(){
  analogWrite(red, 255);
  analogWrite(blue, 0);
  analogWrite(green, 0);  
}

void turnOnLedGreen(){
  analogWrite(red, 0);
  analogWrite(blue, 0);
  analogWrite(green, 255);
}

void turnOffLed(){
  analogWrite(red, 0);
  analogWrite(blue, 0);
  analogWrite(green, 0);  
}
