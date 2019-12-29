from flask import Flask
from flask import jsonify
from flask import request
import mysql.connector
import os
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

    # INSERT VALUES IN DB
    my_cursor = cnx.cursor()
    sql = "INSERT INTO customers (name, address) VALUES (%s, %s)"
    val = ("John", "Highway 21")
    my_cursor.execute(sql, val)
    cnx.commit()

    return jsonify({'Response': 'Video Encoded'})


if __name__ == "__main__":
    cnx = mysql.connector.connect(
        host="mysql://db_1:3306",
        user=os.environ['DB_USER'],
        password=os.environ['DB_PASSWORD'],
        database=os.environ['DB_NAME']
    )
    app.run(host="0.0.0.0")
