#include "FastLED.h"

// How many leds in your strip?
#define NUM_LEDS 50
#define DATA_PIN 6

// Define the array of leds
CRGB leds[NUM_LEDS];
int bytenum = 0;
byte bytes[5];
void setup() {
  Serial.begin(15000);
  Serial.write(255);
  FastLED.addLeds<WS2811, DATA_PIN, RGB>(leds, NUM_LEDS);
  FastLED.show();
}

void loop() {
  if (Serial.available() > 0) {
    byte temp = Serial.read();
    if (bytenum < 3) {
      bytes[bytenum++] = temp;
    } else {      
      bytes[bytenum] = temp;
      bytenum = 0;
      if ((bytes[3] >> 7) == 0) {
        int i = (bytes[3] & 63);
        leds[i].red = bytes[0];
        leds[i].green  = bytes[1];
        leds[i].blue = bytes[2];
      }
      if ((bytes[3] & 64) == 64) {
        FastLED.show();
      }
    }
  }
}
