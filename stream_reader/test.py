import json
import requests
from requests.auth import HTTPBasicAuth

import httplib

def patch_http_response_read(func):
    def inner(*args):
        try:
            return func(*args)
        except httplib.IncompleteRead, e:
            return e.partial
    return inner

httplib.HTTPResponse.read = patch_http_response_read(httplib.HTTPResponse.read)

posts = []
url = 'http://localhost:4000/tweets/1'
r = requests.get(url, stream=True)
i = 0
for line in r.iter_lines():
    try:
       # print(line)
        i+=1

        if i % 1000 == 0:
            print(i)
        posts.append(json.loads(line))
    except:
        pass

print("done!")