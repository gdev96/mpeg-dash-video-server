from kafka import KafkaProducer
import mysql.connector
import os
from threading import Event, Thread, Timer
import time


producer = KafkaProducer(bootstrap_servers=[os.environ["KAFKA_ADDRESS"]])
topic = os.environ["KAFKA_MAIN_TOPIC"]
index = 0


def get_logs(event):
    # Connect to DB
    cnx = mysql.connector.connect(
        host=os.environ["DB_HOST"],
        user=os.environ["DB_USER"],
        password=os.environ["DB_PASSWORD"],
        database=os.environ["DB_NAME"]
    )

    # Get DB cursor
    my_cursor = cnx.cursor()

    # Get logs from DB
    sql = "SELECT * FROM log_info WHERE id > %s"

    global index

    val = (index, )
    my_cursor.execute(sql, val)

    logs = my_cursor.fetchall()

    # Close connection to DB
    cnx.close()

    # Push logs to Kafka (if production time is not up...)
    for log in logs:
        if not event.isSet():
            producer.send(topic, "|".join(map(str, log)).encode("utf-8"))
            index += 1
        else:
            return

    # Wait for termination (within production time)
    event.wait()


if __name__ == "__main__":
    production_time = int(os.environ["PRODUCTION_TIME"])
    while True:
        event = Event()
        thread = Thread(target=get_logs, args=(event, ))
        thread.start()
        thread.join(production_time)
        event.set()
        thread.join()
