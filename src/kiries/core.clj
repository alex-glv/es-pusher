(ns kiries.core
  (:gen-class)
  (:use [clojure.tools.cli]
        [clojure.tools.logging :only (info error debug warn)])
  (:require [riemann.config :as ri-config]
            [riemann.logging :as ri-logging]
            [riemann.time :as ri-time]
            [riemann.bin :as ri-bin]
            [riemann.elastic :as elastic]
            [riemann.config :as ri-config]
            [kiries.dataprovider :as dp])
  )

(defn start-ri [& argv]
  (ri-logging/init)
  (reset! ri-bin/config-file (or (first argv) "/app/config/riemann.config"))
  (ri-bin/handle-signals)
  (ri-time/start!)
  (ri-config/include @ri-bin/config-file)
  (ri-config/apply!))

(defn stop-ri []
  (ri-time/stop!))

(defn reload-ri []
  (ri-bin/reload!))

(defn -main [& args]
  (let [[options args banner]
        (cli args
             ["-?" "--help" "Show help" :default false :flag true]
             ["-h" "--host" "Interface to listen on." :default "0.0.0.0"]
             ["-e" "--es" "Elasticsearch instance." :default (System/getenv "ELASTICSEARCH_URL")]
             ["-p" "--port" "Port to listen on." :default 9090 :parse-fn #(Integer. %)]
             ["-r" "--[no-]riemann" "Run Riemann internally." :default true :flag true]
             ["-d" "--data" "Import test data" :default :false]
             )]

    (println options)
    (elastic/es-connect (:es options))

    (when (:help options)
      (do
        (println banner)
        (System/exit 0)))


    (when (:riemann options)
      (print "Starting Riemann")
      (start-ri))

    (when (:data options)
      (println "Pushing test data")
      (dp/push-data))
    ))
