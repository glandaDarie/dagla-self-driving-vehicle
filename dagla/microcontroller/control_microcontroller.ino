#define ARRAY_SIZE 16
String strArray[ARRAY_SIZE];
int sizeArray = -1;

void initalizeStrArray() 
{
  for(int i = 0; i < ARRAY_SIZE; ++i) 
  {
    strArray[i] = "None";
  }  
}

String *split(String string, char delimiter) 
{
  initalizeStrArray();
  String buildString = "";
  int count = 0;
  for(int i = 0; i < string.length(); ++i) 
  {
      if(string[i] == delimiter)
      {
        strArray[count] = buildString;
        count += 1;
        buildString = "";
        continue;
      }
      buildString += string[i];
  }
  strArray[count] = buildString;
  count += 1;
  buildString = "";
  
  String *ptrString = (String*)calloc(count, sizeof(String));
  
  if(ptrString == NULL) 
  {
    Serial.print("Can't allocate memory!");
    exit(0);
  }

  sizeArray = count;

  for(int i = 0; i < count; ++i) 
  {
    ptrString[i] = strArray[i];
  }
  
  return ptrString;
}

bool stringIsInteger(String input) 
{
  for(int i = 0; i < input.length(); ++i) 
  {
      if(!isDigit(input[i])) 
      {
        return false;
      }  
  }  
  return true;
}

class Direction 
{
  private:
    int pin_pwmLeftSide;
    int pin_highLeft; 
    int pin_lowLeft;

    int pin_pwmRightSide;
    int pin_highRight;
    int pin_lowRight;
  
    int auxPinLed;
    
    int pwmValueHigh, pwmValueLow;
    int countTime;
    int leftPwm, rightPwm;
    int pwm = -1;
    int offset = 100;
   
    unsigned long elapsedTime;
    unsigned long previousMillis;

    int beforeMap;
  
  public:
    Direction(int pin_pwmLeftSide, int pin_highLeft, int pin_lowLeft, 
    int pin_pwmRightSide, int pin_highRight, int pin_lowRight, int auxPinLed) 
    {
      this->pin_pwmLeftSide = pin_pwmLeftSide;
      this->pin_highLeft = pin_highLeft;
      this->pin_lowLeft = pin_lowLeft;
      
      this->pin_pwmRightSide = pin_pwmRightSide;
      this->pin_highRight = pin_highRight;
      this->pin_lowRight = pin_lowRight;
      
      this->auxPinLed = auxPinLed;
  
      pwmValueHigh = 255;
      pwmValueLow = 0;
      leftPwm = rightPwm = 0;
      elapsedTime = 0;
      pwm = 0;
      beforeMap = 0;
      
      initSetup();
    }

    void initSetup() 
    { 
      pinMode(pin_pwmLeftSide,OUTPUT);
      pinMode(pin_highLeft,OUTPUT);
      pinMode(pin_lowLeft,OUTPUT);
      
      pinMode(pin_pwmRightSide,OUTPUT);
      pinMode(pin_highRight,OUTPUT);
      pinMode(pin_lowRight,OUTPUT);

      pinMode(auxPinLed, OUTPUT);
    }

    void setPwm(int pwm) 
    {
      this->pwm = pwm;
    }

    int getPwm() 
    {
      return pwm;
    }

    void setBeforeMap(int beforeMap) 
    {
      this->beforeMap = beforeMap;  
    }

    int getBeforeMap() 
    {
      return beforeMap;  
    }

    void switchLedStates() 
    {
        digitalWrite(auxPinLed, HIGH);
        delay(500);
        digitalWrite(auxPinLed, LOW);
        delay(500);
    }

    void goForward() 
    {
      setPwm(map(pwm, 0, 100, 0, 255));
      
      digitalWrite(pin_highLeft,LOW);
      digitalWrite(pin_lowLeft,HIGH);
      analogWrite(pin_pwmLeftSide,getPwm()+offset);

      digitalWrite(pin_highRight,LOW);
      digitalWrite(pin_lowRight,HIGH);
      analogWrite(pin_pwmRightSide,getPwm()+offset);
    }

    void goBackwards() 
    {
      setPwm(map(pwm, 0, 100, 0, 255));
      
      digitalWrite(pin_highLeft,HIGH);
      digitalWrite(pin_lowLeft,LOW);
      analogWrite(pin_pwmLeftSide,getPwm());

      digitalWrite(pin_highRight,HIGH);
      digitalWrite(pin_lowRight,LOW);
      analogWrite(pin_pwmRightSide,getPwm());
    }

    void goLeft() 
    { 
      digitalWrite(pin_highLeft,HIGH);
      digitalWrite(pin_lowLeft,LOW);
      analogWrite(pin_pwmLeftSide,leftPwm);

      digitalWrite(pin_highRight,HIGH);
      digitalWrite(pin_lowRight,LOW);
      analogWrite(pin_pwmRightSide,pwmValueHigh);
    } 

    void goRight() 
    {
      digitalWrite(pin_highLeft,HIGH);
      digitalWrite(pin_lowLeft,LOW);
      analogWrite(pin_pwmLeftSide,pwmValueHigh);
    
      digitalWrite(pin_highRight,HIGH);
      digitalWrite(pin_lowRight,LOW);
      analogWrite(pin_pwmRightSide,rightPwm);
    }

    void Stop() 
    {
      digitalWrite(pin_highLeft, LOW);
      digitalWrite(pin_lowLeft, LOW);
      digitalWrite(pin_pwmLeftSide, pwmValueLow);

      digitalWrite(pin_highRight, LOW);
      digitalWrite(pin_lowLeft, LOW);
      digitalWrite(pin_pwmRightSide,pwmValueLow);
    }


    void goForwardVoice() 
    {
      digitalWrite(pin_highLeft,LOW);
      digitalWrite(pin_lowLeft,HIGH);
      analogWrite(pin_pwmLeftSide,pwmValueHigh);

      digitalWrite(pin_highRight,LOW);
      digitalWrite(pin_lowRight,HIGH);
      analogWrite(pin_pwmRightSide,pwmValueHigh);
    }

    void goBackwardsVoice() 
    {
      digitalWrite(pin_highLeft,HIGH);
      digitalWrite(pin_lowLeft,LOW);
      analogWrite(pin_pwmLeftSide,pwmValueHigh);

      digitalWrite(pin_highRight,HIGH);
      digitalWrite(pin_lowRight,LOW);
      analogWrite(pin_pwmRightSide,pwmValueHigh);
    }

    void goLeftVoice() 
    { 
      digitalWrite(pin_highLeft,HIGH);
      digitalWrite(pin_lowLeft,LOW);
      analogWrite(pin_pwmLeftSide,leftPwm);

      digitalWrite(pin_highRight,HIGH);
      digitalWrite(pin_lowRight,LOW);
      analogWrite(pin_pwmRightSide,pwmValueHigh);
    } 

    void goRightVoice() 
    {
      digitalWrite(pin_highLeft,HIGH);
      digitalWrite(pin_lowLeft,LOW);
      analogWrite(pin_pwmLeftSide,pwmValueHigh);
    
      digitalWrite(pin_highRight,HIGH);
      digitalWrite(pin_lowRight,LOW);
      analogWrite(pin_pwmRightSide,rightPwm);
    }
    
    void spin(int pwm) 
    {
       digitalWrite(pin_highLeft,LOW);
       digitalWrite(pin_lowLeft,HIGH);
       analogWrite(pin_pwmLeftSide,pwm);
  
       digitalWrite(pin_highRight,LOW);
       digitalWrite(pin_lowRight,HIGH);
       analogWrite(pin_pwmRightSide,pwmValueLow);
    }

    unsigned long getCurrentMillis() 
    {      
      elapsedTime = millis();
      delay(10);
      return elapsedTime;
    }

    unsigned long getPreviousMillis()
    {
        return previousMillis;
    }

    void setPreviousMillis(unsigned long previousMillis) 
    {
       this->previousMillis = previousMillis;
    }

    void goForwardWithSpecifiedMiliSeconds() 
    {
       digitalWrite(pin_highLeft,LOW);
       digitalWrite(pin_lowLeft,HIGH);
       analogWrite(pin_pwmLeftSide,pwmValueHigh);

       digitalWrite(pin_highRight,LOW);
       digitalWrite(pin_lowRight,HIGH);
       analogWrite(pin_pwmRightSide,pwmValueHigh);
    }

    void goBackwardsWithSpecifiedMiliSeconds() 
    {
       digitalWrite(pin_highLeft,HIGH);
       digitalWrite(pin_lowLeft,LOW);
       analogWrite(pin_pwmLeftSide,pwmValueHigh);

       digitalWrite(pin_highRight,HIGH);
       digitalWrite(pin_lowRight,LOW);
       analogWrite(pin_pwmRightSide,pwmValueHigh);
    }

    void goLeftWithSpecifiedMiliSeconds() 
    {
       digitalWrite(pin_highLeft,LOW);
       digitalWrite(pin_lowLeft,HIGH);
       analogWrite(pin_pwmLeftSide,150);
  
       digitalWrite(pin_highRight,LOW);
       digitalWrite(pin_lowRight,HIGH);
       analogWrite(pin_pwmRightSide,pwmValueHigh);
    }

    void goRightWithSpecifiedMiliSeconds() 
    {
       digitalWrite(pin_highLeft,LOW);
       digitalWrite(pin_lowLeft,HIGH);
       analogWrite(pin_pwmLeftSide,pwmValueHigh);
  
       digitalWrite(pin_highRight,LOW);
       digitalWrite(pin_lowRight,HIGH);
       analogWrite(pin_pwmRightSide,150);
    }

    void spinWithSpecifiedMiliSeconds() 
    {
       digitalWrite(pin_highLeft,LOW);
       digitalWrite(pin_lowLeft,HIGH);
       analogWrite(pin_pwmLeftSide,pwmValueHigh);
  
       digitalWrite(pin_highRight,LOW);
       digitalWrite(pin_lowRight,HIGH);
       analogWrite(pin_pwmRightSide,pwmValueLow);
    }
};

int pin_pwmLeftSide = 5;
int pin_highLeft = 2;
int pin_lowLeft = 7;

int pin_pwmRightSide = 10;
int pin_highRight = 12;
int pin_lowRight = 13; 

int auxPinLed = 4;
int defaultPwm = 110;

Direction dir(pin_pwmLeftSide, pin_highLeft, pin_lowLeft, pin_pwmRightSide, pin_highRight, pin_lowRight, auxPinLed); 

static bool happened = false; 
String stringAndroidStudio = "";
String mixed = "";
String currentString = "";
bool globalFlag = false;

enum Command { up, down, left, right, spin };

void readKeyPressed() 
{
  if(stringAndroidStudio.equals("forward")) 
  {
    dir.goForward();
  }
  
  else if(stringAndroidStudio.equals("backwards")) 
  {
    dir.goBackwards(); 
  }
  
  else if(stringAndroidStudio.equals("left")) 
  {
    dir.goLeft();
  }
  
  else if(stringAndroidStudio.equals("right")) 
  {
    dir.goRight();
  }
  
  else if(stringAndroidStudio.equals("stop") || stringAndroidStudio.equals("STOP")) 
  {
    dir.Stop();
  }

  else if(stringAndroidStudio.equals("forward voice") || stringAndroidStudio.equals("vorwärts voice") ||
  stringAndroidStudio.equals("en avant voice") || stringAndroidStudio.equals("înainte voice") || stringAndroidStudio.equals("napred voice")) 
  {
    dir.goForwardVoice();
  }
 
  else if(stringAndroidStudio.equals("backwards voice") || stringAndroidStudio.equals("rückwärts voice") ||
  stringAndroidStudio.equals("en arrière voice") || stringAndroidStudio.equals("înapoi voice") || stringAndroidStudio.equals("nazad voice")) 
  {
    dir.goBackwardsVoice();
  }

  else if(stringAndroidStudio.equals("left voice") || stringAndroidStudio.equals("links voice") ||
  stringAndroidStudio.equals("gauche voice") || stringAndroidStudio.equals("stânga voice") || stringAndroidStudio.equals("levo voice")) 
  {
    dir.goLeftVoice();
  }

  else if(stringAndroidStudio.equals("right voice") || stringAndroidStudio.equals("rechts voice") ||
  stringAndroidStudio.equals("droite voice") || stringAndroidStudio.equals("dreapta voice") || stringAndroidStudio.equals("desno voice")) 
  {
    dir.goRightVoice();
  }

  else if(stringAndroidStudio.equals("stop voice") || stringAndroidStudio.equals("aufhören voice") ||
  stringAndroidStudio.equals("arrêter voice") || stringAndroidStudio.equals("oprește voice") || stringAndroidStudio.equals("prestani voice")) 
  {
    dir.Stop();
  }

  else if(stringAndroidStudio.equals("f")) 
  {
    dir.goForwardWithSpecifiedMiliSeconds();
  }

  else if(stringAndroidStudio.equals("s"))
  {
    dir.Stop();
  }
 
  else if(stringAndroidStudio.equals("UP")) 
  {
    dir.goForwardWithSpecifiedMiliSeconds();
  }

  else if(stringAndroidStudio.equals("DOWN")) 
  {
    dir.goBackwardsWithSpecifiedMiliSeconds();
  }

  else if(stringAndroidStudio.equals("LEFT")) 
  {
    dir.goLeftWithSpecifiedMiliSeconds();
  }

  else if(stringAndroidStudio.equals("RIGHT")) 
  {
    dir.goRightWithSpecifiedMiliSeconds();
  }

  else if(stringAndroidStudio.equals("SPIN")) 
  {
    dir.spinWithSpecifiedMiliSeconds();
  }

  else if(stringAndroidStudio.indexOf("test up and down") >= 0)
  {
     unsigned long startTimer, endTimer;
     String auxString = stringAndroidStudio;
     String *splitter = split(auxString, '-');
     bool hasEntered = false;
     int miliseconds = -1; 
     if(sizeArray != -1) 
     {
       for(int i = 0; i < sizeArray; ++i) 
       {
          if(stringIsInteger(splitter[i])) 
          {
             miliseconds = splitter[i].toInt(); 
             hasEntered = true; 
          }
       }
      if(hasEntered) 
      {
         if(!globalFlag) 
         {
              startTimer = millis();
              globalFlag = true;
         } 
         endTimer = millis();
         while((endTimer - startTimer) <= miliseconds) 
         {
             if((endTimer - startTimer) <= (miliseconds / 2)) 
             {
                 dir.goForwardWithSpecifiedMiliSeconds();  
             }
             else if((endTimer - startTimer) > (miliseconds / 2))  
             {
                 dir.goBackwardsWithSpecifiedMiliSeconds();  
             }
             endTimer = millis();
          }
         dir.Stop();
         globalFlag = false;
       }
     }
   }
   else if(stringAndroidStudio.indexOf("test down and up") >= 0) 
   {
      unsigned long startTimer, endTimer;
      String auxString = stringAndroidStudio;
      String *splitter = split(auxString, '-'); 
      bool hasEntered = false;
      int miliseconds = -1; 
      if(sizeArray != -1)
      {
        for(int i = 0; i < sizeArray; ++i) 
        {
           if(stringIsInteger(splitter[i])) 
           {
              miliseconds = splitter[i].toInt(); 
              hasEntered = true; 
           }
        }
       if(hasEntered) 
       {
          if(!globalFlag) 
          {
               startTimer = millis();
               globalFlag = true;
          } 
          endTimer = millis();
          while((endTimer - startTimer) <= miliseconds) 
          {
              if((endTimer - startTimer) <= (miliseconds / 2)) 
              {
                  dir.goBackwardsWithSpecifiedMiliSeconds();  
              }
              else if((endTimer - startTimer) > (miliseconds / 2))
              {
                  dir.goForwardWithSpecifiedMiliSeconds();  
              }
              endTimer = millis();
           }
          dir.Stop();
          globalFlag = false; 
       }
     }
   }
   else if(stringAndroidStudio.indexOf("test left and right") >= 0)
   {
      unsigned long startTimer, endTimer;
      String auxString = stringAndroidStudio;
      String *splitter = split(auxString, '-'); 
      bool hasEntered = false;
      int miliseconds = -1; 
      if(sizeArray != -1) 
      {
        for(int i = 0; i < sizeArray; ++i) 
        {
           if(stringIsInteger(splitter[i])) 
           {
              miliseconds = splitter[i].toInt(); 
              hasEntered = true; 
           }
        }
       if(hasEntered) 
       {
          if(!globalFlag) 
          {
               startTimer = millis();
               globalFlag = true;
          } 
          endTimer = millis();
          while((endTimer - startTimer) <= miliseconds)
          {
              if((endTimer - startTimer) <= (miliseconds / 2)) 
              {
                  dir.goLeftWithSpecifiedMiliSeconds();  
              }
              else if((endTimer - startTimer) > (miliseconds / 2))  
              {
                  dir.goRightWithSpecifiedMiliSeconds();  
              }
              endTimer = millis(); 
           }
          dir.Stop();
          globalFlag = false; 
       }
     }
   }
   else if(stringAndroidStudio.indexOf("test right and left") >= 0) 
   {
      unsigned long startTimer, endTimer;
      String auxString = stringAndroidStudio;
      String *splitter = split(auxString, '-'); 
      bool hasEntered = false;
      int miliseconds = -1;
      if(sizeArray != -1) 
      {
        for(int i = 0; i < sizeArray; ++i) 
        {
           if(stringIsInteger(splitter[i])) 
           {
              miliseconds = splitter[i].toInt(); 
              hasEntered = true; 
           }
        }
       if(hasEntered) 
       {
          if(!globalFlag) 
          {
               startTimer = millis();
               globalFlag = true;
          } 

          endTimer = millis();
          while((endTimer - startTimer) <= miliseconds) 
          {
              if((endTimer - startTimer) <= (miliseconds / 2)) 
              {
                  dir.goRightWithSpecifiedMiliSeconds();   
              }
              else if((endTimer - startTimer) > (miliseconds / 2))   
              {
                  dir.goLeftWithSpecifiedMiliSeconds(); 
              }
              endTimer = millis(); 
           }
          dir.Stop();
          globalFlag = false; 
       }
     }
   }
   else if(stringAndroidStudio.indexOf("test spin fast to slow") >= 0) 
   {
      unsigned long startTimer, endTimer;
      String auxString = stringAndroidStudio;
      String *splitter = split(auxString, '-');
      bool hasEntered = false;
      int miliseconds = -1;
      if(sizeArray != -1) 
      {
        for(int i = 0; i < sizeArray; ++i) 
        {
           if(stringIsInteger(splitter[i])) 
           {
              miliseconds = splitter[i].toInt(); 
              hasEntered = true; 
           }
        }
       if(hasEntered) 
       {
          if(!globalFlag)
          {
               startTimer = millis();
               globalFlag = true;
          } 
  
          endTimer = millis();
          int pwm = 255;
          int pwmTotal = 255;
          int delayTime = miliseconds / pwmTotal; 
          while((endTimer - startTimer) <= miliseconds) 
          {
              dir.spin(pwm);
              delay(delayTime);
              pwm -= 1;
              
              endTimer = millis();
           }

          dir.Stop();
          globalFlag = false;
       }
     }
   }
   else if(stringAndroidStudio.indexOf("test spin slow to fast") >= 0) 
   {
      unsigned long startTimer, endTimer;
      String auxString = stringAndroidStudio;
      String *splitter = split(auxString, '-');
      bool hasEntered = false;
      int miliseconds = -1;
      if(sizeArray != -1)
      {
        for(int i = 0; i < sizeArray; ++i)
        {
           if(stringIsInteger(splitter[i])) 
           {
              miliseconds = splitter[i].toInt(); 
              hasEntered = true; 
           }
        }
       if(hasEntered) 
       {
          if(!globalFlag)
          {
               startTimer = millis();
               globalFlag = true;
          } 
          endTimer = millis();
          int pwm = 0;
          int pwmTotal = 255;
          int delayTime = miliseconds / pwmTotal; 
          while((endTimer - startTimer) <= miliseconds) 
          { 
              dir.spin(pwm);
              delay(delayTime);
              pwm += 1;
              endTimer = millis(); 
           }
          dir.Stop();
          globalFlag = false;
       }
     }
   }
   else if(stringAndroidStudio.indexOf("test random command") >= 0) 
   {
      String cases [5] = {"up", "down", "left", "right", "spin"};
      int sizeCases = sizeof(cases) / sizeof(String);
      int gotCommand = random(0,5);
      if(gotCommand != -1)
      {
          switch(gotCommand)
          {
            case up: 
            {
               moveVehicle(gotCommand);
            } 
            break;
      
            case down:
            {
               moveVehicle(gotCommand); 
            }
            break;
      
            case left:
            {
               moveVehicle(gotCommand); 
            }
            break;
      
            case right:
            {
               moveVehicle(gotCommand); 
            }
            break;
      
            case spin:
            {
               moveVehicle(gotCommand); 
            }
            break;
      
            default:
            {
               exit(0); 
            }
          }  
      } 
   }
   else if(stringAndroidStudio.indexOf("test choose command") >= 0) 
   {
      unsigned long startTimer, endTimer;
      String auxString = stringAndroidStudio;
      String *splitter = split(auxString, '-'); 
      bool hasEntered = false;
      int miliseconds = -1;
      String command = "None"; 
      if(sizeArray != -1) 
      {
        for(int i = 0; i < sizeArray; ++i) 
        {
           if(stringIsInteger(splitter[i])) 
           {
              miliseconds = splitter[i].toInt(); 
              hasEntered = true; 
           }
        }
        if(hasEntered) 
        {
            command = splitter[sizeArray-1]; 
            if(!globalFlag) 
            {
               startTimer = millis();
               globalFlag = true;
            } 

             endTimer = millis();
             while((endTimer - startTimer) <= miliseconds)
             {
                displayGivenCommand(command);
                endTimer = millis();
             }
             dir.Stop();
             globalFlag = false;
        }
      }
   }
}

void displayGivenCommand(String gotCommand) 
{
    if(gotCommand.equals("forward") || gotCommand.equals("Forward")) { dir.goForwardWithSpecifiedMiliSeconds(); }
    if(gotCommand.equals("backwards") || gotCommand.equals("Backwards")) { dir.goBackwardsWithSpecifiedMiliSeconds(); }
    if(gotCommand.equals("left") || gotCommand.equals("Left")) { dir.goLeftWithSpecifiedMiliSeconds(); }
    if(gotCommand.equals("right") || gotCommand.equals("Right")) { dir.goRightWithSpecifiedMiliSeconds(); }
    if(gotCommand.equals("spin") || gotCommand.equals("Spin")) { dir.spinWithSpecifiedMiliSeconds(); }
}

void moveVehicle(int gotCommand) 
{
    unsigned long startTimer, endTimer;
    String auxString = stringAndroidStudio;
    String *splitter = split(auxString, '-'); 
    bool hasEntered = false;
    int miliseconds = -1; 
    if(sizeArray != -1) 
    {
       for(int i = 0; i < sizeArray; ++i)
       {
           if(stringIsInteger(splitter[i])) 
           {
               miliseconds = splitter[i].toInt(); 
               hasEntered = true; 
            }
       }
       if(hasEntered) 
       {
           if(!globalFlag)
           {
               startTimer = millis();
               globalFlag = true;
           } 
               endTimer = millis();
               while((endTimer - startTimer) <= miliseconds) 
               {
                   if(gotCommand == up) { dir.goForwardWithSpecifiedMiliSeconds(); }
                   if(gotCommand == down) { dir.goBackwardsWithSpecifiedMiliSeconds(); }
                   if(gotCommand == left) { dir.goLeftWithSpecifiedMiliSeconds(); }
                   if(gotCommand == right) { dir.goRightWithSpecifiedMiliSeconds(); }
                   if(gotCommand == spin) { dir.spinWithSpecifiedMiliSeconds(); }
                   endTimer = millis();
               }
                dir.Stop();
                globalFlag = false;
        }
    }  
}

bool everyCharacterFromStringIsDigit(String androidStudioData) 
{
  if(androidStudioData.length() == 0) 
  {
    return false;
  }
  for(int i = 0; i < androidStudioData.length(); ++i) 
  {
    if(!isDigit(androidStudioData[i])) 
    {
      return false;
    }   
  }
  return true;
}

int getIntegerPart(String stringPart) 
{
  String auxString = "";
  for(int i = 0; i < stringPart.length(); ++i) 
  {
    if(!isDigit(stringPart[i])) 
    {
      break;
    }
    auxString += stringPart[i];
  }
  int auxInt = auxString.toInt();
  if(!happened) 
  {
    auxInt = map(auxInt, 0, 255, 0, 100);
  }
  return auxInt;
}

void setup() 
{ 
  Serial.begin(9600); 
  randomSeed(analogRead(0));
}

void loop() 
{
    dir.switchLedStates(); 
    mixed = String(defaultPwm)+"-";
    if(!happened) 
    {
      dir.setPwm(getIntegerPart(mixed));
      Serial.print(String(getIntegerPart(mixed))+"-"); 
    }
    char currentAndroidStudioChar;
    String auxBuildString = "";
    while(Serial.available() > 0) 
    {
      currentAndroidStudioChar = Serial.read();
      if(currentAndroidStudioChar == '\0' || currentAndroidStudioChar == '\n') 
      { 
        break;
      }
      auxBuildString += currentAndroidStudioChar;
    }
    stringAndroidStudio = auxBuildString;
    if(everyCharacterFromStringIsDigit(auxBuildString)) 
    {
      happened = true;
      mixed = String(auxBuildString)+"-";
      dir.setPwm(getIntegerPart(mixed));
      Serial.print(String(getIntegerPart(mixed))+"-");  
    }
    stringAndroidStudio.trim(); 
    readKeyPressed();
    stringAndroidStudio = "";
}
