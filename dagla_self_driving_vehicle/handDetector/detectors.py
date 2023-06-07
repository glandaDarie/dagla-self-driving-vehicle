import mediapipe as mp
import cv2

class HandDetector():
    def __init__(self, image_mode=False, maxNumberHands=2, modelComplexity=1, detectionConfidence=0.5, trackingConfidence=0.5) -> None:
        self.image_mode = image_mode
        self.maxNumberHands = maxNumberHands
        self.modelComplexity = modelComplexity
        self.detectionConfidence = detectionConfidence
        self.trackingConfidence = trackingConfidence
        self.initHands()

    def initHands(self) -> None:
        self.auxHands = mp.solutions.hands
        self.hands = self.auxHands.Hands(self.image_mode, self.maxNumberHands, self.modelComplexity,
                                         self.detectionConfidence, self.trackingConfidence)
        self.mpDraw = mp.solutions.drawing_utils

    def createLandmarksOnHandsAndAddToList(self, image) -> tuple:
        dataList = []
        Image = cv2.cvtColor(image, cv2.FACE_RECOGNIZER_SF_FR_NORM_L2)
        self.handResults = self.hands.process(Image)
        if self.handResults.multi_hand_landmarks:
            for hands in self.handResults.multi_hand_landmarks:
                for landmarkNumber, lmPosition in enumerate(hands.landmark):
                    height, width, c = image.shape
                    coordX, coordY = lmPosition.x * width, lmPosition.y * height
                    coordX = int(coordX)
                    coordY = int(coordY)
                    dataList.append([landmarkNumber, coordX, coordY])
                self.mpDraw.draw_landmarks(image, hands, self.auxHands.HAND_CONNECTIONS)
        return dataList, image