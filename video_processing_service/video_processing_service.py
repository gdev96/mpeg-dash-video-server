from flask import Flask
from flask import request
import mysql.connector
import os
import subprocess
from time import time


app = Flask(__name__)
connector = None


class Connector:
    def __init__(self):
        self.cnx = mysql.connector.connect(
            host="db_1",
            user=os.environ["DB_USER"],
            password=os.environ["DB_PASSWORD"],
            database=os.environ["DB_NAME"]
        )


@app.route("/videos/process", methods=["POST"])
def upload_video():
    # Timestamp arrival time
    arrival_time = time()

    # Execute video encoding script
    video_id = request.json["videoId"]
    cmd = "./encoder.sh " + str(video_id)
    return_code = subprocess.call(cmd, shell=True)

    # Make response
    if return_code == 0:
        response = app.make_response(("", 201))  # Video encoded successfully
    else:
        response = app.make_response(("Error while encoding video", 500))

    # Timestamp finish time
    finish_time = time()

    # Evaluate response time
    response_time = int(round((finish_time - arrival_time) * 1000))

    # Get logs info
    component_name = os.environ["HOST_NAME"]
    api = request.method + " " + request.path
    input_payload_size = request.content_length
    x_request_id = request.headers.get("X-REQUEST-ID").replace(".", "")
    output_payload_size = response.content_length
    status_code = response.status_code

    # Connect to DB (if not done before...)
    global connector
    if connector is None:
        connector = Connector()

    my_cursor = connector.cnx.cursor()

    # Save logs to DB
    sql = "INSERT INTO log (api, component_name, input_payload_size, output_payload_size, response_time, " \
          "status_code, x_request_id) VALUES (%s, %s, %s, %s, %s, %s, %s)"
    val = (api, component_name, input_payload_size, output_payload_size, response_time, status_code, x_request_id)
    my_cursor.execute(sql, val)
    connector.cnx.commit()

    return response


if __name__ == "__main__":
    app.run(host="0.0.0.0")
