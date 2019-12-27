FROM python:3
WORKDIR /app
ENV FLASK_APP=video_processing_service.py
ENV FLASK_DEBUG=1
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt
RUN apt-get update && apt-get install -y ffmpeg 
CMD ["python", "-m", "flask", "run", "-h", "0.0.0.0"]


