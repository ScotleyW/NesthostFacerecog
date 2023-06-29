#include <Arduino.h>
#if defined(ESP32)
#include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>
#include <Servo.h>

// Insert your network credentials
#define WIFI_SSID "REPLACE_WITH_YOUR_SSID"
#define WIFI_PASSWORD "REPLACE_WITH_YOUR_PASSWORD"

// Insert Firebase project API Key
#define API_KEY "REPLACE_WITH_YOUR_FIREBASE_PROJECT_API_KEY"

// Insert RTDB URL
#define DATABASE_URL "REPLACE_WITH_YOUR_FIREBASE_DATABASE_URL"

// Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
int led1Pin = 2;     // Replace with the appropriate GPIO pin for LED1
int led2Pin = 3;     // Replace with the appropriate GPIO pin for LED2
int led3Pin = 4;     // Replace with the appropriate GPIO pin for LED3
int servoPin = 5;    // Replace with the appropriate GPIO pin for the servo

Servo servo;

void setup()
{
  Serial.begin(115200);
  pinMode(led1Pin, OUTPUT);
  pinMode(led2Pin, OUTPUT);
  pinMode(led3Pin, OUTPUT);

  servo.attach(servoPin);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  /* Assign the API key (required) */
  config.api_key = API_KEY;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void controlLed(int pin, bool state)
{
  digitalWrite(pin, state);
}

void controlServo(int angle)
{
  servo.write(angle);
}

void loop()
{
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0))
  {
    sendDataPrevMillis = millis();
    if (Firebase.RTDB.getString(&fbdo, "/commands"))
    {
      if (fbdo.dataType() == "string")
      {
        String command = fbdo.stringData();
        Serial.print("Received command: ");
        Serial.println(command);

        if (command.startsWith("led1:"))
        {
          String ledState = command.substring(6);

          // Perform actions for LED1 based on the command
          if (ledState == "on")
          {
            controlLed(led1Pin, HIGH);
            Serial.println("LED1 turned on");
          }
          else if (ledState == "off")
          {
            controlLed(led1Pin, LOW);
            Serial.println("LED1 turned off");
          }
        }
        else if (command.startsWith("led2:"))
        {
          String ledState = command.substring(6);

          // Perform actions for LED2 based on the command
          if (ledState == "on")
          {
            controlLed(led2Pin, HIGH);
            Serial.println("LED2 turned on");
          }
          else if (ledState == "off")
          {
            controlLed(led2Pin, LOW);
            Serial.println("LED2 turned off");
          }
        }
        else if (command.startsWith("led3:"))
        {
          String ledState = command.substring(6);

          // Perform actions for LED3 based on the command
          if (ledState == "on")
          {
            controlLed(led3Pin, HIGH);
            Serial.println("LED3 turned on");
          }
          else if (ledState == "off")
          {
            controlLed(led3Pin, LOW);
            Serial.println("LED3 turned off");
          }
        }
        else if (command == "servo:on")
        {
          // Perform action to move the servo
          controlServo(90);
          Serial.println("Servo turned on");
        }
      }
    }
    else
    {
      Serial.println(fbdo.errorReason());
    }
  }
}
