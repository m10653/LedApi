#include "FastLED.h"
// How many leds in your strip? MAX 64
#define NUM_LEDS 50
#define DATA_PIN 6

// Define the array of leds
CRGB leds[NUM_LEDS];
int bytenum = 0;
byte bytes[5];
boolean isError = false;
void setup() {
  //Set baud rate
  Serial.begin(4000000);
  //Send binary number 255 to Id device as an led device. 
  Serial.write(255);
  //Turn on Error LED light
  pinMode(LED_BUILTIN, OUTPUT); 
  digitalWrite(LED_BUILTIN, LOW);
  FastLED.addLeds<WS2811, DATA_PIN, RGB>(leds, NUM_LEDS);
  //Blank Leds
  FastLED.show();
}
void loop() {
  if (Serial.available() > 0) {
    bytes[bytenum] = Serial.read();
    //Correct missing byte if Error is detected
    if (isError) {
      //Check parity byte
      if (bytes[4] == (bytes[0] ^ bytes[1] ^ bytes[2] ^ bytes[3])) {
        isError = false;
        goto shortcut;
      } else {
        shiftl();
        return;
      }
    }
    if (bytenum < 4) {
      bytenum++;
      //Check parity byte
    } else if (bytes[4] == (bytes[0] ^ bytes[1] ^ bytes[2] ^ bytes[3])) {
shortcut:
      bytenum = 0;
      //Set Led colors if Ignore bit is not set 
      if ((bytes[3] >> 7) == 0) {
        int i = (bytes[3] & 63);
        leds[i].red = bytes[0];
        leds[i].green  = bytes[1];
        leds[i].blue = bytes[2];
      }
      //Set update leds if show bit is set.
      if ((bytes[3] & 64) == 64) {
        FastLED.show();
      }
    } else {
      isError = true;
      shiftl();
      digitalWrite(LED_BUILTIN, HIGH);
    }
  }
}
//Shift bits to the left
void shiftl() {
  for (int i = 0; i < 4; i++) {
    bytes[i] = bytes[i + 1];
  }
}
