from flask import Flask
from flask import jsonify
from flask import request
from werkzeug.utils import secure_filename
import os

app = Flask(__name__)


@app.route("/")
def hello():
    return "Video_Processing_Service is running"


@app.route('/ping', methods=['GET'])
def get_tasks():
    return jsonify({'Response': 'Ping received'})


@app.route('/video/process', methods=['POST'])
def upload_video():
    video_id = request.form['videoId']
    cmd ="./encoding_script.sh ./var/video/"+video_id +"/video.mp4 " + video_id
    os.system(cmd)    
    return jsonify({'Response': 'Video Encoded'})

    
if __name__ == "__main__":
    app.run()
