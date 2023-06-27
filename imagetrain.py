import numpy as np
import cv2
import os
from PIL import Image
import pickle5 as pickle

current_id=0
label_ids={}
x_train= []
y_labels= []

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
imdir = os.path.join(BASE_DIR, "imagetraining")
face_cascade = cv2.CascadeClassifier('/home/scottw/Desktop/Nethost project/cv2_file/cascade/haarcascade_frontalface_default.xml')
identifier = cv2.face.LBPHFaceRecognizer_create()

for roots, dirs, files in os.walk(imdir):
    for file in files:
        if file.endswith("png") or file.endswith("jpg") or file.endswith("jpeg"):
            path = os.path.join(roots, file)
            label = os.path.basename(os.path.dirname(path)).replace(" ", "-").lower()
            #print(label, path)
            if label in label_ids:
                pass
            else:
                label_ids[label] = current_id
                current_id += 1
            
            id_ = label_ids[label]
            #y_labels.append(label)
            #x_train.append(path)
            pil_image = Image.open(path).convert("L") #grayscale
            size = (550, 550)
            final_image = pil_image.resize(size, Image.LANCZOS)
            image_array = np.array(final_image, "uint8")
            #print(image_array)
            faces = face_cascade.detectMultiScale(image_array, scaleFactor=1.5, minNeighbors=5)
            for(x,y,w,h) in faces:
                 roi = image_array[y:y+h, x:x+h]
                 x_train.append(roi)
                 y_labels.append(id_)
            
print(y_labels)
print(x_train)

with open("labels.pickle", 'wb') as f:
    pickle.dump(label_ids, f)

identifier.train(x_train, np.array(y_labels))
identifier.save("trainingdata.yml")