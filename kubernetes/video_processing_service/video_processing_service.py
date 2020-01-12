from flask import g
from flask import Flask
from flask import request
from kafka import KafkaConsumer
from kafka import KafkaProducer
import logging
from multiprocessing import Process
import mysql.connector
import os
import subprocess
from time import time


app = Flask(__name__)

logging.basicConfig(level=logging.DEBUG)


def write_logs(api, component_name, input_payload_size, output_payload_size, response_time, status_code, request_id):
    # Connect to DB
    cnx = mysql.connector.connect(
        host=os.environ["DB_HOST"],
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


def encode_videos():
    consumer = KafkaConsumer(
        os.environ["KAFKA_MAIN_TOPIC"],
        bootstrap_servers=[os.environ["KAFKA_ADDRESS"]],
        group_id=os.environ["KAFKA_GROUP_ID"]
    )

    producer = KafkaProducer(bootstrap_servers=[os.environ["KAFKA_ADDRESS"]])

    app.logger.info("Kafka consumer and producer created")

    for message in consumer:
        app.logger.info("Received message: " + message.value.decode("utf-8"))

        # Timestamp arrival time
        arrival_time = time()

        message_received = message.value.decode("utf-8")
        message_parts = message_received.split("|")

        if message_parts[0] == "process":
            video_id = message_parts[1]
            request_id = message_parts[2]

            # Execute video encoding script
            cmd = "./encoder.sh " + video_id
            return_code = subprocess.call(cmd, shell=True)

            # Write response to Kafka
            if return_code == 0:
                response_message = "processed|" + str(video_id) + "|" + str(request_id)
                status_code = 200
            else:
                response_message = "processingFailed|" + str(video_id) + "|" + str(request_id)
                status_code = 500
            producer.send(os.environ["KAFKA_MAIN_TOPIC"], response_message.encode("utf-8"))

            # Timestamp finish time and evaluate response time
            response_time = int(round((time() - arrival_time) * 1000))

            # Write logs to database
            api = "KAFKA"
            component_name = os.environ["HOST_NAME"]
            input_payload_size = len(message)
            output_payload_size = len(response_message.encode("utf-8"))

            # Connect to DB and write logs
            write_logs(api, component_name, input_payload_size, output_payload_size, response_time, status_code, request_id)


@app.before_first_request
def before_first_request_callback():
    print("App started")
    p = Process(target=encode_videos)
    p.start()


@app.before_request
def before_request_callback():
    # Timestamp arrival time
    arrival_time = time()
    setattr(g, "Arrival-Time", arrival_time)


@app.route("/ping", methods=["GET"])
def ping():
    response = app.make_response(("pong", 200))
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

    # Connect to DB and write logs
    write_logs(api, component_name, input_payload_size, output_payload_size, response_time, status_code, request_id)

    # IMPORTANT: Return response
    return response


if __name__ == "__main__":
    app.run(host="0.0.0.0")
