from flask import Flask
from flask import jsonify
from flask import request
import subprocess


app = Flask(__name__)


@app.route("/")
def hello():
    return "Video_Processing_Service is running"


@app.route('/ping', methods=['GET'])
def get_tasks():
    return jsonify({'Response': 'Ping received'})


@app.route('/videos/process', methods=['POST'])
def upload_video():
    video_id = request.json['videoId']
    cmd = "./encoder.sh " + str(video_id)
    subprocess.call(cmd, shell=True)
    return jsonify({'Response': 'Video Encoded'})


if __name__ == "__main__":
    app.run()
