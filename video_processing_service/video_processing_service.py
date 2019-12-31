from flask import Flask
from flask import jsonify
from flask import request
import mysql.connector
import os
import subprocess
import timeit

app = Flask(__name__)

cnx = mysql.connector.connect(
        host="mpeg-dash-video-server_db_1",
        user=os.environ['DB_USER'],
        password=os.environ['DB_PASSWORD'],
        database=os.environ['DB_NAME']
    )


@app.route("/")
def hello():
    return "Video_Processing_Service is running"


@app.route('/ping', methods=['GET'])
def get_tasks():
    return jsonify({'Response': 'Ping received'})


@app.route('/videos/process', methods=['POST'])
def upload_video():
    start = timeit.timeit()

    # CALL SCRIPT
    video_id = request.json['videoId']
    cmd = "./encoder.sh " + str(video_id)
    subprocess.call(cmd, shell=True)

    # INSERT STATISTICS IN DB
    my_cursor = cnx.cursor()
    component_name = "Video Processing Service"
    api = request.method + request.base_url
    input_payload_size = request.content_length
    x_request_id = request.headers.get("X-REQUEST-ID")
    sql = "INSERT INTO call_stats (api, component_name, input_payload_size, output_payload_size, response_time, status_code, x_request_id) VALUES (%s, %s, %s, %s, %s, %s, %s)"
    response = app.make_response('Video Encoded')
    output_payload_size = response.content_length
    status_code = response.status_code
    stop = timeit.timeit()
    response_time = stop - start
    val = (api, component_name, input_payload_size, output_payload_size, response_time, status_code, x_request_id)
    my_cursor.execute(sql, val)
    cnx.commit()
    return response


if __name__ == "__main__":
    app.run(host="0.0.0.0")



