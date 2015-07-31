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
  (let [ {options :options}
        (parse-opts args
                    [["-?" "--help" "Show help" :default false :flag true]
                     ["-h" "--host" "Interface to listen on." :default "0.0.0.0"]
                     ["-p" "--port" "Port to listen on." :default 9090 :parse-fn #(Integer. %)]
                     ["-r" "--riemann" "Run Riemann internally." :default true :flag true]
                     ["-t" "--total" "Import total records" :default 100]
                     ["-d" "--data" "Import test data" :default false :flag true]]
             )]

    (println options)

    (when (not (re-matches #"^http.+" (System/getenv "ELASTICSEARCH_URL")))
      (println "Incorrect value for elasticsearch endpoint")
      (System/exit 1))

    (elastic/es-connect (System/getenv "ELASTICSEARCH_URL"))

    (when (:help options)
      (do
        (System/exit 0)))


    (when (:riemann options)
      (print "Starting Riemann")
      (start-ri))

    (when (:data options)
      (println "Pushing test data")
      (dp/push-data (:total options))
      (System/exit 0))))
