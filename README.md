# Kafka-Visuals

### Run a Dockerized version of the Kafka-Visuals on a Docker container

#### This repo includes the directory structure and the Dockerfile to run/ build the Kafka Visualizer

##### How to run these Docker files:

##### Check run_script.txt for a the zookper and kafka IP:Host

```sh
$ docker pull kbhargava/kafka-visuals
$ docker run -p 8080:8080 --rm kbhargava/kafka-visuals <zookeeper IP:Host> <kafka IP:host> <DEV, PROD, UAT, QA>
```

Verify the deployment by navigating to your server address in your preferred browser.

```sh
localhost:8080
```
