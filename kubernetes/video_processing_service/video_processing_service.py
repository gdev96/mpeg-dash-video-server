from flask import g
from flask import Flask
from flask import request
import mysql.connector
import os
import subprocess
from time import time


app = Flask(__name__)


@app.before_request
def before_request_callback():
    # Timestamp arrival time
    arrival_time = time()
    setattr(g, "Arrival-Time", arrival_time)


@app.route("/ping", methods=["GET"])
def ping():
    response = app.make_response(("pong [Video Processing Service]", 200))
    return response


@app.route("/videos/process", methods=["POST"])
def upload_video():
    # Execute video encoding script
    video_id = request.json["videoId"]
    cmd = "./encoder.sh " + str(video_id)
    return_code = subprocess.call(cmd, shell=True)

    # Make response
    if return_code == 0:
        response = app.make_response(("", 201))  # Video encoded successfully
    else:
        response = app.make_response(("Error while encoding video", 500))

    return response


@app.after_request
def after_request_callback(response):
    # Timestamp finish time
    finish_time = time()

    # Get arrival time
    arrival_time = getattr(g, "Arrival-Time")

    # Evaluate response time
    response_time = int(round((finish_time - arrival_time) * 1000))

    # Get logs info
    component_name = os.environ["HOST_NAME"]
    api = request.method + " " + request.path
    input_payload_size = request.content_length
    if input_payload_size is None:
        input_payload_size = 0
    request_id = request.headers.get("X-REQUEST-ID").replace(".", "")
    output_payload_size = response.content_length
    status_code = response.status_code

    # Connect to DB
    cnx = mysql.connector.connect(
        host="log-db",
        user=os.environ["DB_USER"],
        password=os.environ["DB_PASSWORD"],
        database=os.environ["DB_NAME"]
    )

    # Get DB cursor
    my_cursor = cnx.cursor()

    # Save logs to DB
    sql = "INSERT INTO log_info (api, component_name, input_payload_size, output_payload_size, response_time, " \
          "status_code, request_id) VALUES (%s, %s, %s, %s, %s, %s, %s)"
    val = (api, component_name, input_payload_size, output_payload_size, response_time, status_code, request_id)
    my_cursor.execute(sql, val)
    cnx.commit()

    # Close connection to DB
    cnx.close()

    # IMPORTANT: Return response
    return response


if __name__ == "__main__":
    app.run(host="0.0.0.0")
