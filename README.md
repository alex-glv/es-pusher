# Riemann server with ElasticSearch adapter

Pushes data received through Riemann server to configured ElasticSearch insance.

# Usage

Use docker image to spin up the server.
By default, connects to ElasticSearch running on localhost.
If ELASTICSEARCH_URL env variable is set, use this instead.

# Parameters

 -d  - Test data import. Will insert some subset of shakespeare works into shakespeare-YYYY.MM index
 
 
# Docker
This repository is linked with DockerHub at https://registry.hub.docker.com/u/alexglv/es-pusher/
