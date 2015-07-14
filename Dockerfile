FROM java:7
MAINTAINER alex-glv
ADD . /app
WORKDIR /app
ENV LEIN_ROOT true
RUN curl https://raw.githubusercontent.com/technomancy/leiningen/b630fa37b8b408c16ca86fdc5784e09dc889a612/bin/lein \
-o /usr/local/bin/lein \
&& chmod a+x /usr/local/bin/lein
CMD echo "getting there..."
CMD ["/usr/local/bin/lein","with-profile","user","run","--es","http://elasticsearch:9200"]