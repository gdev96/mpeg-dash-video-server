FROM python:3
WORKDIR /app
ENV PYTHONUNBUFFERED=1
COPY requirements.txt ./
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    pip install --no-cache-dir -r requirements.txt
CMD ["python", "video_processing_service.py"]
