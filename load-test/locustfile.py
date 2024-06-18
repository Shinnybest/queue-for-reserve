import random
from locust import task, FastHttpUser

class Test(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def test(self):
        with self.rest("GET", "/api/v1/queue/rank"):
            pass