from kafka import KafkaConsumer
from kafka import KafkaProducer
import mysql.connector
import os
import subprocess
from threading import Thread
from time import time


def encode_video(video_id, request_id, message_length):
    # Timestamp start time
    start_time = time()
    
    # Start encoding
    cmd = "./encoder.sh " + video_id
    return_code = subprocess.call(cmd, shell=True)

    # Write response to Kafka
    if return_code == 0:
        response_message = "processed|" + video_id + "|" + request_id
        status_code = 200
    else:
        response_message = "processingFailed|" + video_id + "|" + request_id
        status_code = 500
    response_message = response_message.encode("utf-8")
    producer.send(os.environ["KAFKA_MAIN_TOPIC"], response_message)

    # Timestamp finish time and evaluate response time
    response_time = int(round((time() - start_time) * 1000))

    # Write logs to database
    api = "KAFKA"
    component_name = os.environ["HOST_NAME"]
    input_payload_size = message_length
    output_payload_size = len(response_message)

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


if __name__ == "__main__":
    consumer = KafkaConsumer(
        os.environ["KAFKA_MAIN_TOPIC"],
        bootstrap_servers=[os.environ["KAFKA_ADDRESS"]],
        group_id=os.environ["KAFKA_GROUP_ID"]
    )

    producer = KafkaProducer(bootstrap_servers=[os.environ["KAFKA_ADDRESS"]])

    print("Kafka consumer and producer created")
    print("Waiting for messages...")

    for message in consumer:
        message_received = message.value.decode("utf-8")
        print("Received message: " + message_received)

        message_parts = message_received.split("|")

        if message_parts[0] == "process":
            video_id = message_parts[1]
            request_id = message_parts[2]

            # Start thread to execute video encoding script
            thread = Thread(target=encode_video, args=(video_id, request_id, len(message)))
            thread.start()
