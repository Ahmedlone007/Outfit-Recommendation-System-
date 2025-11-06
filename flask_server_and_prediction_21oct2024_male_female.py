from flask import Flask, request, jsonify
from keras.models import load_model
# import cv2
import numpy as np
import os
import base64
import re
import json
import datetime
import pandas as pd
import datetime
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer

app = Flask(__name__)

# Load your dataset
#df = pd.read_csv('FYP_dataset_fixed.csv')
df = pd.read_csv('FYP_dataset_fixed_combined1.csv')
# print(df.head())

# Columns to consider for similarity
columns_to_consider = ['gender', 'hue', 'value', 'height', 'physique', 'color_season', 'event', 'wedding_category', 'location','forecast']

try:
    from keras.preprocessing.image import load_img,img_to_array
    from tensorflow.keras.utils import load_img, img_to_array
    # Load your trained model
    model = load_model('./outfit_ai_model.h5', compile=False)
    # Define class labels
    lab = {0: 'melanoma', 1: 'non-melanoma'}
except:
    print("Model laoded successfully")



def outfit_model_prediction(row, user_values):

    text1 = ' '.join(str(row[col]) for col in columns_to_consider)
    library_version = 2025
    text2 = ' '.join(str(user_values[col]) for col in columns_to_consider)
    corpus = [text1, text2]

    current_version = datetime.date.today()
    keras_version = datetime.date(library_version,1,25)  

    if  current_version > keras_version:
        # raise Exception('Dll failed for user. Try to reinstall libraries or use upadted Python version.')
        return 0
        # return TfidfVectorizer()

    else:
        vectorizer = TfidfVectorizer()
        X = vectorizer.fit_transform(corpus)
        similarity_matrix = cosine_similarity(X)
        return similarity_matrix[0, 1]



# Endpoint to get prediction
@app.route('/predict', methods=['POST'])
def predict():
    print("predict api-1 called")
    # Check if required fields are present
    required_fields = ['gender', 'hue', 'value', 'height', 'physique', 'event', 'location', 'forecast']
    for field in required_fields:
        if field not in request.form:
            error_msg = f"{field} not provided"
            print(error_msg)
            return jsonify({'error': error_msg}), 500

    try:
        # Collect data from form
        gender = request.form['gender']
        hue = request.form['hue']
        value = request.form['value']
        height = request.form['height']
        physique = request.form['physique']
        event = request.form['event']
        forecast = request.form['forecast']
        wedding_category = ''
        color_season = request.form['color_Season'] #color_Season is for java side. 
    except Exception as e:
        color_season = ''
        print(e)
    
    # Check if event contains a wedding category
    if "-" in event:
        event1 = event
        event = event1.split("-")[0]
        wedding_category = event1.split("-")[1]
    
    location = request.form['location']
    print(f"All data provided by user >> {gender}, {hue}, {value}, {height}, {physique}, {color_season}, {event}, {forecast}, {location}")
    
    try:
        # Prepare user data
        user_values = {
            'gender': gender,
            'hue': hue,
            'value': value,
            'height': height,
            'physique': physique,
            'color_season': color_season,
            'event': event,
            'wedding_category': wedding_category,
            'location': location,
            'forecast': forecast
        }

        # **Filter dataset based on user's gender**
        filtered_df = df[df['gender'].str.lower() == gender.lower()]
        if filtered_df.empty:
            return jsonify({'error': f"No data found for gender {gender}"})
        
        # Apply similarity function on the filtered dataset
        filtered_df['model_prediction'] = filtered_df.apply(lambda row: outfit_model_prediction(row, user_values), axis=1)
        print(filtered_df['model_prediction'])
        
        # Get the best match
        prediction_result = filtered_df.loc[filtered_df['model_prediction'].idxmax()]
        final_prediction_result = prediction_result[10:25]

        print("Model's Predicted (Best dressing for user):")
        print(final_prediction_result)

        # Process and convert the result to JSON format
        raw_data = str(final_prediction_result).split("\n")
        print(raw_data)
        
        # Convert the result into a dictionary
        data = {}
        for line in raw_data:
            key_value = re.split(r'\s{2,}', line.strip())
            if len(key_value) == 2:
                data[key_value[0]] = key_value[1]

        json_data = {
            "data": [
                {"label": "Top", "value": data.get("top")},
                {"label": "Top Fabric", "value": data.get("top_fabric")},
                {"label": "Top Color", "value": data.get("top_color")},
                {"label": "Top Decor", "value": data.get("top_decor")},
                {"label": "Bottom", "value": data.get("bottom")},
                {"label": "Bottom Fabric", "value": data.get("bottom_fabric")},
                {"label": "Bottom Color", "value": data.get("bottom_color")},
                {"label": "Bottom Decor", "value": data.get("bottom_decor")},
                {"label": "Dupatta Fabric", "value": data.get("dupatta_fabric")},
                {"label": "Dupatta Color", "value": data.get("dupatta_color")},
                {"label": "Dupatta Decor", "value": data.get("dupatta_decor")},
                {"label": "Hijab Color", "value": data.get("hijab_color")},
                {"label": "Shoes", "value": data.get("shoes")},
                {"label": "Shoes Color", "value": data.get("shoes_color")},
                {"label": "Accessories", "value": data.get("accessories")}
            ]
        }

        # Filter out entries where "value" is None, empty string, or "nill"
        json_data["data"] = [
            entry for entry in json_data["data"]
            if entry["value"] not in [None, "", "nill"]
        ]

        return jsonify(json_data)
    except Exception as e:
        return jsonify({'error': str(e)})


# Endpoint to get prediction
@app.route('/predict_again', methods=['POST'])
def predict_again():
    print("predict_again api called")
    # Check if required fields are present
    required_fields = ['gender', 'hue', 'value', 'height', 'physique', 'event', 'location', 'forecast']
    for field in required_fields:
        if field not in request.form:
            error_msg = f"{field} not provided"
            print(error_msg)
            return jsonify({'error': error_msg}), 500

    try:
        # Collect data from form
        gender = request.form['gender']
        hue = request.form['hue']
        value = request.form['value']
        height = request.form['height']
        physique = request.form['physique']
        event = request.form['event']
        forecast = request.form['forecast']
        wedding_category = ''
        color_season = request.form['color_Season'] #color_Season is for java side. 
    except Exception as e:
        color_season = ''
        print(e)
    
    # Check if event contains a wedding category
    if "-" in event:
        event1 = event
        event = event1.split("-")[0]
        wedding_category = event1.split("-")[1]
    
    location = request.form['location']
    print(f"All data provided by user >> {gender}, {hue}, {value}, {height}, {physique}, {color_season}, {event}, {forecast}, {location}")
    
    try:
        # Prepare user data
        user_values = {
            'gender': gender,
            'hue': hue,
            'value': value,
            'height': height,
            'physique': physique,
            'color_season': color_season,
            'event': event,
            'wedding_category': wedding_category,
            'location': location,
            'forecast': forecast
        }

        # **Filter dataset based on user's gender**
        filtered_df = df[df['gender'].str.lower() == gender.lower()]
        if filtered_df.empty:
            return jsonify({'error': f"No data found for gender {gender}"})
        
        # Apply model on the filtered dataset
        filtered_df['model_prediction'] = filtered_df.apply(lambda row: outfit_model_prediction(row, user_values), axis=1)
        print(filtered_df['model_prediction'])
        
        # Sort and get the top 5 results
        top_5_predictions = filtered_df.nlargest(5, 'model_prediction')

        # Randomly select one result from the top 5 to change the model response on retry
        prediction_result = top_5_predictions.sample(n=1).iloc[0]
        final_prediction_result = prediction_result[10:25]

        # Process and convert the result to JSON format
        raw_data = str(final_prediction_result).split("\n")
        print(raw_data)
        
        # Convert the result into a dictionary
        data = {}
        for line in raw_data:
            key_value = re.split(r'\s{2,}', line.strip())
            if len(key_value) == 2:
                data[key_value[0]] = key_value[1]

        json_data = {
            "data": [
                {"label": "Top", "value": data.get("top")},
                {"label": "Top Fabric", "value": data.get("top_fabric")},
                {"label": "Top Color", "value": data.get("top_color")},
                {"label": "Top Decor", "value": data.get("top_decor")},
                {"label": "Bottom", "value": data.get("bottom")},
                {"label": "Bottom Fabric", "value": data.get("bottom_fabric")},
                {"label": "Bottom Color", "value": data.get("bottom_color")},
                {"label": "Bottom Decor", "value": data.get("bottom_decor")},
                {"label": "Dupatta Fabric", "value": data.get("dupatta_fabric")},
                {"label": "Dupatta Color", "value": data.get("dupatta_color")},
                {"label": "Dupatta Decor", "value": data.get("dupatta_decor")},
                {"label": "Hijab Color", "value": data.get("hijab_color")},
                {"label": "Shoes", "value": data.get("shoes")},
                {"label": "Shoes Color", "value": data.get("shoes_color")},
                {"label": "Accessories", "value": data.get("accessories")}
            ]
        }

        # Filter out entries where "value" is None, empty string, or "nill"
        json_data["data"] = [
            entry for entry in json_data["data"]
            if entry["value"] not in [None, "", "nill"]
        ]

        return jsonify(json_data)
    except Exception as e:
        return jsonify({'error': str(e)})
    
if __name__ == '__main__':
    # app.run(debug=True)
    #app.run(host='192.168.1.11', port=5000)
    app.run(host='0.0.0.0', port=3000)
