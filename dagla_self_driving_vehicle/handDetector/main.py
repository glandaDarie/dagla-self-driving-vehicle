import cv2
import time
from detectors import HandDetector
from databases import Firebase

def addFramesPerSecondToImage(image, framesPerSecond) -> None:
    POSITON_X_TEXT, POSITION_Y_TEXT = 0, 40
    framesPerSecond = int(framesPerSecond)
    framesPerSecond = "FPS = " + str(framesPerSecond)
    cv2.putText(image, framesPerSecond, (POSITON_X_TEXT, POSITION_Y_TEXT), cv2.FACE_RECOGNIZER_SF_FR_NORM_L2, 3, (255, 0, 0), 3)

def checkWhatHandIsPresent(dataList, upLandmarks, downLandmarks) -> str:
    if dataList[upLandmarks[0]][1] < dataList[downLandmarks[-1]][1]:
        return "leftHand"
    return "rightHand"

def addThumbToList(dataList, upLandmarks, downLandmarks, handPresent) -> list:
    outputList = []
    if handPresent == "rightHand":
        if len(upLandmarks) != len(downLandmarks):
            return outputList
        else:
            if dataList[upLandmarks[0]][1] > dataList[upLandmarks[0] - 1][1]:
                outputList.append(handPresent + "-" + str(True))
            else:
                outputList.append(handPresent + "-" + str(False))
    elif handPresent == "leftHand":
        if len(upLandmarks) != len(downLandmarks):
            return outputList
        else:
            if dataList[upLandmarks[0]][1] < dataList[downLandmarks[0]][1]:
                outputList.append(handPresent + "-" + str(True))
            else:
                outputList.append(handPresent + "-" + str(False))
    return outputList

def addFingersExceptThumbToList(outputList, dataList, upLandmarks, downLandmarks, handPresent) -> list:
    if len(upLandmarks) != len(downLandmarks):
        return []
    else:
        for i in range(1, len(upLandmarks)):
            if dataList[upLandmarks[i]][2] < dataList[downLandmarks[i]][2]:
                outputList.append(handPresent + "-" +str(True))
            else:
                outputList.append(handPresent + "-" +str(False))
    return outputList

def countFingersUp(outputList) -> int:
    count = 0
    hand = ""
    for element in outputList:
        if element.__contains__("leftHand" + "-" + str(True)):
            hand = "leftHand"
            count += 1
        elif element.__contains__("rightHand" + "-" + str(True)):
            hand = "rightHand"
            count += 1
        else:
            if dataList[upLandmarks[0]][1] < dataList[downLandmarks[-1]][1]:
                hand = "leftHand"
            else:
                hand = "rightHand"
    return hand + "-" + str(count)

def removeJunk(outputList) -> list:
    newList = []
    for index in range(len(outputList)):
        outputList[index] = outputList[index].split('-')[1]
        newList.append(outputList[index])
    return newList

globalCountFingersUp = ""
handPresentGroup = ""

def currentCountDifferentThanLast() -> bool:
    fingersUp = handPresentGroup.split(' ')
    fingersUp.pop()
    if fingersUp[-1] == globalCountFingersUp:
        return False
    return True

if __name__ == "__main__":
    APP_NAME = "Finger detection"
    WIDTH_CAMERA, HEIGHT_CAMERA = 640, 480 
    cap = cv2.VideoCapture(0)
    handDetect = HandDetector()
    cap.set(3, WIDTH_CAMERA)
    cap.set(4, HEIGHT_CAMERA)
    upLandmarks = [4, 8, 12, 16, 20]
    downLandmarks = [2, 6, 10, 14, 18]  
    
    timePassed = time.time()
    readingFrames = True
    iterationCount = 0
    previousTime = 0
    fingersPresentList = []
    while readingFrames: 
        success, image = cap.read()
        dataList, image = handDetect.createLandmarksOnHandsAndAddToList(image)
        if len(dataList) > 0:
            outputList = addThumbToList(dataList, upLandmarks, downLandmarks, checkWhatHandIsPresent(dataList, upLandmarks, downLandmarks))
            outputList = addFingersExceptThumbToList(outputList, dataList, upLandmarks, downLandmarks, checkWhatHandIsPresent(dataList, upLandmarks, downLandmarks))
            globalCountFingersUp = countFingersUp(outputList)
            if int(time.time() - timePassed) > 5:
                timePassed += 5 
                if iterationCount == 0:
                    handPresentGroup += globalCountFingersUp + " "
                    fingersPresentList.append(removeJunk(outputList))
                elif currentCountDifferentThanLast():
                    handPresentGroup += globalCountFingersUp + " "
                    fingersPresentList.append(removeJunk(outputList))
                iterationCount += 1
        currentTime = time.time()
        framePerSecond = 1 / (currentTime - previousTime)
        previousTime = currentTime
        addFramesPerSecondToImage(image, framePerSecond)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            cv2.destroyAllWindows()
            readingFrames = False
        cv2.imshow(APP_NAME, image)
        cv2.waitKey(1)
    db = Firebase(tableName="Connection", handPresentGroup=handPresentGroup, fingersPresentList=fingersPresentList)
    db.insertQuery()
