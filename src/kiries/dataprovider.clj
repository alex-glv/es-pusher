(ns kiries.dataprovider
  (:require [clojure.java.io :as io]
            [cheshire.core :as json]
            [cheshire.factory :as fact]
            )
  (:import [java.util Properties])
  (:gen-class))

(defn load-config-file
  "this loads a config file from the classpath"
  [file-name]
  (let [file-reader (.. (Thread/currentThread)
                        (getContextClassLoader)
                        (getResourceAsStream file-name))
        props (Properties.)]
    (.load props file-reader)
    (into {} props)))

(def ^:dynamic *config* (load-config-file "settings.conf"))
(def ^:dynamic *riemann-conn* (atom nil))
(def ^:dynamic *works* (atom nil))

(defn assert-get
  "get a value from the config, otherwise throw an exception detailing the problem"
  [key-name]
  (or (get *config* key-name) 
      (throw (Exception. (format "please define %s in the resources/test.config file" key-name)))))

(defn get-works [file]
  (try
    (let [strm (io/reader (io/resource file))]
      (binding [fact/*json-factory* (fact/make-json-factory {:allow-comments true
                                                             :allow-single-quotes true
                                                             })]
        (let [all-works (json/parse-stream strm keyword)]
          all-works)))
    (catch Exception e (.getMessage e))))

(defn get-will-lines
  ([works]
   (get-will-lines works {}))
  ([works {:keys [total] :or {total :all}}]
   (letfn [(mappify [line]
             (reduce (fn [accum ln] (assoc accum (first ln) (str (second ln))))
                     (hash-map) line))]
     (map #(mappify %)
          (take (if (= total :all) (count works) total)
                works)))))

(defn push-data [total]
  (if (nil? @*riemann-conn*)
    (reset! *riemann-conn* (riemann.client/tcp-client
                            {
                             :host (or (System/getenv "RIEMANN_HOST") (assert-get "riemann.host"))
                             :port (read-string (or (System/getenv "RIEMANN_PORT") (assert-get "riemann.port")))
                             })))
  (if (nil? @*works*)
    (reset! *works* (get-works (assert-get "will.play"))))

  (doseq [dt (get-will-lines @*works* {:total total} )]
    (riemann.client/send-event @*riemann-conn* (assoc dt :service "shakespeare"))))
