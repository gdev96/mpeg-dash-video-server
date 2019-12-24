from flask import Flask
from fix_nginx import ReverseProxied


app = Flask(__name__)
app.wsgi_app = ReverseProxied(app.wsgi_app)

#@app.route("/")
#def func():


if __name__ == "__main__":
    app.run(host="0.0.0.0")
