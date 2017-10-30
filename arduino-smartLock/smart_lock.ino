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
}

void loop() {  
  checkDoorOpen();
  meetAndroid.receive();
}

void authenticate(byte flag, byte numOfValues){
  delay(5000);
  meetAndroid.send("InvalidName");
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
