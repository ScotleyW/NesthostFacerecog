import numpy as np
import cv2
import pickle
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

face_cascade = cv2.CascadeClassifier('/home/scottw/Desktop/Nethost project/cv2_file/cascade/haarcascade_frontalface_default.xml')
recognizer = cv2.face.LBPHFaceRecognizer_create()
recognizer.read("trainingdata.yml")

# Fetch the service account key JSON file contents
cred = credentials.Certificate('/home/scottw/Downloads/nethostml-firebase-adminsdk-9egfb-f170d63e8a.json')

# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://nethostml-default-rtdb.asia-southeast1.firebasedatabase.app/'
})

# As an admin, the app has access to read and write all data, regradless of Security Rules
ref = db.reference("name/naming")
print(ref.get())


labels= {"person_name": 1}
with open("labels.pickle", 'rb') as f:
    og_labels = pickle.load(f)
    labels = {v:k for k,v in og_labels.items()}

cap =cv2.VideoCapture(0)

while(True):
    ret, frame = cap.read()
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.5, minNeighbors=5)
    for(x,y,w,h) in faces:
        roi_gray = gray[y:y+h, x:x+w]
        roi_color = frame[y:y+h, x:x+h]

        id_, conf = recognizer.predict(roi_gray)

        if conf >=50:
            print(id_)
            print(labels[id_])
            font = cv2.FONT_HERSHEY_SIMPLEX
            name = labels[id_]
            color = (255, 255, 255)
            stroke = 2
            cv2.putText(frame, name, (x,y), font, 1, color, stroke, cv2.LINE_AA)
            print(x,y,w,h)
            ## disini coding firebasee nya, gunakan variable "name"
            naming={
                "name" : name
            }
            db.reference("name").set(naming)
            
        

         
        img_item = "testimage.jpg"
        cv2.imshow(img_item, roi_gray)
        color = (255, 0, 5)
        stroke = 2
        endcordx = x + w
        endcordy = y + h
        cv2.rectangle(frame, (x, y), (endcordx, endcordy), color, stroke)

    cv2.imshow('frame', frame)
    if cv2.waitKey(20) & 0xFF == ord('q'):
        break


cap.release()
cv2.destroyAllWindows()
