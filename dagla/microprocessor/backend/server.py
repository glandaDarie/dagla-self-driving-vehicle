from flask import Flask, Response, render_template
import cv2
import concurrent.futures
import time
import socket
import os
import re
from datetime import datetime
import RPi.GPIO as GPIO
import numpy as np
import subprocess
import sys
import serial

ICON_FOLDER = os.path.join('static', 'images')
app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = ICON_FOLDER

LED_PIN = 17

GPIO.setmode(GPIO.BCM)
GPIO.setup(LED_PIN, GPIO.OUT)

videoRunning = True
runCustomThread = True
happened = False
photoTaken = False

APP_NAME = "Glanda Darie Teofil - signal detection"
connection = ""
filename = "autopilot.mp4"
framesPerSecond = 24.0
standardDimension = "720p"

cap = cv2.VideoCapture(0) 
frameWidth = int(cap.get(3)) 
frameHeight = int(cap.get(4))
framesPerSecond = int(round(cap.get(5)))

out = cv2.VideoWriter(filename, cv2.VideoWriter_fourcc(*'mp4v'), framesPerSecond,
                      (frameWidth, frameHeight))

ser = serial.Serial('/dev/ttyACM0', 9600, timeout=1)

CURRENT_DIRECTORY = os.path.join(os.getcwd(), "Cascades")
STOP_SIGN_MODEL_FILE = "cascade_stop_sign.xml"
TRAFFIC_LIGHT_MODEL_FILE = "cascade_traffic_light.xml"

def getSocket():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    ip = s.getsockname()[0]
    port = 5000
    return ip, port

@app.route('/index_contact')
def index_contact() -> None:
    fullFilenameFacebook = os.path.join(app.config['UPLOAD_FOLDER'], "facebook.jpg")
    fullFileNameInstagram = os.path.join(app.config['UPLOAD_FOLDER'], "instagram.jpg")
    fullFileNameTwitter = os.path.join(app.config['UPLOAD_FOLDER'], "twitter.jpg")
    fullFileNameLogo = os.path.join(app.config['UPLOAD_FOLDER'], "logo.jpg")
    return render_template('index_contact.html', facebookIcon=fullFilenameFacebook,
            instagramIcon=fullFileNameInstagram, twitterIcon=fullFileNameTwitter, carLogo=fullFileNameLogo)

@app.route('/index_help')
def index_help() -> None:
    fullFilenameFacebook = os.path.join(app.config['UPLOAD_FOLDER'], "facebook.jpg")
    fullFileNameInstagram = os.path.join(app.config['UPLOAD_FOLDER'], "instagram.jpg")
    fullFileNameTwitter = os.path.join(app.config['UPLOAD_FOLDER'], "twitter.jpg")
    fullFileNameLogo = os.path.join(app.config['UPLOAD_FOLDER'], "logo.jpg")
    return render_template('index_help.html', facebookIcon=fullFilenameFacebook,
            instagramIcon=fullFileNameInstagram, twitterIcon=fullFileNameTwitter, carLogo=fullFileNameLogo)

def cameraOn() -> None:
    global cap
    global LED_PIN
    global out
    global ser
    
    stopSign = cv2.CascadeClassifier(os.path.join(CURRENT_DIRECTORY, "cascade_stop_sign.xml"))
    trafficLight = cv2.CascadeClassifier(os.path.join(CURRENT_DIRECTORY, "cascade_traffic_light.xml"))
    
    lowerRedOne = np.array([0, 100, 100])
    upperRedOne = np.array([10, 255, 255])

    lowerRedTwo = np.array([160, 100, 100])
    upperRedTwo = np.array([180, 255, 255])
    
    lowerYellow = np.array([20, 0, 0])
    upperYellow = np.array([40, 255, 255])
    
    lowerGreen = np.array([50, 100, 100])
    upperGreen = np.array([70, 255, 255])
    
    currentTime = int(time.time())
    while int(time.time()) - currentTime < 5: 
        GPIO.output(LED_PIN, GPIO.HIGH)
        time.sleep(1) 
        GPIO.output(LED_PIN, GPIO.LOW)
        time.sleep(1)
    
    GPIO.output(LED_PIN, GPIO.HIGH) 
    timePassed = int(time.time()) 
    
    flag = flagDetectedStop = flagTime = False
    happenedOnce = False 
    checkHappenedRed = checkHappenedYellow = checkHappenedGreen = False
    flagColorRed = flagColorYellow = flagColorGreen = False
    
    ser.flush() 
    while True:
        success, image = cap.read() 
        if success: 
            if flagDetectedStop is True or flagColorRed is True or flagColorYellow is True or flagColorGreen is True:
                if flagTime is False: 
                    stopSignTime = time.time()
                    flagTime = True
                if int(time.time()) - stopSignTime > 3:
                    flag = flagTime = False
                    if flagDetectedStop:
                        flagDetectedStop = False 
                        happenedOnce = True 
                    elif flagColorRed:
                        flagColorRed = False
                        checkHappenedRed = True
                    elif flagColorYellow:
                        flagColorYellow = False
                        checkHappenedYellow = True
                    elif flagColorGreen:
                        flagColorGreen = False
                        checkHappenedGreen = True
            else:
                if flag is False:
                    ser.write("f\n".encode('ascii')) 
                    flag = True
            
            detectedStopSign, width, height, image = stopSignDetection(stopSign, image) 
            if detectedStopSign:
                image = cv2.putText(image, "Width = "+ str(width) + ", " + "Height = "+ str(height), (200,100), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
            
            if happenedOnce is False and detectedStopSign and (width >= 110 and height >= 110): 
                ser.write("s\n".encode('ascii'))
                flagDetectedStop = True
            
            detectedTrafficLight, coordX, coordY, width, height, image = trafficLightDetection(trafficLight, image) 
            if detectedTrafficLight:
                image = cv2.putText(image, "Width = "+ str(width) + ", " + "Height = "+ str(height), (200,100), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA) 
                hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
                image, detectedRed, detectedYellow, detectedGreen = detectLight(image, hsv, coordX, coordY, width, height, lowerRedOne, upperRedOne, lowerRedTwo, upperRedTwo, lowerYellow, upperYellow, lowerGreen, upperGreen)
                colors = [detectedRed, detectedYellow, detectedGreen]
                countColorsDetected = sum(colors) 
                if countColorsDetected == 1: 
                    if detectedRed: 
                        if (width >= 110 and height >= 110) and checkHappenedRed is False: 
                            ser.write("s\n".encode('ascii')) 
                            flagColorRed = True 
                    if detectedYellow: 
                        if (width >= 110 and height >= 110) and checkHappenedYellow is False: 
                            ser.write("w\n".encode('ascii')) 
                            flagColorYellow = True 
                    if detectedGreen: 
                        if (width >= 110 and height >= 110) and checkHappenedGreen is False: 
                            ser.write("f\n".encode('ascii')) 
                            flagColorGreen = True 
            if (int(time.time()) - timePassed) > 12:
                cv2.destroyAllWindows()
                ser.write("s\n".encode('ascii')) 
                return
            out.write(image)
            continue
        else:
            exit(0)
    
def distance(x, y, frame) -> tuple: 
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    blur = cv2.blur(gray, (3, 3))
    _, thresh = cv2.threshold(blur, 200, 255, cv2.THRESH_BINARY)
    contours, _ = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
    distanceObject = -1
    if len(trafficLightContours(x, y, contours)) != 0:
        count = np.array(trafficLightContours(x, y, contours))
        area = cv2.contourArea(count)
        distanceObject = 2 * (10 * (-7)) * (area * 2) - (0.0067 * area) + 83.487
        stringDistance = "Distance of the object: " + str(distanceObject)
        cv2.putText(frame, str(stringDistance), (5, 70), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
        cv2.drawContours(frame, contours, -1, (0, 255, 0), 3)
        distanceObject = int(distanceObject)
    else:
        return frame, distanceObject
    return frame, distanceObject

def trafficLightContours(coordX, coordY, contours) -> list:
    numpyList = []
    for contour in contours:
        for downList in contour:
            for coordinates in downList:
                if ((coordX + 10) >= coordinates[0] and (coordX - 10) <= coordinates[0]) or (coordX <= (coordinates[0] + 10) and coordX >= (coordinates[0] - 10)):
                    if ((coordY + 10) >= coordinates[1] and (coordY - 10) <= coordinates[1]) or (coordY <= (coordinates[1] + 10) and coordY >= (coordinates[1] - 10)):
                        auxList = []
                        currentList = []
                        currentList.append(coordinates[0])
                        currentList.append(coordinates[1])
                        auxList.append(currentList)
                        numpyList.append(auxList)
    return numpyList

def trafficLightDetection(trafficLight, image) -> tuple:
    flag = False
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    trafficLightScaled = trafficLight.detectMultiScale(gray, 1.3, 5)
    coordX, coordY = -1, -1
    width, height = -1, -1
    if len(trafficLightScaled) == 0: 
        return flag, coordX, coordY, width, height, image
    else:
        for (x, y, w, h) in trafficLightScaled:
             width, height = w, h
             coordX, coordY = x, y
             flag = True 
    return flag, coordX, coordY, width, height, image 

def stopSignDetection(stopSign, image) -> tuple:
    flag = False
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    stopSignScaled = stopSign.detectMultiScale(gray, 1.3, 5)
    width, height = -1, -1
    if len(stopSignScaled) == 0: 
        return flag, width, height, image
    else:
        for (_, _, w, h) in stopSignScaled:
            width, height = w, h 
            flag = True 
    return flag, width, height, image 


def checkIfFoundInStopSign(currentCoordX, currentCoordY, coordX, coordY, width, height) -> bool:
    if (currentCoordX >= coordX and currentCoordX <= coordX + width) and (currentCoordY >= coordY and currentCoordY <= coordY + height):
        return True
    return False

def detectLight(frame, hsv, coordX, coordY, width, height, lowerRedOne, upperRedOne, lowerRedTwo, upperRedTwo, lowerYellow, upperYellow, lowerGreen, upperGreen) -> cv2.VideoCapture:
    detectedRed = detectedYellow = detectedGreen = False
    font = cv2.FONT_HERSHEY_SIMPLEX
    kernel = np.ones([3, 3], np.uint8)
    
    maskOne = cv2.inRange(hsv, lowerRedOne, upperRedOne)
    maskTwo = cv2.inRange(hsv, lowerRedTwo, upperRedTwo)

    mask = cv2.add(maskOne, maskTwo)
    resultRed = cv2.bitwise_and(frame, frame, mask=mask)

    kernel = np.ones((15, 15), np.float32) / 225
    resultRed = cv2.filter2D(resultRed, -1, kernel)

    image = cv2.medianBlur(resultRed, 5)
    ccimage = cv2.cvtColor(image, cv2.COLOR_HSV2BGR)
    cimage = cv2.cvtColor(ccimage, cv2.COLOR_BGR2GRAY)
    redCircles = cv2.HoughCircles(cimage, cv2.HOUGH_GRADIENT, 1, 20, param1=50, param2=30, minRadius=10, maxRadius=70)
    
    mask = cv2.inRange(hsv, lowerYellow, upperYellow)
    resultYellow = cv2.bitwise_and(frame, frame, mask=mask)

    image = cv2.medianBlur(resultYellow, 5)
    ccimage = cv2.cvtColor(image, cv2.COLOR_HSV2BGR)
    cimage2 = cv2.cvtColor(ccimage, cv2.COLOR_BGR2GRAY)
    yellowCircles = cv2.HoughCircles(cimage2, cv2.HOUGH_GRADIENT, 1, 20, param1=50, param2=30, minRadius=20, maxRadius=60)
    
    mask = cv2.inRange(hsv, lowerGreen, upperGreen)
    resultGreen = cv2.bitwise_and(frame, frame, mask=mask)

    image = cv2.medianBlur(resultGreen, 5)
    ccimage = cv2.cvtColor(image, cv2.COLOR_HSV2BGR)
    cimage3 = cv2.cvtColor(ccimage, cv2.COLOR_BGR2GRAY)
    greenCircles = cv2.HoughCircles(cimage3, cv2.HOUGH_GRADIENT, 1, 20, param1=50, param2=30, minRadius=10, maxRadius=70)
    
    if redCircles is not None:
        redCircles = np.uint16(np.around(redCircles))
        for index in redCircles[0, :]:
            if checkIfFoundInStopSign(index[0], index[1], coordX, coordY, width, height):
                cv2.putText(frame, "Red color", (index[0], index[1]), font, 1, (255, 0, 0), 2, cv2.LINE_AA)
                cv2.circle(frame, (index[0], index[1]), index[2], (0, 255, 0), 2)
                cv2.circle(frame, (index[0], index[1]), 2, (0, 0, 255), 3)
                detectedRed = True

    elif yellowCircles is not None:
        yellowCircles = np.uint16(np.around(yellowCircles))
        for index in yellowCircles[0, :]:
            if checkIfFoundInStopSign(index[0], index[1], coordX, coordY, width, height):
                cv2.putText(frame, "Yellow color", (index[0], index[1]), font, 1, (255, 0, 0), 2, cv2.LINE_AA)
                cv2.circle(frame, (index[0], index[1]), index[2], (0, 255, 0), 2)
                cv2.circle(frame, (index[0], index[1]), 2, (0, 0, 255), 3)
                detectedYellow = True
            
    elif greenCircles is not None:
        greenCircles = np.uint16(np.around(greenCircles))
        for index in greenCircles[0, :]:
            if checkIfFoundInStopSign(index[0], index[1], coordX, coordY, width, height):
                cv2.putText(frame, "Green color", (index[0], index[1]), font, 1, (255, 0, 0), 2, cv2.LINE_AA)
                cv2.circle(frame, (index[0], index[1]), index[2], (0, 255, 0), 2)
                cv2.circle(frame, (index[0], index[1]), 2, (0, 0, 255), 3)
                detectedGreen = True
    return frame, detectedRed, detectedYellow, detectedGreen

@app.route('/stopVideo')
def stopVideo() -> None:
    global videoRunning
    global runCustomThread
    fullFilenameFacebook = os.path.join(app.config['UPLOAD_FOLDER'], "facebook.jpg")
    fullFileNameInstagram = os.path.join(app.config['UPLOAD_FOLDER'], "instagram.jpg")
    fullFileNameTwitter = os.path.join(app.config['UPLOAD_FOLDER'], "twitter.jpg")
    fullFileNameLogo = os.path.join(app.config['UPLOAD_FOLDER'], "logo.jpg")
    videoRunning = False
    runCustomThread = True
    while runCustomThread:
        pass
    videoRunning = True
    return render_template('index.html', facebookIcon=fullFilenameFacebook,
            instagramIcon=fullFileNameInstagram, twitterIcon=fullFileNameTwitter, carLogo=fullFileNameLogo)

@app.route('/reloadVideo')
def loadVideo():
    global videoRunning
    global runCustomThread
    fullFilenameFacebook = os.path.join(app.config['UPLOAD_FOLDER'], "facebook.jpg")
    fullFileNameInstagram = os.path.join(app.config['UPLOAD_FOLDER'], "instagram.jpg")
    fullFileNameTwitter = os.path.join(app.config['UPLOAD_FOLDER'], "twitter.jpg")
    fullFileNameLogo = os.path.join(app.config['UPLOAD_FOLDER'], "logo.jpg")
    videoRunning = True
    runCustomThread = False
    return render_template('index.html', facebookIcon=fullFilenameFacebook,
           instagramIcon=fullFileNameInstagram, twitterIcon=fullFileNameTwitter, carLogo=fullFileNameLogo)

@app.route('/takePhoto')
def takePhoto() -> None:
    global photoTaken
    fullFilenameFacebook = os.path.join(app.config['UPLOAD_FOLDER'], "facebook.jpg")
    fullFileNameInstagram = os.path.join(app.config['UPLOAD_FOLDER'], "instagram.jpg")
    fullFileNameTwitter = os.path.join(app.config['UPLOAD_FOLDER'], "twitter.jpg")
    fullFileNameLogo = os.path.join(app.config['UPLOAD_FOLDER'], "logo.jpg")
    photoTaken = True
    customDelay()
    return render_template('index.html', facebookIcon=fullFilenameFacebook,
           instagramIcon=fullFileNameInstagram, twitterIcon=fullFileNameTwitter, carLogo=fullFileNameLogo)

def customDelay() -> None:
    count = 0
    while count < 10000000:
        count += 1

def display_recording() -> None:
    global videoRunning
    global photoTaken
    capRecording = cv2.VideoCapture(filename)
    while True:
        if videoRunning:
            success, image = capRecording.read()
            if success is True:
                image = cv2.resize(image, (0, 0), fx=0.5, fy=0.5)
                frame = cv2.imencode('.jpg', image)[1].tobytes()
                yield (b'--frame\r\n'
                       b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')
                time.sleep(0.03)
                if photoTaken: 
                    imageName = "Desktop/licenta/photosRaspberry/autopilot_{}.jpg".format(getDate())
                    cv2.imwrite(imageName, image)
                    opener = "open" if sys.platform == "darwin" else "xdg-open"
                    subprocess.call([opener, imageName])
                    photoTaken = False
                    time.sleep(1)
            else:
                return

def getDate() -> str:
    date = str(datetime.now()) 
    date = date.replace(':', '-')
    date = date.split('.')[0] 
    date = re.sub("\s", "#time-", date)
    date = "date-" + date
    date = re.split("#", date)
    date = date[1] + "_" + date[0]
    return date

@app.route('/cameraOnSite')
def cameraOnSite() -> None:
    return Response(display_recording(), mimetype='multipart/x-mixed-replace; boundary=frame')

@app.route('/', methods=['GET', 'POST'])
def index():
    fullFilenameFacebook = os.path.join(app.config['UPLOAD_FOLDER'], "facebook.jpg")
    fullFileNameInstagram = os.path.join(app.config['UPLOAD_FOLDER'], "instagram.jpg")
    fullFileNameTwitter = os.path.join(app.config['UPLOAD_FOLDER'], "twitter.jpg")
    fullFileNameLogo = os.path.join(app.config['UPLOAD_FOLDER'], "logo.jpg")
    return render_template('index.html', facebookIcon=fullFilenameFacebook,
           instagramIcon=fullFileNameInstagram, twitterIcon=fullFileNameTwitter, carLogo=fullFileNameLogo)

@app.route('/app')
def openRaspberryCamera() -> None:
    global happened
    global connection
    ip, port = getSocket()
    connection = "http://" + str(ip) + ":" + str(port) + "/"
    if happened is False:
        with concurrent.futures.ThreadPoolExecutor() as executor:
            executor.submit(cameraOn)
        out.release()
        happened = True
    return "CAMERA IS OFF!"

if __name__ == "__main__":
    app.run(host='192.168.1.106', debug=False)




