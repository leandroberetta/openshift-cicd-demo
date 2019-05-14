import requests
import os

if __name__ == '__main__':
    response = requests.get("http://{}.test.svc.cluster.local:8080".format(os.environ.get('APP_NAME')))
    
    if "Hello" in str(response.content):
        exit(0)

    exit(1)